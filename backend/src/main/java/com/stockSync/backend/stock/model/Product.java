package com.stockSync.backend.stock.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.stockSync.backend.user.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price; // Las validaciones como @DecimalMin pertenecen a los DTOs de entrada, no a las entidades físicas

    @Column(nullable = false)
    private Long stock;

    @Column(unique = true, length = 50)
    private String sku;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "min_stock_level", nullable = false)
    private Integer minStockLevel = 5;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}