package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.model.Product;
import com.stockSync.backend.stock.model.StockMovement;
import com.stockSync.backend.stock.repository.ProductRepository;
import com.stockSync.backend.stock.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicStockEvaluationService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    // Ejecutar todos los días a la medianoche
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void evaluateAndAdjustMinStockLevels() {
        log.info("Iniciando evaluación dinámica de niveles mínimos de stock...");
        
        // Obtener la fecha de hace 30 días
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Obtener todos los productos activos
        List<Product> products = productRepository.findAll().stream()
                .filter(Product::getActive)
                .toList();

        for (Product product : products) {
            // Buscar egresos o ventas de los últimos 30 días para este producto
            List<StockMovement> recentOutflows = stockMovementRepository.findByProductIdAndMovementTypeAndCreatedAtAfter(
                    product.getId(), "EGRESO", thirtyDaysAgo);

            int totalOutflow = recentOutflows.stream()
                    .mapToInt(StockMovement::getQuantity)
                    .sum();

            // Lógica adaptativa simple: si las ventas mensuales superan el cuádruple del mínimo actual,
            // aumentamos el mínimo en un 50% para estar preparados.
            int currentMin = product.getMinStockLevel() != null ? product.getMinStockLevel() : 5;
            
            if (totalOutflow > currentMin * 4) {
                int newMin = (int) Math.ceil(currentMin * 1.5);
                log.info("Ajustando stock mínimo para producto ID {} ({}): {} -> {} debido a alta demanda ({})", 
                         product.getId(), product.getName(), currentMin, newMin, totalOutflow);
                
                product.setMinStockLevel(newMin);
                productRepository.save(product);
            }
        }
        
        log.info("Evaluación dinámica completada.");
    }
}
