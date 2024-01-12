package com.wmy.study.DearIMProject.Socket;

import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.Utils.ByteBufferUtil;
import com.wmy.study.DearIMProject.dao.IMessageDao;
import com.wmy.study.DearIMProject.service.IMessageService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.lang.System.out;

@Slf4j
@Component
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {
    @Value("${application.magicNumber}")
    final int MAGIC_NUMBER = 891013;
    final int VERSION = 1;
    @Resource
    private IMessageService messageService;
//    private MessageCodec messageCodec;

    public MessageCodec() {
    }

    @PostConstruct
    public void init() {
//        messageCodec = this;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) throws Exception {
        ByteBuf out = channelHandlerContext.alloc().buffer();
        // 魔数
        out.writeInt(MAGIC_NUMBER);
        // 版本号
        out.writeByte(VERSION);
        // 序列化
        out.writeByte(ProtocolSerializeType.JSON.ordinal());
        // 消息id
        out.writeLong(message.getMsgId());
        // 消息类型
        out.writeInt(message.getMessageType().ordinal());
        // 时间戳
        out.writeLong(message.getTimestamp());
        // 发送方
        out.writeLong(message.getFromId());
        out.writeByte(message.getFromEntity().ordinal());
        // 接收方
        out.writeLong(message.getToId());
        out.writeByte(message.getToEntity().ordinal());
        // 获取内容字节数组
        byte[] bytes = message.getContent().getBytes();
        // 长度
        out.writeInt(bytes.length);
        // 写入消息内容
        out.writeBytes(bytes);
        ByteBuf buffer = channelHandlerContext.alloc().buffer();
        buffer.writeInt(out.readableBytes());
        buffer.writeBytes(out);
        list.add(buffer);
        //TODO: wmy 添加加密算法
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        //TODO: wmy 解密数据
        // 魔数
        int magicNumber = in.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            return;
        }
        // 版本号
        int version = in.readByte();
        if (version > VERSION) {
            throw new Exception(" message version is valid");
        }
        // 序列化
        int serialize = in.readByte();
        if (serialize != ProtocolSerializeType.JSON.ordinal()) {
            throw new Exception(" message serialize type is valid");
        }
        // 消息id
        long messageId = in.readLong();
        // 消息类型
        int messageType = in.readInt();
        Message message = MessageFactory.factoryWithMessageType(MessageType.fromInt(messageType));
        message.setMsgId(messageId);
        message.setMessageType(MessageType.fromInt(messageType));
        // 时间戳
        long timestamp = in.readLong();
        message.setTimestamp(timestamp);
        // 发送方
        long fromId = in.readLong();
        message.setFromId(fromId);
        MessageEntityType entityType = MessageEntityType.fromInt((int) in.readByte());
        message.setFromEntity(entityType);
        // 接收方
        long toId = in.readLong();
        message.setToId(toId);
        message.setToEntity(MessageEntityType.fromInt((int) in.readByte()));
        // 获取内容字节数组
        int length = in.readInt();
        byte[] contentByte = new byte[length];
        in.readBytes(contentByte, 0, length);
        String content = new String(contentByte, StandardCharsets.UTF_8);
        message.setContent(content);
        log.debug(message.toString());
        list.add(message);
    }
}
