package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.mapper.WarehouseMapper;
import com.stockSync.backend.stock.dto.WarehouseRequest;
import com.stockSync.backend.stock.dto.WarehouseResponse;
import com.stockSync.backend.stock.model.Warehouse;
import com.stockSync.backend.stock.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends BaseService implements WarehouseService { // <-- Heredamos de BaseService

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public List<WarehouseResponse> getAllWarehouse() {
        // Buscamos por el ID del dueño del entorno
        return warehouseMapper.toResponseList(warehouseRepository.findByUserId(getTenantId()));
    }

    @Override
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada"));

        // Verificamos que la bodega pertenezca al entorno actual
        if (!warehouse.getUser().getId().equals(getTenantId())) {
            throw new RuntimeException("Bodega no encontrada");
        }
        return warehouseMapper.toResponse(warehouse);
    }

    @Override
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        Warehouse warehouse = warehouseMapper.toEntity(request);
        // Asignamos al DUEÑO como propietario del registro
        warehouse.setUser(getTenantUser());
        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar: ID no encontrado"));

        if (!warehouse.getUser().getId().equals(getTenantId())) {
            throw new RuntimeException("Bodega no encontrada");
        }

        warehouseMapper.updateEntityFromRequest(request, warehouse);
        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar: ID no encontrado"));

        if (!warehouse.getUser().getId().equals(getTenantId())) {
            throw new RuntimeException("Bodega no encontrada");
        }
        warehouseRepository.deleteById(id);
    }

    @Override
    public long countAllWarehouse() {
        return warehouseRepository.countByUserId(getTenantId());
    }
}