package com.stockSync.backend.user.dto;

import com.stockSync.backend.user.model.Role;
import lombok.Data;

@Data
public class UserUpdateDto {
    private Role role;
    private Long assignedWarehouseId;
    private Boolean active;
}
