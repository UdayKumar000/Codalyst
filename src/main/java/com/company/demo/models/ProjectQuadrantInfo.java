package com.company.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "project_quadrant_info")
public class ProjectQuadrantInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="score_id")
    private Long scoreId;




    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "x_score")
    private int xScore;

    @Column(name = "y_score")
    private int yScore;

    @Column(name = "file_layer")
    private String fileLayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}
