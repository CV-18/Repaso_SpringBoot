package Albumes.services;

import Albumes.dto.AlbumCreateDto;
import Albumes.dto.AlbumResponseDto;
import Albumes.dto.AlbumUpdateDto;
import Albumes.models.Album;


import java.util.List;

public interface AlbumesService {
    List<AlbumResponseDto> findAll(String nombre, String banda);

    AlbumResponseDto findById(Long id);

    AlbumResponseDto findbyUuid(String uuid);

    AlbumResponseDto save(AlbumCreateDto albumCreateDto);

    AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto);

    void deleteById(Long id);

}
