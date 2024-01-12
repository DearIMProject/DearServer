package com.wmy.study.DearIMProject.Socket.handler;

import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.message.RequestOfflineMessage;
import com.wmy.study.DearIMProject.service.IMessageService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@ChannelHandler.Sharable
@Component
public class OfflineMessageRequestHandler extends SimpleChannelInboundHandler<RequestOfflineMessage> {
    @Resource
    private IMessageService messageService;

    @Override
    protected void channelRead0(ChannelHandlerContext context, RequestOfflineMessage message) throws Exception {
        //TODO: wmy 发送offlinemessage
        String token = message.getContent();
        List<Message> messages = messageService.getOfflineMessages(token);
        for (Message offlineMessage : messages) {
            context.channel().writeAndFlush(offlineMessage).sync();
        }
    }
}
