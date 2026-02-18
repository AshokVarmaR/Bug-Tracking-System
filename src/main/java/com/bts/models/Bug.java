package com.bts.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bts.enums.BugStatus;
import com.bts.enums.Priority;
import com.bts.enums.Severity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bugs")
public class Bug extends AuditingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String description;
    
    private String remarks;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private BugStatus status;
    
    @OneToMany(mappedBy = "bug", cascade = CascadeType.ALL)
    private List<Attachment> attachments = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;   // Tester

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;  // Developer


}
