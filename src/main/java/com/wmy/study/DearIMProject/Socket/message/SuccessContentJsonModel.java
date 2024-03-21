package com.wmy.study.DearIMProject.Socket.message;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmy.study.DearIMProject.Socket.MessageType;
import lombok.Data;

import java.util.Map;

@Data
public class SuccessContentJsonModel {
    private Long msgId;
    private Long timestamp;
    private MessageType messageType;
    private String content;

    public SuccessContentJsonModel(Long msgId, Long timestamp, MessageType messageType) {
        this.msgId = msgId;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    public SuccessContentJsonModel(Long msgId, Long timestamp, MessageType messageType, String content) {
        this.msgId = msgId;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.content = content;
    }

    public SuccessContentJsonModel fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, SuccessContentJsonModel.class);
    }

    public String jsonString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
