package com.wmy.study.DearIMProject.nettyStudy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class EchoClient {
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
}
