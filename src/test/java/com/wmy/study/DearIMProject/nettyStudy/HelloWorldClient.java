package com.wmy.study.DearIMProject.nettyStudy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class HelloWorldClient {
    public static void main(String[] args) throws InterruptedException {
//        simpleCreateClient();
        addListenerCreateClient();
    }

    static void addListenerCreateClient() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LoggingHandler());
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("127.0.0.1", 8888));
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Scanner scanner = new Scanner(System.in);
                ChannelFuture closedFuture = channelFuture.channel().closeFuture();
                new Thread(() -> {
                    while (true) {
                        String nextLine = scanner.nextLine();
                        if (":q".equals(nextLine)) {
                            channelFuture.channel().close();
                            break;
                        }
                        log.debug(nextLine);
                        channelFuture.channel().writeAndFlush(nextLine);
                    }// 读取输入
                }).start();
                closedFuture.addListeners(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        log.debug("channel已关闭，做关闭之后的操作");
                        group.shutdownGracefully();
                    }
                });
            }
        });

    }


    static void simpleCreateClient() throws InterruptedException {
        // 1. 启动类
        new Bootstrap()
                // 客户端的EventLoop
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());

                    }
                })
                // 连接到服务器
                .connect("127.0.0.1", 8888)
                // 知道连接建立才会往下执行
                .sync()
                // 代表连接对象
                .channel()

                .writeAndFlush("hello world");// 向服务器发送数据


    }


}


