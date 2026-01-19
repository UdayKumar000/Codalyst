package com.company.demo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "projects")
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    public Project(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "repo_url")
    private String repoUrl;

    @Setter
    @Getter
    @Column(name = "video_id")
    private String videoId;

    @Getter
    @Setter
    @Column(name = "video_status")
    private String videoStatus;

    @Getter
    @Setter
    @Column(name = "map_file_url")
    private String mapFileUrl;

    @Getter
    @Setter
    @Column(name = "xml_file_url")
    private String xmlFileUrl;

}
