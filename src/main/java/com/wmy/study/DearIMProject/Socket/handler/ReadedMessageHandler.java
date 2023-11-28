package com.wmy.study.DearIMProject.Socket.handler;

import com.wmy.study.DearIMProject.Socket.message.ReadedMessage;
import com.wmy.study.DearIMProject.service.IMessageService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ReadedMessageHandler extends SimpleChannelInboundHandler<ReadedMessage> {
    @Resource
    private IMessageService messageService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ReadedMessage readedMessage) throws Exception {
        // 数据库添加已读状态
        String content = readedMessage.getContent();
        Long messageId = Long.valueOf(content);
        messageService.setReaded(messageId);
    }
}
