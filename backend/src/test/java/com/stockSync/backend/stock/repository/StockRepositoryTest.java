package com.stockSync.backend.stock.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void conectarConNeon_DeberiaCargarElRepositorio() {
        // Al usar SpringBootTest, levantamos la app real y se conecta a Neon automáticamente
        assertNotNull(stockRepository);
    }
}