package com.carlosvc.repaso_springboot.Discograficas.mappers;

import com.carlosvc.repaso_springboot.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import org.springframework.stereotype.Component;

@Component
public class DiscograficaMappers {
    public Discografica toDiscografica(DiscograficaRequestDTO discograficaRequestDTO){
        return Discografica.builder()
                .id(null)
                .nombre(discograficaRequestDTO.getNombre())
                .build();
    }


    public Discografica toDiscografica(DiscograficaRequestDTO discoDTO, Discografica discografica){
        return Discografica.builder()
                .id(discografica.getId())
                .nombre(discoDTO.getNombre() != null ? discoDTO.getNombre() : discografica.getNombre())
                .createdAt(discografica.getCreatedAt())
                .updatedAt(discografica.getUpdatedAt())
                .isDeleted(discoDTO.getIsDeleted() != null ? discoDTO.getIsDeleted() : discografica.getIsDeleted())
                .build();
    }
}
