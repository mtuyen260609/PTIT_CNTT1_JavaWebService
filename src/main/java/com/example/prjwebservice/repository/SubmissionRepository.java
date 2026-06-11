package com.example.prjwebservice.repository;

import com.example.prjwebservice.model.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    List<Submission> findByCourse_Id(Long courseId);
}
