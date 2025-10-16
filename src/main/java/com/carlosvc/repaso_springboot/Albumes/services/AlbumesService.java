package com.carlosvc.repaso_springboot.Albumes.services;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumesResponseDto;
import com.carlosvc.repaso_springboot.Albumes.models.Album;


import java.util.List;

public interface AlbumesService {
    List<AlbumesResponseDto> findAll(String nombre, String banda);

    AlbumesResponseDto findById(Long id);

    AlbumesResponseDto findbyUuid(String uuid);

    AlbumesResponseDto save(AlbumesResponseDto albumUpdateDto);

    AlbumesResponseDto update(Long id, AlbumUpdateDto albumUpdateDto);

    void deleteById(Long id);

}
