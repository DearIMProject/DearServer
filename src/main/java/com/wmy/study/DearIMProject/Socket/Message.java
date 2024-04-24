package com.wmy.study.DearIMProject.Socket;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmy.study.DearIMProject.Socket.message.SuccessContentJsonModel;
import com.wmy.study.DearIMProject.typeHandler.MessageEntityTypeHandler;
import com.wmy.study.DearIMProject.typeHandler.MessageStatusTypeHandler;
import com.wmy.study.DearIMProject.typeHandler.MessageTypeHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Data
@TableName(value = "tb_message", autoResultMap = true)
public class Message implements Cloneable {
    /*消息id*/
    @TableId(value = "msgId", type = IdType.AUTO)
    private Long msgId;
    private Long fromId;
    @TableField(typeHandler = MessageEntityTypeHandler.class)
    private MessageEntityType fromEntity;
    private Long toId;
    @TableField(typeHandler = MessageEntityTypeHandler.class)
    private MessageEntityType toEntity;
    private MessageEntityType entityType; // 这条消息是群消息还是私聊消息
    private Long entityId;// 消息对应的群id或userId
    /*消息内容*/
    private String content;
    @TableField(typeHandler = MessageTypeHandler.class)
    private MessageType messageType;
    private Long timestamp;
    @TableField(typeHandler = MessageStatusTypeHandler.class)
    private MessageStatus status;
    @TableLogic
    @JsonIgnore
    private boolean deleted;


    @Override
    public Message clone() {

        String json = null;
        Message clone;
        try {
            json = jsonString();
            clone = Message.fromJson(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    public static Message fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Message.class);
    }

    public String jsonString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

}
