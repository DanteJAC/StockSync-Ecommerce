package com.stockSync.backend.stock.controller;


import com.stockSync.backend.stock.service.CategoryService;
import com.stockSync.backend.stock.service.ProductService;
import com.stockSync.backend.stock.service.StockService;
import com.stockSync.backend.stock.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.HashMap;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final ProductService productService;
    private final WarehouseService warehouseService;
    private final StockService stockService;
    private final CategoryService categoryService;

    // ─── PRODUCTOS ────────────────────────────────────────────

    @GetMapping("/productos")
    public String productos(Model model) {
        model.addAttribute("productos", productService.getAllProducts());
        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProductoForm(Model model) {
        // ✅ Objeto vacío para que th:text="${producto.idProducto != null}" no explote
        model.addAttribute("producto", new HashMap<>());
        model.addAttribute("categorias", categoryService.getAllCategories());
        return "admin/producto-form";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProductoForm(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productService.getProductById(id));
        model.addAttribute("categorias", categoryService.getAllCategories());
        return "admin/producto-form";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/productos";
    }

    // ─── BODEGAS ──────────────────────────────────────────────

    @GetMapping("/bodegas")
    public String bodegas(Model model) {
        model.addAttribute("bodegas", warehouseService.getAllWarehouse());
        return "admin/bodegas";
    }

    @GetMapping("/bodegas/nuevo")
    public String nuevaBodegaForm(Model model) {
        // ✅ Objeto vacío para que th:text="${bodega.idBodega != null}" no explote
        model.addAttribute("bodega", new HashMap<>());
        return "admin/bodega-form";
    }

    @GetMapping("/bodegas/editar/{id}")
    public String editarBodegaForm(@PathVariable Long id, Model model) {
        model.addAttribute("bodega", warehouseService.getWarehouseById(id));
        return "admin/bodega-form";
    }

    @GetMapping("/bodegas/eliminar/{id}")
    public String eliminarBodega(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return "redirect:/admin/bodegas";
    }

    // ─── STOCK ────────────────────────────────────────────────

    @GetMapping("/stock")
    public String stock(Model model) {
        model.addAttribute("stocks", stockService.getAllStocks());
        return "admin/stock";
    }

    @GetMapping("/stock/nuevo")
    public String nuevoStockForm(Model model) {
        // ✅ Objeto vacío para que th:text="${stock.idStock != null}" no explote
        model.addAttribute("stock", new HashMap<>());
        model.addAttribute("productos", productService.getAllProducts());
        model.addAttribute("bodegas", warehouseService.getAllWarehouse());
        return "admin/stock-form";
    }

    @GetMapping("/stock/editar/{id}")
    public String editarStockForm(@PathVariable Long id, Model model) {
        model.addAttribute("stock", stockService.getAllStocks()
                .stream()
                .filter(s -> s.getId().equals(id))  // ✅ getId() no getIdStock()
                .findFirst()
                .orElseThrow());
        model.addAttribute("productos", productService.getAllProducts());
        model.addAttribute("bodegas", warehouseService.getAllWarehouse());
        return "admin/stock-form";
    }

    @GetMapping("/stock/eliminar/{id}")
    public String eliminarStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return "redirect:/admin/stock";
    }

    // ─── USUARIOS ─────────────────────────────────────────────

    @GetMapping("/usuarios")
    public String usuarios() {
        return "admin/usuarios";
    }
}