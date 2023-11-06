package com.wmy.study.DearIMProject.Socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;

@Slf4j
@Component
public class SocketServer {
    @Resource
    private SocketInitializer socketInitializer;
    @Getter
    private ServerBootstrap serverBootstrap;
    //TODO: wmy 这里无法读取yml的数据，后期需要看看如何设置
    @Value("${netty.port:9999}")
    private int port;
    @Value("${netty.boss:4}")
    private int bossThread;
    @Value("${netty.worker:2}")
    private int workerThread;

    public void init() {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            log.debug("当前模式为BIG_ENDIAN");
        } else {
            log.debug("当前模式为LITTLE_ENDIAN");
        }
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(bossThread);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(workerThread);
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(socketInitializer);

    }

    public void start() {
        init();
        serverBootstrap.bind(port);
        log.info("netty 启动，port {}(tcp),boss thread:{} worker thread: {}", port, bossThread, workerThread);
    }

}
