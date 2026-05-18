package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.mapper.StockMapper;
import com.stockSync.backend.stock.dto.StockRequest;
import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.dto.StockTransferRequest;
import com.stockSync.backend.stock.model.Product;
import com.stockSync.backend.stock.model.Stock;
import com.stockSync.backend.stock.model.StockMovement;
import com.stockSync.backend.stock.model.Warehouse;
import com.stockSync.backend.stock.repository.ProductRepository;
import com.stockSync.backend.stock.repository.StockRepository;
import com.stockSync.backend.stock.repository.WarehouseRepository;
import com.stockSync.backend.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final StockMapper stockMapper;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public List<StockResponse> getAllStocks() {
        return stockMapper.toResponseList(stockRepository.findByUserId(getCurrentUser().getId()));
    }

    @Override
    public List<StockResponse> getStocksByWarehouse(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new RuntimeException("La bodega con ID: " + warehouseId + " no existe");
        }
        return stockMapper.toResponseList(stockRepository.findByWarehouseIdAndUserId(warehouseId, getCurrentUser().getId()));
    }

    @Override
    public List<StockResponse> getStocksByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("El producto con ID: " + productId + " no existe");
        }
        return stockMapper.toResponseList(stockRepository.findByProductIdAndUserId(productId, getCurrentUser().getId()));
    }

    @Override
    @Transactional
    public StockResponse addStock(StockRequest request) {
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Bodega no encontrado"));

        Stock stock = stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElse(new Stock(product, warehouse, 0));
        stock.setQuantity(stock.getQuantity() + request.getQuantity());
        if (stock.getUser() == null) {
            stock.setUser(currentUser);
        }

        product.setStock(product.getStock() + request.getQuantity());
        productRepository.save(product);

        return stockMapper.toResponse(stockRepository.save(stock));
    }

    @Override
    @Transactional
    public StockResponse updateStock(StockRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Bodega no encontrado"));

        Stock stock = stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElse(new Stock(product, warehouse, 0));

        stock.setQuantity(request.getQuantity());
        return stockMapper.toResponse(stockRepository.save(stock));
    }

    @Override
    @Transactional
    public void transferStock(StockTransferRequest request) {
        User currentUser = getCurrentUser();
        Stock sourceStock = stockRepository.findByProductIdAndWarehouseId(
                        request.getProductId(), request.getSourcewarehouseId())
                .orElseThrow(() -> new RuntimeException("No hay stock en la bodega de origen."));

        if (sourceStock.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Stock insuficiente para realizar la transferencia");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Warehouse sourceWh = warehouseRepository.findById(request.getSourcewarehouseId())
                .orElseThrow(() -> new RuntimeException("Bodega de origen no encontrada"));
        Warehouse destWh = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new RuntimeException("Bodega de destino no encontrada"));

        Stock destStock = stockRepository.findByProductAndWarehouse(product, destWh)
                .orElse(new Stock(product, destWh, 0));

        sourceStock.setQuantity(sourceStock.getQuantity() - request.getQuantity());
        destStock.setQuantity(destStock.getQuantity() + request.getQuantity());

        stockRepository.save(sourceStock);
        stockRepository.save(destStock);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .sourceWarehouse(sourceWh)
                .destinationWarehouse(destWh)
                .quantity(request.getQuantity())
                .movementType("TRANSFER")
                .build();
    }

    @Override
    @Transactional
    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new RuntimeException("La bodega con ID: " + id + " no existe");
        }
        stockRepository.deleteById(id);
    }
}
