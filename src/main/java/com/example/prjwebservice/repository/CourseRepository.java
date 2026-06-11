package com.example.prjwebservice.repository;

import com.example.prjwebservice.model.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    boolean existsByCourseCode(String courseCode);

    Page<Course> findByCourseCodeContainingIgnoreCaseOrCourseNameContainingIgnoreCase(
            String courseCode, String courseName, Pageable pageable
    );
}
