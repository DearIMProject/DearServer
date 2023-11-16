package com.wmy.study.DearIMProject.Socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserTokenChannel {
    private final Map<String, Channel> tokenChannelMap = new HashMap<>();

    public static UserTokenChannel userTokenChannel;

    public UserTokenChannel() {
    }

    @PostConstruct
    public void init() {
        userTokenChannel = this;
    }

    public void addChannelToToken(String token, Channel channel) {

        Channel findChannel = tokenChannelMap.get(token);
        if (findChannel != null) {
            ChannelFuture closeFuture = findChannel.closeFuture();
            closeFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.debug("oldChannel" + channelFuture.channel() + " is closed!");
                }
            });
        }
        tokenChannelMap.put(token, channel);
    }
}
