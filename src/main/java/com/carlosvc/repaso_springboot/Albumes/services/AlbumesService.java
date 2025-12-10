package com.carlosvc.repaso_springboot.Albumes.services;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;

public interface AlbumesService {
    Page<AlbumResponseDto> findAll(Optional<String> nombre, Optional<String> discografica, Optional<Boolean> isDeleted, Pageable pageable);

    AlbumResponseDto findById(Long id);

    AlbumResponseDto findbyUuid(String uuid);

    AlbumResponseDto save(AlbumCreateDto albumCreateDto);

    AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto);

    void deleteById(Long id);

}
