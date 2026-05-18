package com.stockSync.backend.stock.repository;


import com.stockSync.backend.stock.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByNameContainingIgnoreCaseAndUserId(String name, Long userId);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByCategoryIdAndUserId(Long categoryId, Long userId);
    Optional<Product> findBySku(String sku);
    List<Product> findByUserId(Long userId);
    long countByUserId(Long userId);
}
