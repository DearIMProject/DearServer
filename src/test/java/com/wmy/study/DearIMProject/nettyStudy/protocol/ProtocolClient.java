package com.wmy.study.DearIMProject.nettyStudy.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;

import java.util.Scanner;

@Slf4j
public class ProtocolClient {
    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        MessageCodec codec = new MessageCodec();
        ChannelFuture future = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LoggingHandler());
//                        nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0));
                        nioSocketChannel.pipeline().addLast(codec);
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                                super.channelRead(ctx, msg);
                            }
                        });

                    }
                }).connect("127.0.0.1", 8888);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

                Message message = new Message();
                message.setMsgId(123);
                message.setMessageType(MessageType.TEXT);
                message.setContent("一个消息体");
                message.setTimestamp(System.currentTimeMillis());
                message.setFromEntity(MessageEntityType.USER);
                message.setFromId(1);
                message.setToEntity(MessageEntityType.USER);
                message.setToId(2);
                ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
                System.out.println(buffer);
                log.debug("{}", buffer);
                codec.encode(null, message, buffer);
                channelFuture.channel().writeAndFlush(buffer);

//                new Thread(() -> {
//                    Scanner scanner = new Scanner(System.in);
//                    while (true) {
//                        String line = scanner.nextLine();
//                        if (":q".equals(line)) {
//                            log.debug("退出连接");
//                            channelFuture.channel().close();
//                        }
//                        channelFuture.channel().writeAndFlush(line);
//                    }
//                }).start();

                ChannelFuture closeFuture = channelFuture.channel().closeFuture();
                closeFuture.addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        log.debug("已关闭连接");
                        worker.shutdownGracefully();
                    }
                });
            }
        });
    }
}
