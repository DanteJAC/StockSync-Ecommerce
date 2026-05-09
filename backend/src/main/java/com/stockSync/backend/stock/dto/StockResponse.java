package com.stockSync.backend.stock.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private LocalDateTime lastUpdate;
}