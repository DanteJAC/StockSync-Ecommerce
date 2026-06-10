package com.stockSync.backend.stock.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.stockSync.backend.user.model.User;
import java.time.LocalDateTime; // <-- Asegúrate de importar LocalDateTime
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    // ELIMINAMOS @Column(name = "date") y usamos el estándar
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // <-- Con "d" al final y LocalDateTime

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    // ELIMINA el método prePersist() que tenías aquí abajo,
    // @CreationTimestamp ya hace ese trabajo automáticamente.
}