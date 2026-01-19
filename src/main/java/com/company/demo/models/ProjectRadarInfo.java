package com.company.demo.models;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "project_radar_info")
public class ProjectRadarInfo {

    @Id
    private Long id;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "code_quality")
    private int codeQuality;

    private int architecture;
    private int reliability;
    private int performance;
    private int security;
    private int testability;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Project project;
}
