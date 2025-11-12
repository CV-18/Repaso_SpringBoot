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
        return Album.builder()
                .id(id)
                .nombre(albumCreateDto.getNombre())
                .anio(albumCreateDto.getAnio())
                .banda(albumCreateDto.getBanda())
                .genero(albumCreateDto.getGenero())
                .precio(albumCreateDto.getPrecio())
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Album toAlbum(AlbumUpdateDto albumUpdateDto, Album album){
        return Album.builder()
                .id(album.getId())
                .nombre(albumUpdateDto.getNombre() != null ? albumUpdateDto.getNombre() : album.getNombre())
                .anio(albumUpdateDto.getAnio() != null ? albumUpdateDto.getAnio() : album.getAnio())
                .banda(albumUpdateDto.getBanda() != null ? albumUpdateDto.getBanda() : album.getBanda())
                .genero(albumUpdateDto.getGenero() != null ? albumUpdateDto.getGenero() : album.getGenero())
                .precio(albumUpdateDto.getPrecio() != null ? albumUpdateDto.getPrecio() : album.getPrecio())
                .createdAt(album.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .uuid(album.getUuid())
                .build();
    }

    public AlbumResponseDto toAlbumResponseDto(Album album){
        return AlbumResponseDto.builder()
                .id(album.getId())
                .nombre(album.getNombre())
                .anio(album.getAnio())
                .banda(album.getBanda())
                .genero(album.getGenero())
                .precio(album.getPrecio())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .uuid(album.getUuid())
                .build();

    }

    public List<AlbumResponseDto> toAlbumResponseDto(List<Album> albums){
        return albums.stream()
                .map(this::toAlbumResponseDto)
                .toList();
    }
}
