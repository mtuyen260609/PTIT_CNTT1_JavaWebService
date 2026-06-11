package com.example.prjwebservice.service.impl;

import com.example.prjwebservice.exception.ConflictException;
import com.example.prjwebservice.exception.ResourceNotFoundException;
import com.example.prjwebservice.model.dto.request.CourseUpsertRequest;
import com.example.prjwebservice.model.dto.response.CourseResponse;
import com.example.prjwebservice.model.entity.Course;
import com.example.prjwebservice.model.entity.User;
import com.example.prjwebservice.repository.CourseRepository;
import com.example.prjwebservice.repository.UserRepository;
import com.example.prjwebservice.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> search(String keyword, Pageable pageable) {
        String search = keyword == null ? "" : keyword.trim();
        return courseRepository.findByCourseCodeContainingIgnoreCaseOrCourseNameContainingIgnoreCase(
                search, search, pageable
        ).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getById(Long id) {
        return toResponse(findCourse(id));
    }

    @Override
    @Transactional
    public CourseResponse create(CourseUpsertRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new ConflictException("Mã lớp học đã tồn tại");
        }

        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .description(request.getDescription())
                .credits(request.getCredits())
                .deleted(request.getDeleted() != null && request.getDeleted())
                .createdAt(LocalDateTime.now())
                .lecturer(resolveLecturer(request.getLecturerId()))
                .build();

        return toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, CourseUpsertRequest request) {
        Course course = findCourse(id);

        courseRepository.findByCourseCode(request.getCourseCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Mã lớp học đã tồn tại");
                });

        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());
        if (request.getDeleted() != null) {
            course.setDeleted(request.getDeleted());
        }
        course.setLecturer(resolveLecturer(request.getLecturerId()));

        return toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Course course = findCourse(id);
        courseRepository.delete(course); // This will trigger @SQLDelete
    }

    private User resolveLecturer(Long lecturerId) {
        if (lecturerId == null) {
            return null;
        }
        return userRepository.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));
    }

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học"));
    }

    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .credits(course.getCredits())
                .deleted(course.isDeleted())
                .lecturerId(course.getLecturer() == null ? null : course.getLecturer().getId())
                .lecturerName(course.getLecturer() == null ? null : course.getLecturer().getFullName())
                .build();
    }
}
