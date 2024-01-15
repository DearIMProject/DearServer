package com.wmy.study.DearIMProject.Socket;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.wmy.study.DearIMProject.typeHandler.MessageEntityTypeHandler;
import com.wmy.study.DearIMProject.typeHandler.MessageStatusTypeHandler;
import com.wmy.study.DearIMProject.typeHandler.MessageTypeHandler;
import lombok.Data;


@Data
@TableName(value = "tb_message", autoResultMap = true)
public class Message {
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
}
