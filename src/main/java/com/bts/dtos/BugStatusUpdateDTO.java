package com.bts.dtos;

import com.bts.enums.BugStatus;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BugStatusUpdateDTO {
    private BugStatus status;
    private String remarks;
}
