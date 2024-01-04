package com.wmy.study.DearIMProject.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.domain.ResponseBean;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Message message = getMessage(fromUid, toUid, content);
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", toUid);
        List<UserToken> list = tokenService.list(wrapper);
        for (UserToken userToken : list) {
            String token = userToken.getToken();
            Channel channel = userTokenChannel.getChannel(token);
            if (channel != null) {
                log.info("找到一个channel");
                channel.writeAndFlush(message).sync();
            }
        }
        return new ResponseBean(true, null);
    }

    private Message getMessage(String fromUid, String toUid, String content) {
        Message message = MessageFactory.factoryWithMessageType(MessageType.CHAT_MESSAGE);
        message.setFromId(Long.parseLong(fromUid));
        message.setFromEntity(MessageEntityType.USER);
        message.setToId(Long.parseLong(toUid));
        message.setToEntity(MessageEntityType.USER);
        message.setContent(content);
        message.setMsgId(null);
        messageService.saveOnlineMessage(message);
        return message;
    }
}
