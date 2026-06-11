package com.example.prjwebservice.service.impl;

import com.example.prjwebservice.exception.ConflictException;
import com.example.prjwebservice.exception.ResourceNotFoundException;
import com.example.prjwebservice.model.dto.response.EnrollmentResponse;
import com.example.prjwebservice.model.entity.Course;
import com.example.prjwebservice.model.entity.Enrollment;
import com.example.prjwebservice.model.entity.EnrollmentStatus;
import com.example.prjwebservice.model.entity.User;
import com.example.prjwebservice.repository.CourseRepository;
import com.example.prjwebservice.repository.EnrollmentRepository;
import com.example.prjwebservice.repository.EnrollmentStatusRepository;
import com.example.prjwebservice.repository.UserRepository;
import com.example.prjwebservice.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentStatusRepository enrollmentStatusRepository;

    @Override
    @Transactional
    public EnrollmentResponse enrollCurrentStudent(Long courseId) {
        User student = getCurrentStudent();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học hoặc lớp học đã bị xóa"));

        enrollmentRepository.findByStudent_IdAndCourse_Id(student.getId(), courseId)
                .ifPresent(existing -> {
                    throw new ConflictException("Sinh viên đã đăng ký lớp học này");
                });

        EnrollmentStatus status = enrollmentStatusRepository.findByName("REGISTERED")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạng thái REGISTERED"));

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(status)
                .enrolledAt(LocalDateTime.now())
                .build();

        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments() {
        User student = getCurrentStudent();
        return enrollmentRepository.findByStudent_Id(student.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private User getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Chưa xác thực người dùng");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFullName())
                .courseId(enrollment.getCourse().getId())
                .courseName(enrollment.getCourse().getCourseName())
                .status(enrollment.getStatus().getName())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }
}
