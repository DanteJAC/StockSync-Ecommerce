package com.stockSync.backend.user.service;

import com.stockSync.backend.user.model.User;
import com.stockSync.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getMyInvitedUsers() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByOwnerId(currentUser.getId());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, com.stockSync.backend.user.dto.UserUpdateDto dto, com.stockSync.backend.stock.repository.WarehouseRepository warehouseRepository) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }
        
        if (dto.getAssignedWarehouseId() != null) {
            if (dto.getAssignedWarehouseId() == -1) {
                user.setAssignedWarehouse(null);
            } else {
                com.stockSync.backend.stock.model.Warehouse wh = warehouseRepository.findById(dto.getAssignedWarehouseId())
                        .orElseThrow(() -> new RuntimeException("Bodega no encontrada"));
                user.setAssignedWarehouse(wh);
            }
        }
        
        return userRepository.save(user);
    }
}
