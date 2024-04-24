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

//    public static UserTokenChannel userTokenChannel;

    public UserTokenChannel() {
    }

    @PostConstruct
    public void init() {
//        userTokenChannel = this;
    }

    public void addChannelToToken(String token, Channel channel) {

        Channel findChannel = tokenChannelMap.get(token);
        if (!channel.equals(findChannel) && findChannel != null) {
            findChannel.close();
        }
        tokenChannelMap.put(token, channel);

        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("oldChannel" + channelFuture.channel() + " is closed!");
                boolean containsValue = tokenChannelMap.containsValue(channelFuture.channel());
                String findToken = null;
                for (Map.Entry<String, Channel> entry : tokenChannelMap.entrySet()) {
                    if (entry.getValue().equals(channel)) {
                        findToken = entry.getKey();
                        break;
                    }
                }// end of for
                if (findToken != null) {
                    tokenChannelMap.remove(findToken);
                    log.debug(tokenChannelMap.toString());
                }
            }
        });

    }

    public Channel getChannel(String token) {
        Channel channel = tokenChannelMap.get(token);
        return channel;
    }

}
