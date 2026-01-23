package com.company.demo.repository;

import com.company.demo.models.ProjectRadarInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRadarInfoRepository extends JpaRepository<ProjectRadarInfo,Long> {
    Optional<ProjectRadarInfo> findByProject_Id(Long projectId);
}
