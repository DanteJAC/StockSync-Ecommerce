package com.stockSync.backend.stock.repository;

import com.stockSync.backend.stock.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Búsquedas y listados totalmente aislados por el entorno del dueño
    List<Product> findByNameContainingIgnoreCaseAndUserId(String name, Long tenantId);

    List<Product> findByCategoryIdAndUserId(Long categoryId, Long tenantId);

    Optional<Product> findBySkuAndUserId(String sku, Long tenantId);

    List<Product> findByUserId(Long tenantId);

    long countByUserId(Long tenantId);

    @Query("SELECT p FROM Product p WHERE p.user.id = :tenantId AND p.stock <= p.minStockLevel")
    List<Product> findLowStockProducts(@Param("tenantId") Long tenantId);
}