package com.wmy.study.DearIMProject.Socket;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;

//@ChannelHandler.Sharable
//@Component
public class FrameMessaageCodec extends LengthFieldBasedFrameDecoder {
    public FrameMessaageCodec() {
        this(1024, 0, 4, 0, 4);
    }

//    private FrameMessaageCodec frameMessaageCodec;

//    @PostConstruct
//    public void init() {
//        frameMessaageCodec = this;
//    }

    public FrameMessaageCodec(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    public FrameMessaageCodec(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    public FrameMessaageCodec(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public FrameMessaageCodec(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }
}
