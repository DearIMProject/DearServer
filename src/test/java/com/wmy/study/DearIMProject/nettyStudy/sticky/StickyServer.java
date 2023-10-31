package com.wmy.study.DearIMProject.nettyStudy.sticky;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LoggingHandler;

// netty入门学习
public class StickyServer {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 1. 服务器端启动器
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 2. selector thread
        bootstrap.group(group);
        // 接收缓冲区改成10字节
        bootstrap.option(ChannelOption.SO_RCVBUF, 10);
        // 3. 选择服务器的ssc实现
        bootstrap.channel(NioServerSocketChannel.class)
                // 4. 决定child可以处理那些操作
                .childHandler(
                        //客户端进行数据读写的通道，负责添加别的handler 添加初始化器
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new LoggingHandler());
                                // 将传输来的ByteBuf转为字符串
                                ch.pipeline().addLast(new StringDecoder());
                                // 自定义的handler
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println("h1");
//                                        System.out.println(msg);
                                        super.channelRead(ctx, msg);
                                    }
                                });

                            }
                        })
                .bind(8888);// 监听端口
    }
}
