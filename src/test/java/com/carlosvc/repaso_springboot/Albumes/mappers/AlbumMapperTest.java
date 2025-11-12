package com.carlosvc.repaso_springboot.Albumes.mappers;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumMapperTest {
    private final Discografica discografica = Discografica.builder().nombre("Black Light").build();
    private final AlbumMapper albumMapper = new AlbumMapper();

    @Test
    void toAlbum_create() {
        Long id = 1L;
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder()
                .nombre("The End")
                .anio(2004)
                .banda("Ad Hominem")
                .genero("Black Metal")
                .precio(15.90)
                .discografica("Black Light")
                .build();



        var resultado = albumMapper.toAlbum(albumCreateDto,discografica);

        assertAll(
                () -> assertEquals(id, resultado.getId()),
                () -> assertEquals(albumCreateDto.getNombre(), resultado.getNombre()),
                () -> assertEquals(albumCreateDto.getAnio(), resultado.getAnio()),
                () -> assertEquals(albumCreateDto.getBanda(), resultado.getBanda()),
                () -> assertEquals(albumCreateDto.getGenero(),resultado.getGenero()),
                () -> assertEquals(albumCreateDto.getDiscografica(), resultado.getDiscografica().getNombre()),
                () -> assertEquals(albumCreateDto.getPrecio(), resultado.getPrecio())

        );
    }

    @Test
    void ToAlbum_update() {
        Long id = 1L;
        AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder()
                .nombre("The End")
                .anio(2005)
                .banda("Ad Hominem")
                .genero("Black Metal")
                .precio(15.90)
                .build();

        Album album = Album.builder()
                .id(id)
                .nombre(albumUpdateDto.getNombre())
                .anio(albumUpdateDto.getAnio())
                .banda(albumUpdateDto.getBanda())
                .genero(albumUpdateDto.getGenero())
                .precio(albumUpdateDto.getPrecio())
                .build();

        var resultado = albumMapper.toAlbum(albumUpdateDto,album);

        assertAll(
                () -> assertEquals(id,resultado.getId()),
                () -> assertEquals(albumUpdateDto.getNombre(),resultado.getNombre()),
                () -> assertEquals(albumUpdateDto.getAnio(),resultado.getAnio()),
                () -> assertEquals(albumUpdateDto.getBanda(),resultado.getBanda()),
                () -> assertEquals(albumUpdateDto.getGenero(),resultado.getGenero()),
                () -> assertEquals(albumUpdateDto.getPrecio(),resultado.getPrecio())

        );
    }

    @Test
    void toAlbumResponseDto() {
        Album album = Album.builder()
                .id(1L)
                .nombre("The End")
                .anio(2004)
                .banda("Ad Hominem")
                .genero("Black Metal")
                .precio(15.90)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        var resultado = albumMapper.toAlbumResponseDto(album);

        assertAll(
                () -> assertEquals(album.getId(),resultado.getId()),
                () -> assertEquals(album.getNombre(),resultado.getNombre()),
                () -> assertEquals(album.getAnio(),resultado.getAnio()),
                () -> assertEquals(album.getBanda(), resultado.getBanda()),
                () -> assertEquals(album.getGenero(),resultado.getGenero()),
                () -> assertEquals(album.getPrecio(),resultado.getPrecio())

        );
    }

    @Test
    void testToAlbumResponseDto() {
    }
}