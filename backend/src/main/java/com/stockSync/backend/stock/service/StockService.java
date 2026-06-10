package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.dto.StockRequest;
import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.dto.StockTransferRequest;
import java.util.List;
import com.stockSync.backend.stock.dto.StockMovementResponse;

public interface StockService {

    //CONSULTAS
    List<StockResponse> getAllStocks();
    List<StockResponse>getStocksByWarehouse(Long warehouseId);
    List<StockResponse>getStocksByProduct(Long productId);
    List<StockMovementResponse> getMovements(String type, Long warehouseId);

    //OPERACIONES
    StockResponse updateStock(StockRequest request);
    StockResponse addStock(StockRequest request);
    StockResponse processSale(StockRequest request);

    void transferStock(StockTransferRequest request);
    void deleteStock(Long id);

}
