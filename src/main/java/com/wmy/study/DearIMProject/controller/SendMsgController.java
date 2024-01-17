package com.wmy.study.DearIMProject.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.domain.ResponseBean;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
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
        Message message = getMessage(fromUid, toUid, content);
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", toUid);
        List<UserToken> list = tokenService.list(wrapper);


        boolean hasFindChannel = false;
        boolean hasSendMesssage = false;

        for (UserToken userToken : list) {
            String token = userToken.getToken();
            Channel channel = userTokenChannel.getChannel(token);
            if (channel != null) {
                log.info("找到一个channel");
                if (!hasSendMesssage) {
                    messageService.saveOnlineMessage(message);
                    hasSendMesssage = true;
                }
                channel.writeAndFlush(message).sync();
                hasFindChannel = true;
            }
        }
        if (!hasFindChannel) {
            messageService.saveOfflineMessage(message);
        }

        return new ResponseBean(true, null);
    }

    @PostMapping("/offlineMessage")
    public ResponseBean getOfflineMessage(String token) {
        List<Message> offlineMessages = messageService.getOfflineMessages(token, 0L);
        HashMap<String, Object> map = new HashMap<>();
        map.put("messages", offlineMessages);
        return new ResponseBean(true, map);
    }

    @PostMapping("/sendReadedMessage")
    public ResponseBean sendReadedMessage(Long timestamp) throws BusinessException {
        Message message = messageService.getMessageByTimestamp(timestamp);
        log.debug("message" + message);
        messageService.setReaded(timestamp);
        // 发送给用户告知已读状态
        List<UserToken> userTokens = userService.getUserTokens(message.getFromId());
        boolean isSend = false;
        Message readMessage = MessageFactory.factoryWithMessageType(MessageType.READED_MESSAGE);
        readMessage.setFromId(message.getFromId());
        readMessage.setFromEntity(message.getFromEntity());
        readMessage.setToEntity(message.getToEntity());
        readMessage.setToId(message.getToId());
        readMessage.setMsgId(message.getMsgId());
        readMessage.setContent(timestamp.toString());
        for (UserToken userToken : userTokens) {
            Channel channel = userTokenChannel.getChannel(userToken.getToken());
            if (channel != null) {
                channel.writeAndFlush(readMessage);
                isSend = true;
            }
        }
        // 如果用户不在线，则放到消息库中
        if (!isSend) {
            readMessage.setMsgId(null);
            messageService.save(readMessage);
        }
        return new ResponseBean(true, null);
    }

    private Message getMessage(String fromUid, String toUid, String content) {
        Message message = MessageFactory.factoryWithMessageType(MessageType.TEXT);
        message.setFromId(Long.parseLong(fromUid));
        message.setFromEntity(MessageEntityType.USER);
        message.setToId(Long.parseLong(toUid));
        message.setToEntity(MessageEntityType.USER);
        message.setContent(content);
        message.setMsgId(null);
        return message;
    }
}
