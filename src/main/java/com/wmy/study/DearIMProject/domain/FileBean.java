package com.wmy.study.DearIMProject.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmy.study.DearIMProject.Socket.message.SuccessContentJsonModel;
import lombok.Data;

@Data
@TableName("tb_file")
public class FileBean {
    @TableId(value = "fileId", type = IdType.AUTO)
    private Long fileId;
    /**
     * 注册时为userId 登录或者忘记密码时为userId+token
     */
    private String filePath;
    private String fileMd5;
    private int width;
    private int height;

    public static FileBean fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, FileBean.class);
    }

    public String jsonString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
