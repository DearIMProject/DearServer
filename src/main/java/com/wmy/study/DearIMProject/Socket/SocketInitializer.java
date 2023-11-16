package com.wmy.study.DearIMProject.Socket;

import com.wmy.study.DearIMProject.Socket.handler.LoginRequestHandler;
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

    private MessageCodec MESSAGE_CODEC = new MessageCodec();
    LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(LOGGING_HANDLER);
        pipeline.addLast(MESSAGE_CODEC);
        pipeline.addLast(LOGIN_REQUESR_HANDLER);
        //TODO: wmy 创建一个LoginRequestMessage进行处理

        pipeline.addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ctx.writeAndFlush(msg);
                super.channelRead(ctx, msg);
            }
        });
    }
}
