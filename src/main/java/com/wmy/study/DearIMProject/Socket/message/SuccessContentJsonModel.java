package com.wmy.study.DearIMProject.Socket.message;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmy.study.DearIMProject.Socket.MessageType;
import lombok.Data;

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

    public String jsonString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
