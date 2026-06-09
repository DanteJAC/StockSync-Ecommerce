package com.stockSync.backend.stock.mapper;

import com.stockSync.backend.stock.dto.StockRequestResponse;
import com.stockSync.backend.stock.model.StockRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockRequestMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.sku", target = "sku")
    @Mapping(source = "sourceWarehouse.id", target = "sourceWarehouseId")
    @Mapping(source = "sourceWarehouse.name", target = "sourceWarehouseName")
    @Mapping(source = "destinationWarehouse.id", target = "destinationWarehouseId")
    @Mapping(source = "destinationWarehouse.name", target = "destinationWarehouseName")
    StockRequestResponse toResponse(StockRequest entity);

    List<StockRequestResponse> toResponseList(List<StockRequest> entities);
}
