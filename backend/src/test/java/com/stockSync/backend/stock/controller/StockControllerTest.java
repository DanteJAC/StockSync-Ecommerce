package com.stockSync.backend.stock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockSync.backend.stock.dto.StockRequest;
import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StockControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StockService stockService;

    @InjectMocks
    private StockController stockController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        // Configuramos MockMvc para poder probar las rutas sin levantar el servidor HTTP completo
        mockMvc = MockMvcBuilders.standaloneSetup(stockController).build();
    }

    @Test
    public void givenExistingStocks_whenGetAll_thenReturnStockList() throws Exception {
        // Arrange
        StockResponse response = new StockResponse();
        response.setId(1L);
        response.setQuantity(50);
        
        when(stockService.getAllStocks()).thenReturn(Collections.singletonList(response));

        // Act & Assert
        // Llamamos al endpoint GET /api/v1/stocks y verificamos que responda HTTP 200 y traiga una lista con tamaño 1
        mockMvc.perform(get("/api/v1/stocks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].quantity").value(50));

        // Verify
        verify(stockService).getAllStocks();
    }

    @Test
    public void givenValidStockRequest_whenAddStock_thenReturnCreatedStock() throws Exception {
        // Arrange
        StockRequest request = new StockRequest();
        request.setProductId(10L);
        request.setWarehouseId(20L);
        request.setQuantity(100);

        StockResponse response = new StockResponse();
        response.setId(5L);
        response.setQuantity(100);

        when(stockService.addStock(any(StockRequest.class))).thenReturn(response);

        // Act & Assert
        // Llamamos al endpoint POST /api/v1/stocks/add, enviamos el JSON y esperamos HTTP 201 Created
        mockMvc.perform(post("/api/v1/stocks/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.quantity").value(100));

        // Verify
        verify(stockService).addStock(any(StockRequest.class));
    }
}
