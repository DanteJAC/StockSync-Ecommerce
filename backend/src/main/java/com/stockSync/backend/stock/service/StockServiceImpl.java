package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.mapper.StockMapper;
import com.stockSync.backend.stock.dto.StockRequest;
import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.dto.StockTransferRequest;
import com.stockSync.backend.stock.dto.StockMovementResponse;
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
import org.springframework.security.access.AccessDeniedException;
import com.stockSync.backend.common.exception.ResourceNotFoundException;
import com.stockSync.backend.common.exception.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<StockMovementResponse> getMovements(String type, Long warehouseId) {
        List<StockMovement> movements;
        if (warehouseId != null) {
            movements = stockMovementRepository.findBySourceWarehouseId(warehouseId);
            movements.addAll(stockMovementRepository.findByDestinationWarehouseId(warehouseId));
        } else {
            movements = stockMovementRepository.findAll();
        }

        return movements.stream()
                .filter(m -> type == null || type.equals(m.getMovementType()))
                .filter(m -> m.getProduct().getUser().getId().equals(getTenantId()))
                .map(m -> {
                    StockMovementResponse res = new StockMovementResponse();
                    res.setId(m.getId());
                    res.setProductId(m.getProduct().getId());
                    res.setProductName(m.getProduct().getName());
                    if (m.getSourceWarehouse() != null) {
                        res.setSourceWarehouseId(m.getSourceWarehouse().getId());
                        res.setSourceWarehouseName(m.getSourceWarehouse().getName());
                    }
                    if (m.getDestinationWarehouse() != null) {
                        res.setDestinationWarehouseId(m.getDestinationWarehouse().getId());
                        res.setDestinationWarehouseName(m.getDestinationWarehouse().getName());
                    }
                    res.setQuantity(m.getQuantity());
                    res.setMovementType(m.getMovementType());
                    res.setUnitPrice(m.getProduct().getPrice());
                    if (m.getProduct().getPrice() != null && m.getQuantity() != null) {
                        res.setTotalPrice(m.getProduct().getPrice().multiply(new java.math.BigDecimal(m.getQuantity())));
                    }
                    res.setCreatedAt(m.getCreatedAt());
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StockResponse addStock(StockRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductId()));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega", "id", request.getWarehouseId()));

        if (!product.getUser().getId().equals(getTenantId()) || !warehouse.getUser().getId().equals(getTenantId())) {
            throw new AccessDeniedException("Operación no permitida: los recursos no corresponden a tu organización.");
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
    public StockResponse processSale(StockRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductId()));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega", "id", request.getWarehouseId()));

        if (!product.getUser().getId().equals(getTenantId()) || !warehouse.getUser().getId().equals(getTenantId())) {
            throw new AccessDeniedException("Operación no permitida: los recursos no corresponden a tu organización.");
        }

        Stock stock = stockRepository.findByProductIdAndWarehouseIdAndUserId(product.getId(), warehouse.getId(), getTenantId())
                .orElseThrow(() -> new BadRequestException("No hay stock disponible para realizar la venta."));

        if (stock.getQuantity() < request.getQuantity()) {
            throw new BadRequestException("Stock insuficiente para realizar la venta.");
        }

        stock.setQuantity(stock.getQuantity() - request.getQuantity());
        stock.setUser(getTenantUser());

        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        // Registro de auditoría del movimiento histórico
        StockMovement movement = StockMovement.builder()
                .product(product)
                .sourceWarehouse(warehouse)
                .quantity(request.getQuantity())
                .movementType("VENTA")
                .build();
        stockMovementRepository.save(movement);

        return stockMapper.toResponse(stockRepository.save(stock));
    }

    @Override
    @Transactional
    public StockResponse updateStock(StockRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductId()));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega", "id", request.getWarehouseId()));

        if (!product.getUser().getId().equals(getTenantId()) || !warehouse.getUser().getId().equals(getTenantId())) {
            throw new AccessDeniedException("Acceso denegado.");
        }

        Stock stock = stockRepository.findByProductIdAndWarehouseIdAndUserId(product.getId(), warehouse.getId(), getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Registro de inventario no existente para ajuste directo"));

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
                .orElseThrow(() -> new ResourceNotFoundException("No hay existencias registradas en la sucursal de origen."));

        if (sourceStock.getQuantity() < request.getQuantity()) {
            throw new BadRequestException("Stock insuficiente para realizar la transferencia");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductId()));
        Warehouse sourceWh = warehouseRepository.findById(request.getSourcewarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega", "id", request.getSourcewarehouseId()));
        Warehouse destWh = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega", "id", request.getDestinationWarehouseId()));

        if (!sourceWh.getUser().getId().equals(getTenantId()) || !destWh.getUser().getId().equals(getTenantId())) {
            throw new AccessDeniedException("Movimiento denegado: las sucursales involucradas no pertenecen a la misma empresa.");
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
                .orElseThrow(() -> new ResourceNotFoundException("Stock", "id", id));

        if (!stock.getUser().getId().equals(getTenantId())) {
            throw new AccessDeniedException("Acceso denegado.");
        }

        Product product = stock.getProduct();
        product.setStock(product.getStock() - stock.getQuantity());
        productRepository.save(product);

        stockRepository.delete(stock);
    }
}