package com.stockSync.backend.stock.repository;

import com.stockSync.backend.stock.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Valida si el nombre de categoría ya existe dentro del mismo entorno
    boolean existsByNameAndUserId(String name, Long tenantId);

    List<Category> findByUserId(Long tenantId);

    long countByUserId(Long tenantId);
}