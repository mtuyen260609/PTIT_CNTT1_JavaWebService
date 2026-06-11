package com.example.prjwebservice.service.impl;

import com.example.prjwebservice.exception.ResourceNotFoundException;
import com.example.prjwebservice.model.dto.response.LectureMaterialResponse;
import com.example.prjwebservice.model.entity.Course;
import com.example.prjwebservice.model.entity.LectureMaterial;
import com.example.prjwebservice.model.entity.User;
import com.example.prjwebservice.repository.CourseRepository;
import com.example.prjwebservice.repository.LectureMaterialRepository;
import com.example.prjwebservice.repository.UserRepository;
import com.example.prjwebservice.service.CloudinaryService;
import com.example.prjwebservice.service.LectureMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureMaterialServiceImpl implements LectureMaterialService {

    private final LectureMaterialRepository lectureMaterialRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public LectureMaterialResponse upload(Long courseId, String title, MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User lecturer = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        String fileUrl = cloudinaryService.uploadFile(file);

        LectureMaterial material = LectureMaterial.builder()
                .title(title)
                .fileUrl(fileUrl)
                .course(course)
                .lecturer(lecturer)
                .uploadedAt(LocalDateTime.now())
                .build();

        material = lectureMaterialRepository.save(material);

        return toResponse(material);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LectureMaterialResponse> getByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId);
        }

        List<LectureMaterial> list = lectureMaterialRepository.findByCourse_Id(courseId);
        
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Khóa học này chưa có tài liệu nào");
        }

        return list.stream().map(this::toResponse).toList();
    }

    private LectureMaterialResponse toResponse(LectureMaterial material) {
        return LectureMaterialResponse.builder()
                .id(material.getId())
                .title(material.getTitle())
                .fileUrl(material.getFileUrl())
                .courseId(material.getCourse().getId())
                .lecturerId(material.getLecturer().getId())
                .uploadedAt(material.getUploadedAt())
                .build();
    }
}
