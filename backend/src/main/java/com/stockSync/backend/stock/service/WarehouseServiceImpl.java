package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.mapper.WarehouseMapper;
import com.stockSync.backend.stock.dto.WarehouseRequest;
import com.stockSync.backend.stock.dto.WarehouseResponse;
import com.stockSync.backend.stock.model.Warehouse;
import com.stockSync.backend.stock.repository.WarehouseRepository;
import com.stockSync.backend.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public List<WarehouseResponse> getAllWarehouse() {
        return warehouseMapper.toResponseList(warehouseRepository.findByUserId(getCurrentUser().getId()));
    }

    @Override
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada"));
        if (!warehouse.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Bodega no encontrada");
        }
        return warehouseMapper.toResponse(warehouse);
    }

    @Override
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        Warehouse warehouse = warehouseMapper.toEntity(request);
        warehouse.setUser(getCurrentUser());
        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        User currentUser = getCurrentUser();
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar: ID no encontrado"));

        if (!warehouse.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bodega no encontrada");
        }

        warehouseMapper.updateEntityFromRequest(request, warehouse);
        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        User currentUser = getCurrentUser();
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar: ID no encontrado"));
        if (!warehouse.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bodega no encontrada");
        }
        warehouseRepository.deleteById(id);
    }

    @Override
    public long countAllWarehouse() {
        return warehouseRepository.countByUserId(getCurrentUser().getId());
    }
}
