package com.carlosvc.repaso_springboot.websockets.notifications.dto;

public record AlbumNotificationResponse (
    Long id,
    String nombre,
    Integer anio,
    String banda,
    String genero,
    Double precio,
    String discografica,

    String createdAt,
    String updatedAt,
    String uuid

) {
}
