package com.wmy.study.DearIMProject.Socket.handler;

import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.Socket.message.ReadedMessage;
import com.wmy.study.DearIMProject.Socket.message.SuccessContentJsonModel;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ReadedMessageHandler extends SimpleChannelInboundHandler<ReadedMessage> {
    @Resource
    private IMessageService messageService;

    @Resource
    private UserTokenChannel userTokenChannel;

    @Resource
    private IUserService userService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ReadedMessage readedMessage) throws Exception {
        // 数据库添加已读状态
        String content = readedMessage.getContent();
        SuccessContentJsonModel jsonModel = SuccessContentJsonModel.fromJson(content);
        Long timestamp = jsonModel.getTimestamp();
        Long msgId = jsonModel.getMsgId();
        Message message = messageService.getById(msgId);
        log.debug("message" + message);
        messageService.sendReadedMessage(message, readedMessage.getFromId());
        // 发送给用户告知已读状态， 已读消息直接透传给对方
        List<UserToken> userTokens = userService.getUserTokens(readedMessage.getFromId());
        for (UserToken userToken : userTokens) {
            Channel channel = userTokenChannel.getChannel(userToken.getToken());
            if (channel != null) {
                channel.writeAndFlush(readedMessage);
            }
        }
//        // 已读消息放到消息库中
//        readedMessage.setMsgId(null);
//        messageService.save(readedMessage);

        // 给已读放发送已读消息回执
        Message successMsg = MessageFactory.factoryWithMessageType(MessageType.SEND_SUCCESS_MESSAGE);
        successMsg.setContent(String.valueOf(readedMessage.getTimestamp()));
        successMsg.setToId(readedMessage.getFromId());
        successMsg.setToEntity(MessageEntityType.USER);
        successMsg.setFromId(readedMessage.getToId());
        successMsg.setFromEntity(MessageEntityType.SERVER);
        successMsg.setMsgId(0L);
        successMsg.setEntityId(readedMessage.getToId());
        successMsg.setEntityType(MessageEntityType.USER);
        successMsg.setContent(new SuccessContentJsonModel(readedMessage.getMsgId(),
                readedMessage.getTimestamp(),
                readedMessage.getMessageType(),
                timestamp.toString()).jsonString());
        log.debug("success message:" + successMsg);
        channelHandlerContext.channel().writeAndFlush(successMsg).sync();
    }
}
