package com.carlosvc.repaso_springboot.rest.Albumes.mappers;

import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.models.Album;
import com.carlosvc.repaso_springboot.rest.Discograficas.models.Discografica;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class AlbumMapper {
    public Album toAlbum(AlbumCreateDto albumCreateDto, Discografica discografica){
        return Album.builder()
                .id(null)
                .nombre(albumCreateDto.getNombre())
                .anio(albumCreateDto.getAnio())
                .banda(albumCreateDto.getBanda())
                .genero(albumCreateDto.getGenero())
                .precio(albumCreateDto.getPrecio())
                .discografica(discografica)
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
                .discografica(album.getDiscografica())
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
                .discografica(album.getDiscografica().getNombre())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .uuid(album.getUuid())
                .build();

    }

    public List<AlbumResponseDto> toAlbumResponseDtoList(List<Album> albums){
        return albums.stream()
                .map(this::toAlbumResponseDto)
                .toList();
    }

    public Page<AlbumResponseDto> toAlbumResponseDtoPage(Page<Album> albums){
        return albums.map(this::toAlbumResponseDto);
    }
}
