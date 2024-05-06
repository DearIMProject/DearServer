package com.wmy.study.DearIMProject.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_msg_status")
public class MsgStatus {
    @TableId(value = "status_id", type = IdType.AUTO)
    private Long statusId;
    /**
     * 消息id
     */
    private Long msgId;
    /**
     * 用户id
     */
    private Long userId;
}
