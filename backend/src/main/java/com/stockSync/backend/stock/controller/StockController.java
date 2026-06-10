package com.stockSync.backend.stock.controller;


import com.stockSync.backend.stock.dto.StockRequest;
import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.dto.StockTransferRequest;
import com.stockSync.backend.stock.dto.StockMovementResponse;
import com.stockSync.backend.stock.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    //Lista todo el inventario global
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    //Ver historial de movimientos
    @GetMapping("/movements")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public ResponseEntity<List<StockMovementResponse>> getMovements(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long warehouseId) {
        return ResponseEntity.ok(stockService.getMovements(type, warehouseId));
    }

    //Ver stocks por bodega especifica
    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public ResponseEntity<List<StockResponse>> getByWarehouse(@PathVariable Long warehouseId){
        return ResponseEntity.ok(stockService.getStocksByWarehouse(warehouseId));
    }

    //Ver stock por producto especifico
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public ResponseEntity<List<StockResponse>> getByProduct(@PathVariable Long productId){
        return ResponseEntity.ok(stockService.getStocksByProduct(productId));
    }

    // Agregar stock (suma a lo existente)
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public ResponseEntity<StockResponse> addStock(@Valid @RequestBody StockRequest stockRequest){ // <-- Falta el @Valid
        return new ResponseEntity<>(stockService.addStock(stockRequest), HttpStatus.CREATED);
    }

    // Registrar una venta (resta al stock existente)
    @PostMapping("/sale")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    public ResponseEntity<StockResponse> processSale(@Valid @RequestBody StockRequest stockRequest){
        return new ResponseEntity<>(stockService.processSale(stockRequest), HttpStatus.CREATED);
    }

    // Ajustar Stock (sobreescribe el valor - correciones manuales)
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<StockResponse> updateStock(@Valid @RequestBody StockRequest stockRequest){ // <-- Falta el @Valid
        return ResponseEntity.ok(stockService.updateStock(stockRequest));
    }

    //Tranferencia entre bodegas (Logica de historial incluida)
    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public ResponseEntity<Void> transferStock(@Valid @RequestBody StockTransferRequest request) {
        stockService.transferStock(request);
        return ResponseEntity.noContent().build(); //204: Exito sin cuerpo de respuesta
    }

    //Eliminar registro de stock por id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id){
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }


}
