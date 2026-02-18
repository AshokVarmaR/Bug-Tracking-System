package com.bts.dtos;

import com.bts.enums.Priority;
import com.bts.enums.Severity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BugCreateRequestDTO {
    private String title;
    private String description;
    private Severity severity;
    private Priority priority;
    private Long projectId;
}
