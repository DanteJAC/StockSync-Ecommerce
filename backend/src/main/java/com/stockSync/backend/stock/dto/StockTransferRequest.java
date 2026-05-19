package com.stockSync.backend.stock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockTransferRequest {
    @NotNull(message = "El ID del producto a transferir es obligatorio")
    private Long productId;

    @NotNull(message = "La bodega de origen es obligatoria")
    private Long sourcewarehouseId;

    @NotNull(message = "La bodega o local de destino es obligatoria")
    private Long destinationWarehouseId;

    @NotNull(message = "La cantidad a transferir es obligatoria")
    @Min(value = 1, message = "La cantidad a transferir debe ser como mínimo 1 unidad")
    private Integer quantity;
}