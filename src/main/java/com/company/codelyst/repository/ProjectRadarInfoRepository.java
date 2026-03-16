package com.company.codelyst.repository;

import com.company.codelyst.models.ProjectRadarInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRadarInfoRepository extends JpaRepository<ProjectRadarInfo,Long> {
    Optional<ProjectRadarInfo> findByProject_Id(Long projectId);
}
