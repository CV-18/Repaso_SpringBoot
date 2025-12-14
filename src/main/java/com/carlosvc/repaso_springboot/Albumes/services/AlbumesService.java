package com.carlosvc.repaso_springboot.Albumes.services;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

public interface AlbumesService {
    Page<AlbumResponseDto> findAll(Optional<String> nombre, Optional<String> discografica, Optional<Boolean> isDeleted, Pageable pageable);

    List<AlbumResponseDto> findByBanda(String banda);

    AlbumResponseDto findById(Long id);


    Page<AlbumResponseDto>findByUsuarioId(Long usuarioId,Pageable pageable);
    AlbumResponseDto findByUsuarioId(Long usuarioId, Long idAlbum);


    AlbumResponseDto findbyUuid(String uuid);

    AlbumResponseDto save(AlbumCreateDto albumCreateDto);
    AlbumResponseDto save(AlbumCreateDto albumCreateDto, Long usuarioId);


    AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto);
    AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto, Long usuarioId);


    void deleteById(Long id);
    void deleteById(Long id, Long usuarioId);

}
