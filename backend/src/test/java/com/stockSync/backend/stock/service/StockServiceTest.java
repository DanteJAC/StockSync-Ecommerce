package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.mapper.StockMapper;
import com.stockSync.backend.stock.repository.ProductRepository;
import com.stockSync.backend.stock.repository.StockRepository;
import com.stockSync.backend.stock.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockMapper stockMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void getAllStocks_ShouldReturnListOfStocks() {
        // 1. Arrange (Preparar)
        when(stockRepository.findAll()).thenReturn(List.of());
        when(stockMapper.toResponseList(anyList())).thenReturn(List.of());

        // 2. Act (Actuar)
        List<StockResponse> resultado = stockService.getAllStocks();

        // 3. Assert (Afirmar)
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}