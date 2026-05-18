package com.stockSync.backend.stock.repository;

import com.stockSync.backend.stock.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;




@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndUserId(String name, Long userId);
    List<Category> findByUserId(Long userId);
    long countByUserId(Long userId);
}
