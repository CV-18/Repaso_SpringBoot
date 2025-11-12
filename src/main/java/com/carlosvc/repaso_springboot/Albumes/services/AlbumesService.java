package com.carlosvc.repaso_springboot.Albumes.services;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;


import java.util.List;

public interface AlbumesService {
    List<AlbumResponseDto> findAll(String nombre, String banda);

    AlbumResponseDto findByGenero(String genero);

    AlbumResponseDto findById(Long id);

    AlbumResponseDto findbyUuid(String uuid);

    AlbumResponseDto save(AlbumCreateDto albumCreateDto);

    AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto);

    void deleteById(Long id);

}
