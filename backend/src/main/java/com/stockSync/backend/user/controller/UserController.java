package com.stockSync.backend.user.controller;

import com.stockSync.backend.user.model.User;
import com.stockSync.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.stockSync.backend.user.dto.UserUpdateDto;
import com.stockSync.backend.stock.repository.WarehouseRepository;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final WarehouseRepository warehouseRepository;

    @GetMapping("/invited")
    public ResponseEntity<List<User>> listInvited() {
        return ResponseEntity.ok(userService.getMyInvitedUsers());
    }

    @GetMapping
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto, warehouseRepository));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
