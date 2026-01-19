package com.company.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name="project_c4_diagrams")
public class ProjectC4Diagrams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="diagram_id")
    private Long diagramId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name="c4_diagram_url")
    private String c4DiagramUrl;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",unique = true)
    private Project project;

}
