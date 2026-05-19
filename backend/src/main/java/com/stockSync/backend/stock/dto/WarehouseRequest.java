package com.stockSync.backend.stock.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonPropertyOrder({ "id", "code", "name", "address", "city", "createAt" })
public class WarehouseRequest {

    @NotBlank(message = "El código identificador de la sucursal es obligatorio")
    private String code;

    @NotBlank(message = "El nombre de la bodega o local es obligatorio")
    private String name;

    @NotBlank(message = "La dirección física es obligatoria")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;
}
