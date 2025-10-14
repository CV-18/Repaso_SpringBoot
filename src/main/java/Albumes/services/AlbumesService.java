package Albumes.services;

import Albumes.models.Album;


import java.util.List;

public interface AlbumesService {
    List<Album> findAll(String nombre, String banda);

    Album findById(Long id);

    Album findbyUuid(String uuid);

    Album save(Album album);

    Album update(Long id, Album album);

    void deleteById(Long id);

}
