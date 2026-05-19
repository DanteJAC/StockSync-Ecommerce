package com.stockSync.backend.stock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockRequest {
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "El ID de la bodega/local es obligatorio")
    private Long warehouseId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad a registrar debe ser como mínimo 1 unidad")
    private Integer quantity;
}
