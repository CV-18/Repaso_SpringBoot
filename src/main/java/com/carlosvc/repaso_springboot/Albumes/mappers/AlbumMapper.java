package com.carlosvc.repaso_springboot.Albumes.mappers;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.models.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class AlbumMapper {
    public Album toAlbum(Long id, AlbumCreateDto albumCreateDto){
        return new Album(
                id,
                albumCreateDto.getNombre(),
                albumCreateDto.getAnio(),
                albumCreateDto.getBanda(),
                albumCreateDto.getGenero(),
                albumCreateDto.getPrecio(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID()
        );
    }

    public Album toAlbum(AlbumUpdateDto albumUpdateDto, Album album){
        return new Album(
                album.getId(),
                albumUpdateDto.getNombre() != null ? albumUpdateDto.getNombre() : album.getNombre(),
                albumUpdateDto.getAnio() != null ? albumUpdateDto.getAnio() : album.getAnio(),
                albumUpdateDto.getBanda() != null ? albumUpdateDto.getBanda() : album.getBanda(),
                albumUpdateDto.getGenero() != null ? albumUpdateDto.getGenero() : album.getGenero(),
                albumUpdateDto.getPrecio() != null ? albumUpdateDto.getPrecio() : album.getPrecio(),
                album.getCreatedAt(),
                LocalDateTime.now(),
                album.getUuid()
        );
    }

    public AlbumResponseDto toAlbumResponseDto(Album album){
        return new AlbumResponseDto(
                album.getId(),
                album.getNombre(),
                album.getAnio(),
                album.getBanda(),
                album.getGenero(),
                album.getPrecio(),
                album.getCreatedAt(),
                album.getUpdatedAt(),
                album.getUuid()
        );
    }

    public List<AlbumResponseDto> toAlbumResponseDto(List<Album> albums){
        return albums.stream()
                .map(this::toAlbumResponseDto)
                .toList();
    }
}
