package com.websockets;

import org.springframework.stereotype.Service;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

@Service
@ServerEndpoint("/echo")
public class EchoServer {

    @OnMessage
    public String echo(String incomingMessage) {
        return "I got this (" + incomingMessage + ") so I'm sending it back!";
    }
}
