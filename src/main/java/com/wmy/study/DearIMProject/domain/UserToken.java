package com.wmy.study.DearIMProject.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_token")
public class UserToken {
    private long uid;
    private String token;
    private long expireTime;
    @TableId
    private long id;
    private int isExpire;
}
