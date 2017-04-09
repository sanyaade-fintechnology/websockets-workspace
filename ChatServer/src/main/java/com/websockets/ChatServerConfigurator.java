package com.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class ChatServerConfigurator extends ServerEndpointConfig.Configurator {

    private static Logger LOG = LoggerFactory.getLogger(ChatServerConfigurator.class);

    private Transcript transcript;

    public ChatServerConfigurator() {
        transcript = new Transcript(20);
    }

    public Transcript getTranscript() {
        return transcript;
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        LOG.info("Handshake Request headers: {}", request.getHeaders());
        LOG.info("Handshake Response headers: {}", response.getHeaders());
    }
}
