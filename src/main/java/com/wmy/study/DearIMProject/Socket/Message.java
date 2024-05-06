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
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
    private Long entityId;// 消息对应的群id或userId
    private MessageEntityType entityType; // 这条消息是群消息还是私聊消息
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

    @TableField(exist = false)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private List<Long> readList;

    @TableField(exist = false)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private String readUserIds;

    public void setReadList(List<Long> readList) {
        this.readList = readList;
        String result = readList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        setReadUserIds(result);
    }

    public void setReadUserIds(String readUserIds) {
        this.readUserIds = readUserIds;
        String[] numberStrings = readUserIds.split(",");

        // 创建一个Long类型的数组
        List<Long> longNumbers = new ArrayList<>();


        // 将字符串数组转换为Long数组
        for (String numberString : numberStrings) {
            if (numberString.length() != 0) {
                longNumbers.add(Long.parseLong(numberString));
            }

        }
        readList = longNumbers;
    }


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
