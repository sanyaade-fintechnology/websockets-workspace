package com.websockets.data;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ChatEncoder implements Encoder.Text<ChatMessage> {
    public static final String SEPARATOR = ":";

    @Override
    public String encode(ChatMessage chatMessage) throws EncodeException {
        if (chatMessage instanceof StructuredMessage) {

            StringBuilder dataString = new StringBuilder(chatMessage.getType());

            for (String s : ((StructuredMessage) chatMessage).getList()) {
                dataString.append(SEPARATOR).append(s);
            }

            return dataString.toString();
        } else if (chatMessage instanceof BasicMessage) {
            return chatMessage.getType() + ((BasicMessage) chatMessage).getData();
        } else {
            throw new EncodeException(chatMessage, "Cannot encode messages of type: " + chatMessage.getClass());
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {}

    @Override
    public void destroy() {}
}
