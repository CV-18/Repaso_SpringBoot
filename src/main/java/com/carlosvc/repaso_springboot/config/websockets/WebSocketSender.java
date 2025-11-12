package com.carlosvc.repaso_springboot.config.websockets;

import java.io.IOException;

public interface WebSocketSender {
    void sendMessage(String message) throws IOException;

    void sendPeriodicMessages() throws IOException;
}
