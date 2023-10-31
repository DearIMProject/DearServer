package com.wmy.study.DearIMProject.nettyStudy.sticky;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class StickyClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LoggingHandler());
                        socketChannel.pipeline().addLast(new StringEncoder());
                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug(msg.toString());
                                super.channelRead(ctx, msg);
                            }
                        });
                    }

                }).connect("127.0.0.1", 8888);
        channelFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                // 发10次，但是服务端1次收取到
//                sendBigBytes(channelFuture);

                // 发一个大的
                ByteBuf buffer = channelFuture.channel().alloc().buffer();
                for (int i = 0; i < 10; i++) {
                    byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
                    buffer.writeBytes(bytes);
                }
                channelFuture.channel().writeAndFlush(buffer);
                channelFuture.channel().close();

                // console输入
                new Thread(() -> {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        String line = scanner.nextLine();
                        if (":q".equals(line)) {
                            log.debug("退出通信");
                            channelFuture.channel().close();
                        }
                        channelFuture.channel().writeAndFlush(line);
                    }
                }).start();
                // 关闭操作
                ChannelFuture closeFuture = channelFuture.channel().closeFuture();
                closeFuture.addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        log.debug("已关闭通信");
                        group.shutdownGracefully();
                    }
                });

            }
        });

    }

    private static void sendBigBytes(ChannelFuture channelFuture) {
        for (int i = 0; i < 10; i++) {
            byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
            ByteBuf buffer = channelFuture.channel().alloc().buffer();
            buffer.writeBytes(bytes);
            channelFuture.channel().writeAndFlush(buffer);
        }
    }
}
