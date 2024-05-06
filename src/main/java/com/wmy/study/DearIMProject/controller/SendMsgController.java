package com.wmy.study.DearIMProject.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.domain.FileBean;
import com.wmy.study.DearIMProject.domain.ResponseBean;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/message")
public class SendMsgController {
    @Resource
    private UserTokenChannel userTokenChannel;
    @Resource
    private IUserTokenService tokenService;

    @Resource
    private IUserService userService;

    @Resource
    private IMessageService messageService;

    /**
     * 发送一个消息，用于模拟发送消息
     *
     * @param fromUid 发送方
     * @param toUid   接收方
     * @param content 消息内容
     * @return 返回
     */
    @PostMapping("/send")
    public ResponseBean sendMessage(String fromUid, String toUid, String content) throws InterruptedException {
        messageService.sendMessage(fromUid, toUid, content, MessageType.TEXT);
        return new ResponseBean(true, null);
    }

    @PostMapping("/offlineMessage")
    public ResponseBean getOfflineMessage(String token) {
        List<Message> offlineMessages = messageService.getOfflinePersonalMessages(token, 0L);
        HashMap<String, Object> map = new HashMap<>();
        map.put("messages", offlineMessages);
        return new ResponseBean(true, map);
    }

    @PostMapping("/sendImage")
    public ResponseBean sendImage(String fromUid, String toUid, String imageUrl) throws JsonProcessingException, InterruptedException {
        FileBean fileBean = new FileBean();
        String content = fileBean.jsonString();
        messageService.sendMessage(fromUid, toUid, content, MessageType.PICTURE);
        return new ResponseBean(true, null);
    }

//    @PostMapping("/sendReadedMessage")
//    public ResponseBean sendReadedMessage(Long timestamp, Long fromId) throws BusinessException, JsonProcessingException {
//        messageService.sendReadedMessage(timestamp, fromId);
//        return new ResponseBean(true, null);
//    }
}

