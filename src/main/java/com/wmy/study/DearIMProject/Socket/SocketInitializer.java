package com.wmy.study.DearIMProject.Socket;

import com.wmy.study.DearIMProject.Socket.handler.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketInitializer extends ChannelInitializer<SocketChannel> {
    @Resource
    private LoginRequestHandler LOGIN_REQUESR_HANDLER;
    @Resource
    private HeartBeatRequestHandler HEART_BEAT_HANDLER;
    @Resource
    private ReadedMessageHandler READED_HANDLER;
    @Resource
    private MessageCodec MESSAGE_CODEC;
    LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
    @Resource
    private SingleChatHandler CHAT_HANDLER;

    @Resource
    private OfflineMessageRequestHandler OFFLINE_HANDLER;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new FrameMessaageCodec());
        pipeline.addLast(LOGGING_HANDLER);
        pipeline.addLast(MESSAGE_CODEC);
        pipeline.addLast(LOGIN_REQUESR_HANDLER);
        pipeline.addLast(HEART_BEAT_HANDLER);
        pipeline.addLast(OFFLINE_HANDLER);
        pipeline.addLast(READED_HANDLER);
        pipeline.addLast(CHAT_HANDLER);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("active: {}", ctx.channel().id().asShortText());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("inactive: {}", ctx.channel().id().asShortText());
        super.channelInactive(ctx);
    }
}


