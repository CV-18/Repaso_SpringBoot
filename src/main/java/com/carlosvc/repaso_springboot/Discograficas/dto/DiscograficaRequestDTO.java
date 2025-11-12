package com.carlosvc.repaso_springboot.Discograficas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
public class DiscograficaRequestDTO {
    @NotBlank
    @Length(min = 5, max = 50,message = "El nombre debe de tener al menos 20 caracteres y maximo 50 ")
    private final String nombre;
    private final Boolean isDeleted;
}
