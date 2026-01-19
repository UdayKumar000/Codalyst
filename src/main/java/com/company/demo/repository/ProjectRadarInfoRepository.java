package com.company.demo.repository;

import com.company.demo.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRadarInfoRepository extends JpaRepository<Project,Long> {
}
