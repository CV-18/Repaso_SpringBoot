package com.carlosvc.repaso_springboot.rest.Albumes.services;

import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.models.Album;
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

    List<Album> buscarPorUsuarioId(Long usuarioId);
    Optional<Album>buscarPorId(Long id);

}
