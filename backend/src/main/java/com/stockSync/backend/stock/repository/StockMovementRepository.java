package com.stockSync.backend.stock.repository;

import com.stockSync.backend.stock.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // Listar historial de movimientos de un producto en específico
    List<StockMovement> findByProductId(Long productId);

    // Ver todo lo que ha salido de una bodega de origen específica
    List<StockMovement> findBySourceWarehouseId(Long sourceWarehouseId);

    // Ver todo lo que ha ingresado a una bodega o local de destino específica
    List<StockMovement> findByDestinationWarehouseId(Long destinationWarehouseId);

    // Rastrear movimientos por tipo (ej: "MERMA", "TRASLADO")
    List<StockMovement> findByMovementType(String movementType);

    List<StockMovement> findByProductIdAndMovementTypeAndCreatedAtAfter(Long productId, String movementType, LocalDateTime createdAt);
}
