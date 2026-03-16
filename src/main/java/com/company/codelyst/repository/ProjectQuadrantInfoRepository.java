package com.company.codelyst.repository;

import com.company.codelyst.models.ProjectQuadrantInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectQuadrantInfoRepository extends JpaRepository<ProjectQuadrantInfo,Long> {
    Optional<ProjectQuadrantInfo> findByProjectIdAndFileName(
            Long projectId, String fileName);
    List<ProjectQuadrantInfo> findByProject_Id(Long projectId);

}
