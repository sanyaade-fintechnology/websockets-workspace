package com.websockets.data;

import java.util.ArrayList;
import java.util.List;

public class StructuredMessage extends ChatMessage {

    protected List<String> dataList = new ArrayList<>();

    protected StructuredMessage(String type) {
        super(type);
    }

    public StructuredMessage(String type, List<String> dataList) {
        super(type);
        this.dataList = dataList;
    }

    public List<String> getList() {
        return dataList;
    }
}
