package com.wmy.study.DearIMProject.Socket;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmy.study.DearIMProject.Socket.message.SuccessContentJsonModel;
import com.wmy.study.DearIMProject.typeHandler.MessageEntityTypeHandler;
import com.wmy.study.DearIMProject.typeHandler.MessageStatusTypeHandler;
import com.wmy.study.DearIMProject.typeHandler.MessageTypeHandler;
import lombok.Data;


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
    /*消息内容*/
    private String content;
    @TableField(typeHandler = MessageTypeHandler.class)
    private MessageType messageType;
    private Long timestamp;
    @TableField(typeHandler = MessageStatusTypeHandler.class)
    private MessageStatus status;

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
