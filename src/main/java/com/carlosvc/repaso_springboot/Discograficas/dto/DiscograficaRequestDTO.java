package com.carlosvc.repaso_springboot.Discograficas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DiscograficaRequestDTO {
    @NotBlank(message = "El nombre no puede estar vacio")
    @Length(min = 5, max = 50,message = "El nombre debe de tener al menos 5 caracteres y maximo 50 ")
    private  String nombre;
    private  Boolean isDeleted;
}
