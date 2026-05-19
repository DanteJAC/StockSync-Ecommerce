package com.stockSync.backend.stock.repository;

import com.stockSync.backend.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // Consultas de stock aisladas por la empresa dueña
    List<Stock> findByWarehouseIdAndUserId(Long warehouseId, Long tenantId);

    List<Stock> findByProductIdAndUserId(Long productId, Long tenantId);

    Optional<Stock> findByProductIdAndWarehouseIdAndUserId(Long productId, Long warehouseId, Long tenantId);

    boolean existsByProductIdAndWarehouseIdAndUserId(Long productId, Long warehouseId, Long tenantId);

    List<Stock> findByUserId(Long tenantId);
}