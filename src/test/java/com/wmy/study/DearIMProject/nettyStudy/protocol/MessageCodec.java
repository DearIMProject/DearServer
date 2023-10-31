package com.wmy.study.DearIMProject.nettyStudy.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import static java.lang.System.out;


public class MessageCodec extends ByteToMessageCodec<Message> {
    final int MAGIC_NUMBER = 891013;
    final int VERSION = 1;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf out) throws Exception {
        // 魔数
        out.writeInt(MAGIC_NUMBER);
        // 版本号
        out.writeByte(VERSION);
        // 序列化
        out.writeByte(ProtocolSerializeType.JSON.ordinal());
        // 消息id
        out.writeLong(message.getMsgId());
        // 消息类型
        out.writeByte(message.getMessageType().ordinal());
        // 时间戳
        out.writeLong(message.getTimestamp());
        // 发送方
        out.writeLong(message.getFromId());
        out.writeByte(message.getFromEntity().ordinal());
        // 接收方
        out.writeLong(message.getToId());
        out.writeByte(message.getToEntity().ordinal());
        // 获取内容字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message.getContent());
        byte[] bytes = bos.toByteArray();
        // 长度
        out.writeByte(bytes.length);
        // 写入消息内容
        out.writeBytes(bytes);
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
        int serialize = in.readByte();
        if (serialize != ProtocolSerializeType.JSON.ordinal()) {
            throw new Exception(" message serialize type is valid");
        }
        Message message = new Message();
        // 消息id
        long messageId = in.readLong();
        message.setMsgId(messageId);
        // 消息类型
        int messageType = in.readByte();
        message.setMessageType(MessageType.fromInt(messageType));
        // 时间戳
        long timestamp = in.readLong();
        message.setTimestamp(timestamp);
        // 发送方
        long fromId = in.readLong();
        message.setFromId(fromId);
        MessageEntityType entityType = MessageEntityType.fromInt(in.readByte());
        message.setFromEntity(entityType);
        // 接收方
        long toId = in.readLong();
        message.setToId(toId);
        message.setToEntity(MessageEntityType.fromInt(in.readByte()));
        // 获取内容字节数组
        int length = in.readByte();
        byte[] contentByte = new byte[length];
        in.readBytes(contentByte, 0, length);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(contentByte));
        // 消息内容
        message.setContent(ois.readObject().toString());
        System.out.println(message.toString());
        list.add(message);

    }
}
