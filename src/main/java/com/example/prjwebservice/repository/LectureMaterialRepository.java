package com.example.prjwebservice.repository;

import com.example.prjwebservice.model.entity.LectureMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureMaterialRepository extends JpaRepository<LectureMaterial, Long> {
    java.util.List<LectureMaterial> findByCourse_Id(Long courseId);
}
