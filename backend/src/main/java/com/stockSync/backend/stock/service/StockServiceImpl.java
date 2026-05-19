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
import com.stockSync.backend.stock.repository.StockMovementRepository;
import com.stockSync.backend.stock.repository.StockRepository;
import com.stockSync.backend.stock.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl extends BaseService implements StockService {

    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockMapper stockMapper;

    @Override
    public List<StockResponse> getAllStocks() {
        return stockMapper.toResponseList(stockRepository.findByUserId(getTenantId()));
    }

    @Override
    public List<StockResponse> getStocksByWarehouse(Long warehouseId) {
        return stockMapper.toResponseList(stockRepository.findByWarehouseIdAndUserId(warehouseId, getTenantId()));
    }

    @Override
    public List<StockResponse> getStocksByProduct(Long productId) {
        return stockMapper.toResponseList(stockRepository.findByProductIdAndUserId(productId, getTenantId()));
    }

    @Override
    @Transactional
    public StockResponse addStock(StockRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada"));

        if (!product.getUser().getId().equals(getTenantId()) || !warehouse.getUser().getId().equals(getTenantId())) {
            throw new RuntimeException("Operación no permitida: los recursos no corresponden a tu organización.");
        }

        Stock stock = stockRepository.findByProductIdAndWarehouseIdAndUserId(product.getId(), warehouse.getId(), getTenantId())
                .orElse(new Stock(product, warehouse, 0));

        stock.setQuantity(stock.getQuantity() + request.getQuantity());
        stock.setUser(getTenantUser());

        product.setStock(product.getStock() + request.getQuantity());
        productRepository.save(product);

        // Registro de auditoría del movimiento histórico
        StockMovement movement = StockMovement.builder()
                .product(product)
                .destinationWarehouse(warehouse)
                .quantity(request.getQuantity())
                .movementType("INGRESO")
                .build();
        stockMovementRepository.save(movement);

        return stockMapper.toResponse(stockRepository.save(stock));
    }

    @Override
    @Transactional
    public StockResponse updateStock(StockRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada"));

        if (!product.getUser().getId().equals(getTenantId()) || !warehouse.getUser().getId().equals(getTenantId())) {
            throw new RuntimeException("Acceso denegado.");
        }

        Stock stock = stockRepository.findByProductIdAndWarehouseIdAndUserId(product.getId(), warehouse.getId(), getTenantId())
                .orElseThrow(() -> new RuntimeException("Registro de inventario no existente para ajuste directo"));

        // Ajustar el stock global recalculando la diferencia neta
        int diferencia = request.getQuantity() - stock.getQuantity();
        product.setStock(product.getStock() + diferencia);
        productRepository.save(product);

        stock.setQuantity(request.getQuantity());

        StockMovement movement = StockMovement.builder()
                .product(product)
                .destinationWarehouse(warehouse)
                .quantity(request.getQuantity())
                .movementType("AJUSTE")
                .build();
        stockMovementRepository.save(movement);

        return stockMapper.toResponse(stockRepository.save(stock));
    }

    @Override
    @Transactional
    public void transferStock(StockTransferRequest request) {
        Stock sourceStock = stockRepository.findByProductIdAndWarehouseIdAndUserId(
                        request.getProductId(), request.getSourcewarehouseId(), getTenantId())
                .orElseThrow(() -> new RuntimeException("No hay existencias registradas en la sucursal de origen."));

        if (sourceStock.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Stock insuficiente para realizar la transferencia");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Warehouse sourceWh = warehouseRepository.findById(request.getSourcewarehouseId())
                .orElseThrow(() -> new RuntimeException("Bodega de origen no encontrada"));
        Warehouse destWh = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new RuntimeException("Bodega de destino no encontrada"));

        if (!sourceWh.getUser().getId().equals(getTenantId()) || !destWh.getUser().getId().equals(getTenantId())) {
            throw new RuntimeException("Movimiento denegado: las sucursales involucradas no pertenecen a la misma empresa.");
        }

        Stock destStock = stockRepository.findByProductIdAndWarehouseIdAndUserId(product.getId(), destWh.getId(), getTenantId())
                .orElse(new Stock(product, destWh, 0));

        sourceStock.setQuantity(sourceStock.getQuantity() - request.getQuantity());
        destStock.setQuantity(destStock.getQuantity() + request.getQuantity());
        destStock.setUser(getTenantUser());

        stockRepository.save(sourceStock);
        stockRepository.save(destStock);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .sourceWarehouse(sourceWh)
                .destinationWarehouse(destWh)
                .quantity(request.getQuantity())
                .movementType("TRASLADO")
                .build();
        stockMovementRepository.save(movement);
    }

    @Override
    @Transactional
    public void deleteStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El registro de inventario no existe"));

        if (!stock.getUser().getId().equals(getTenantId())) {
            throw new RuntimeException("Acceso denegado.");
        }
        stockRepository.delete(stock);
    }
}