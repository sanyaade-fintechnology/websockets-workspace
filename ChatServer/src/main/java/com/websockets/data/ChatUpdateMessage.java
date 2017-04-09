package com.websockets.data;

public class ChatUpdateMessage extends StructuredMessage {
    public ChatUpdateMessage(String username, String message) {
        super(CHAT_DATA_MESSAGE);
        super.dataList.add(username);
        super.dataList.add(message);
    }

    public String getUsername() {
        return super.getList().get(0);
    }

    public String getMessage() {
        return super.getList().get(1);
    }
}
