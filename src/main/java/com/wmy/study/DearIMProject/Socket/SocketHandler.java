package com.wmy.study.DearIMProject.Socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketHandler extends ChannelInboundHandlerAdapter {
    public static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        log.info("收到消息：" + new String(data));
        for (Channel channel : clients) {
//            if (!channel.equals(ctx.channel())) {
            channel.writeAndFlush(data);
//            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("收到新链接：" + ctx.channel().id().asShortText());
        clients.add(ctx.channel());
//        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("链接断开：" + ctx.channel().id().asShortText());
        clients.remove(ctx.channel());
//        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        clients.remove(ctx.channel());
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }
}
