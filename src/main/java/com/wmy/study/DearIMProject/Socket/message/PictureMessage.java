package com.wmy.study.DearIMProject.Socket.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.domain.FileBean;

public class PictureMessage extends Message {
    public FileBean getFileBean() throws JsonProcessingException {
        return FileBean.fromJson(getContent());
    }
}
