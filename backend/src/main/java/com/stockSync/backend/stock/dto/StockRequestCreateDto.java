package com.stockSync.backend.stock.dto;
import lombok.Data;
@Data
public class StockRequestCreateDto {
    private Long productId;
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;
    private Integer quantity;
}
