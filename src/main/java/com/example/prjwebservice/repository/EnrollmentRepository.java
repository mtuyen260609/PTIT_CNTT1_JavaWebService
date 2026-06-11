package com.example.prjwebservice.repository;

import com.example.prjwebservice.model.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    List<Enrollment> findByStudent_Id(Long studentId);
}
