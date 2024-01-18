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
        Long timestamp = Long.valueOf(content);
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
        // 给已读放发送已读消息回执
        Message successMsg = MessageFactory.factoryWithMessageType(MessageType.SEND_SUCCESS_MESSAGE);
        successMsg.setContent(String.valueOf(readMessage.getTimestamp()));
        successMsg.setToId(readMessage.getFromId());
        successMsg.setFromId(0L);
        successMsg.setFromEntity(MessageEntityType.SERVER);
        successMsg.setToEntity(MessageEntityType.USER);
        successMsg.setMsgId(0L);
        successMsg.setContent(new SuccessContentJsonModel(readedMessage.getMsgId(),
                readMessage.getTimestamp(),
                readMessage.getMessageType(),
                timestamp.toString()).jsonString());
        channelHandlerContext.channel().writeAndFlush(successMsg).sync();
    }
}
