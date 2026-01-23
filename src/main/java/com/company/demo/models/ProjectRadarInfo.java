package com.company.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "project_radar_info")
public class ProjectRadarInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "code_quality")
    private int codeQuality;

    private int architecture;
    private int reliability;
    private int performance;
    private int security;
    private int testability;

    @OneToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;
}
