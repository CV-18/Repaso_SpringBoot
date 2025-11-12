package com.carlosvc.repaso_springboot.websockets.notifications.models;

public record Notificacion<A> (
        String entity,
        Tipo type,
        A data,
        String createdAt
){
    public enum Tipo {CREATED, UPDATE,DELETE}
}
