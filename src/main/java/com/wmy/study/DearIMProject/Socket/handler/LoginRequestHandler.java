package com.wmy.study.DearIMProject.Socket.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.LoginRequestMessage;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.dao.IMessageDao;
import com.wmy.study.DearIMProject.dao.IUserDao;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@ChannelHandler.Sharable
@Component
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Resource
    private IUserTokenService tokenService;
    @Resource
    private UserTokenChannel tokenChannel;
    @Value("${application.magicNumber}")
    private int magicNumber;
    @Resource
    private IMessageDao messageDao;

//    private static LoginRequestHandler loginRequestHandler;

    public LoginRequestHandler() {
    }

    @PostConstruct
    public void init() {
//        loginRequestHandler = this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage loginRequestMessage) throws Exception {
        String token = loginRequestMessage.getContent();
        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        UserToken userToken = tokenService.getOne(queryWrapper);
        Message message = MessageFactory.factoryWithMessageType(MessageType.REQUEST_LOGIN);
        if (userToken == null) {
            message.setContent("error: cannot found token!");
        } else {
            message.setContent(Integer.toString(magicNumber));
            Channel channel = channelHandlerContext.channel();
            tokenChannel.addChannelToToken(token, channel);
        }
        channelHandlerContext.writeAndFlush(message);
    }
}
