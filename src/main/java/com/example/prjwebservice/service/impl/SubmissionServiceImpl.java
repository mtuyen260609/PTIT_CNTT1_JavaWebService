package com.example.prjwebservice.service.impl;

import com.example.prjwebservice.exception.BadRequestException;
import com.example.prjwebservice.exception.ConflictException;
import com.example.prjwebservice.exception.ResourceNotFoundException;
import com.example.prjwebservice.model.dto.response.SubmissionResponse;
import com.example.prjwebservice.model.entity.Course;
import com.example.prjwebservice.model.entity.Submission;
import com.example.prjwebservice.model.entity.SubmissionStatus;
import com.example.prjwebservice.model.entity.User;
import com.example.prjwebservice.repository.CourseRepository;
import com.example.prjwebservice.repository.SubmissionRepository;
import com.example.prjwebservice.repository.UserRepository;
import com.example.prjwebservice.service.CloudinaryService;
import com.example.prjwebservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public SubmissionResponse submit(Long courseId, MultipartFile file) {
        User student = getCurrentStudent();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học"));

        if (!file.getOriginalFilename().endsWith(".pdf") && !file.getOriginalFilename().endsWith(".doc") && !file.getOriginalFilename().endsWith(".docx")) {
            throw new BadRequestException("Chỉ cho phép định dạng PDF hoặc Word");
        }

        if (file.getSize() > 15 * 1024 * 1024) {
            throw new BadRequestException("Dung lượng file vượt quá 15MB");
        }

        String reportUrl = cloudinaryService.uploadFile(file);

        Submission submission = submissionRepository.findByStudent_IdAndCourse_Id(student.getId(), courseId)
                .orElse(Submission.builder()
                        .student(student)
                        .course(course)
                        .build());

        submission.setReportUrl(reportUrl);
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());

        return toResponse(submissionRepository.save(submission));
    }

    @Override
    @Transactional
    public SubmissionResponse grade(com.example.prjwebservice.model.dto.request.GradeRequest request) {
        User lecturer = getCurrentUser();
        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài nộp"));

        if (submission.getStatus() == SubmissionStatus.PENDING) {
            throw new BadRequestException("Sinh viên chưa nộp bài");
        }

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setLecturer(lecturer);

        return toResponse(submissionRepository.save(submission));
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<SubmissionResponse> findByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId);
        }

        java.util.List<Submission> list = submissionRepository.findByCourse_Id(courseId);
        
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Khóa học này chưa có bài nộp nào");
        }

        return list.stream()
                .map(this::toResponse)
                .toList();
    }

    private User getCurrentStudent() {
        return getCurrentUser();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Chưa xác thực người dùng");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private SubmissionResponse toResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .reportUrl(submission.getReportUrl())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .status(submission.getStatus())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getFullName())
                .courseId(submission.getCourse().getId())
                .courseName(submission.getCourse().getCourseName())
                .lecturerId(submission.getLecturer() != null ? submission.getLecturer().getId() : null)
                .lecturerName(submission.getLecturer() != null ? submission.getLecturer().getFullName() : null)
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
