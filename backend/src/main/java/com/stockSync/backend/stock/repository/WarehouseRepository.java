package com.stockSync.backend.stock.repository;

import com.stockSync.backend.stock.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    // Valida si el código existe SOLO dentro de las bodegas del mismo dueño
    boolean existsByCodeAndUserId(String code, Long tenantId);

    // Lista las bodegas del entorno empresarial
    List<Warehouse> findByUserId(Long tenantId);

    long countByUserId(Long tenantId);
}