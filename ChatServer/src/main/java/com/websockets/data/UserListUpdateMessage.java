package com.websockets.data;

import java.util.List;

public class UserListUpdateMessage extends StructuredMessage {
    public UserListUpdateMessage(List<String> usernames) {
        super(USERLIST_UPDATE, usernames);
    }

    public List<String> getUserList() {
        return super.dataList;
    }
}
