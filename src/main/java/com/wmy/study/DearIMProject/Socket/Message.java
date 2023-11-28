package com.wmy.study.DearIMProject.Socket;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("tb_message")
public abstract class Message {
    /*消息id*/
    @TableId(value = "msgId", type = IdType.AUTO)
    private Long msgId;

    private Long fromId;
    private MessageEntityType fromEntity;
    private Long toId;
    private MessageEntityType toEntity;
    /*消息内容*/
    private String content;
    private MessageType messageType;
    private Long timestamp;
    // 消息状态 0: 发送成功，未读 1：发送成功，已读 2：未发送，未读
    private Integer status;
}
