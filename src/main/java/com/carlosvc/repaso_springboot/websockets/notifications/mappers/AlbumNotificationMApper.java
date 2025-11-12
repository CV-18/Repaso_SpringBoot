package com.carlosvc.repaso_springboot.websockets.notifications.mappers;

import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.carlosvc.repaso_springboot.websockets.notifications.dto.AlbumNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class AlbumNotificationMApper {
    public AlbumNotificationResponse toAlbumNotificationResponse(Album album) {
        return new AlbumNotificationResponse(
                album.getId(),
                album.getNombre(),
                album.getAnio(),
                album.getBanda(),
                album.getGenero(),
                album.getPrecio(),
                album.getDiscografica().getNombre(),
                album.getCreatedAt().toString(),
                album.getUpdatedAt().toString(),
                album.getUuid().toString()
        );
    }
}
