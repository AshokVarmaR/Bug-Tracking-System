package com.bts.models;

import java.time.LocalDate;
import java.util.Set;

import com.bts.enums.ProjectStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    
    private String remarks;
    
    private String document;
    
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.CREATED;

    /* =========================
       PROJECT MANAGER
       ========================= */
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_manager_id")
    private User projectManager;

    /* =========================
       DEVELOPERS
       ========================= */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "project_developers",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> developers;

    /* =========================
       TESTERS
       ========================= */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "project_testers",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> testers;
}
