package com.wmy.study.DearIMProject.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_token")
public class UserToken {
    @TableId(value = "tokenId", type = IdType.AUTO)
    private Long tokenId;
    private Long uid;
    private String token;
    private Long expireTime;
    private Integer isExpire;
    @TableLogic
    private Boolean deleted;
    /**
     * 机型
     */
    private String os;
}
