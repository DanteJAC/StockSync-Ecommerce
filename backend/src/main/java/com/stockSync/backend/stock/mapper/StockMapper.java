package com.stockSync.backend.stock.mapper;

import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.model.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMapper {

    //Mapea campos anidados de entity.getProduct().getName() a dto.productName
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    StockResponse toResponse(Stock entity);

    //Mapea las listas completas automaticamente usando el metodo anterior
    List<StockResponse> toResponseList(List<Stock> entities);
}
