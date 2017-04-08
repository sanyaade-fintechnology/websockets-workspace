package com.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@Service
@ServerEndpoint("/echo")
public class EchoServer {

    private static Logger LOG = LoggerFactory.getLogger(EchoServer.class);

    @OnMessage
    public String echo(String incomingMessage) {
        LOG.info("Received message: " + incomingMessage);
        return "I got this (" + incomingMessage + ") so I'm sending it back!";
    }

    @OnOpen
    public void init(Session session) {
        LOG.info("Opening connection, session id: {}", session.getId());
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        LOG.info("Closing connection, reason: {}", closeReason.getCloseCode());
    }

    @OnError
    public void errorHandler(Throwable err) {
        LOG.error("WebSocket error!", err);
    }
}
