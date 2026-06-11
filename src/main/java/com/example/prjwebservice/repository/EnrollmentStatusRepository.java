package com.example.prjwebservice.repository;

import com.example.prjwebservice.model.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentStatusRepository extends JpaRepository<EnrollmentStatus, Long> {
    Optional<EnrollmentStatus> findByName(String name);
}
