package com.wmy.study.DearIMProject.Socket.handler;

import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.DeleteMessage;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
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
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

// 删除消息
@Component
@ChannelHandler.Sharable
public class DeleteMessageHandler extends SimpleChannelInboundHandler<DeleteMessage> {
    @Resource
    private UserTokenChannel userTokenChannel;
    @Resource
    private IUserService userService;
    @Resource
    private IMessageService messageService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DeleteMessage deleteMessage) throws Exception {
        SuccessContentJsonModel jsonModel = SuccessContentJsonModel.fromJson(deleteMessage.getContent());
        boolean success = messageService.removeByTimestamp(jsonModel.getTimestamp());
        if (success) {
            // 给原用户发送信息，标识信息已收到
            Message successMsg = MessageFactory.factoryWithMessageType(MessageType.SEND_SUCCESS_MESSAGE);
            successMsg.setContent(String.valueOf(deleteMessage.getTimestamp()));
            successMsg.setToId(deleteMessage.getFromId());
            successMsg.setToEntity(MessageEntityType.USER);
            channelHandlerContext.channel().writeAndFlush(successMsg);
            // 给其他相同的用户发送信息
            List<UserToken> userTokens = userService.getUserTokens(deleteMessage.getFromId());
            if (userTokens != null) {
                for (UserToken userToken : userTokens) {
                    Channel channel = userTokenChannel.getChannel(userToken.getToken());
                    if (channel != null && !channel.equals(channelHandlerContext)) {
                        channel.writeAndFlush(deleteMessage);
                    }

                }
            }
        }
    }
}
