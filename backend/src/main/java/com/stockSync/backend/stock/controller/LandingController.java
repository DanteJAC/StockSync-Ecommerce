package com.stockSync.backend.stock.controller;

import com.stockSync.backend.stock.service.CategoryService;
import com.stockSync.backend.stock.service.ProductService;
import com.stockSync.backend.stock.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LandingController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final WarehouseService warehouseService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("productos", productService.getAllProducts());
        model.addAttribute("categorias", categoryService.getAllCategories());
        model.addAttribute("totalProductos", productService.countAllProducts());
        model.addAttribute("totalBodegas", warehouseService.countAllWarehouse());
        model.addAttribute("totalCategorias", categoryService.getAllCategories().size());
        model.addAttribute("paginaActiva", "inicio");
        return "index";
    }
}
