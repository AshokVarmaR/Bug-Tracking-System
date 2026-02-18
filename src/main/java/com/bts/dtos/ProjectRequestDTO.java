package com.bts.dtos;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjectRequestDTO {
    private String name;
    private String description;
    private LocalDate dueDate;
}
