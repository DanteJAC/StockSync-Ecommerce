package com.stockSync.backend.stock.controller;


import com.stockSync.backend.stock.service.ProductService;
import com.stockSync.backend.stock.service.StockService;
import com.stockSync.backend.stock.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ProductService productService;
    private final StockService stockService;
    private final WarehouseService warehouseService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("totalProducts", productService.countAllProducts());
        model.addAttribute("totalBodegas", warehouseService.countAllWarehouse());
        model.addAttribute("totalStocks", stockService.getAllStocks().size());

        return "admin/dashboard";
    }


}
