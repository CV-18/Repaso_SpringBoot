package com.carlosvc.repaso_springboot.graphql.controllers;


import com.carlosvc.repaso_springboot.rest.Albumes.models.Album;
import com.carlosvc.repaso_springboot.rest.Albumes.repository.AlbumRepository;
import com.carlosvc.repaso_springboot.rest.Discograficas.models.Discografica;
import com.carlosvc.repaso_springboot.rest.Discograficas.repositories.DiscograficasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AlbumDiscograficaGraphQLController {
    private final AlbumRepository albumRepository;
    private final DiscograficasRepository discograficasRepository;

    @QueryMapping
    public List<Album> albums(){
        return albumRepository.findAll();
    }

    @QueryMapping
    public List<Discografica> discograficas(){
        return discograficasRepository.findAll();
    }

    @QueryMapping
    public Discografica discograficaById(@Argument Long id){
        return discograficasRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Discografica> discograficasByNombre(@Argument String nombre){
        return discograficasRepository.findByNombreContainingIgnoreCase(nombre);
    }


    @SchemaMapping(typeName = "Album", field = "discografica")
    public Discografica discografica(Album album){
        return album.getDiscografica();
    }

    @SchemaMapping(typeName = "Discografica", field = "albums")
    public List<Album> albums(Discografica discografica){
        return discografica.getAlbumes();
    }

}
