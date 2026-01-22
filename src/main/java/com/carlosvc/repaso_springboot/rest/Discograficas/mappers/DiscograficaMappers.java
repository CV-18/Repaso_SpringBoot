package com.carlosvc.repaso_springboot.rest.Discograficas.mappers;

import com.carlosvc.repaso_springboot.rest.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.rest.Discograficas.models.Discografica;
import org.springframework.stereotype.Component;

@Component
public class DiscograficaMappers {
    public Discografica toDiscografica(DiscograficaRequestDTO discograficaRequestDTO){
        return Discografica.builder()
                .id(null)
                .nombre(discograficaRequestDTO.getNombre())
                .build();
    }


    public Discografica toDiscografica(DiscograficaRequestDTO discograficaRequestDTO, Discografica discografica){
        return Discografica.builder()
                .id(discografica.getId())
                .nombre(discograficaRequestDTO.getNombre() != null ? discograficaRequestDTO.getNombre() : discografica.getNombre())
                .createdAt(discografica.getCreatedAt())
                .isDeleted(discograficaRequestDTO.getIsDeleted() != null ? discograficaRequestDTO.getIsDeleted() : discografica.getIsDeleted())
                .build();
    }
}
