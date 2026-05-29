package com.stockSync.backend.stock.controller;


import com.stockSync.backend.stock.dto.ProductRequest;
import com.stockSync.backend.stock.dto.ProductResponse;
import com.stockSync.backend.stock.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    //Listar Todos los productos
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    //Obtener producto por Id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    //Listar Productos por categoria
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public List<ProductResponse> getProductsByCategoryId(@PathVariable Long categoryId){
        return productService.getProductsByCategoryId(categoryId);
    }

    //Buscar productos por nombre
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL', 'BODEGA')")
    public ResponseEntity<List<ProductResponse>> searchByName(@RequestParam String name){
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }


    //Crear nuevo producto
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request){
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }

    //Actualizar producto
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request){
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    //Eliminar producto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
