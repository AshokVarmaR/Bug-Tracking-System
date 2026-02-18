package com.bts.dtos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignUsersRequestDTO {
    private List<Long> developers;
    private List<Long> testers;
}
