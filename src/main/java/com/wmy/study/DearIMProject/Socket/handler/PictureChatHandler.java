package com.wmy.study.DearIMProject.Socket.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.Socket.message.PictureMessage;
import com.wmy.study.DearIMProject.Socket.message.SuccessContentJsonModel;
import com.wmy.study.DearIMProject.domain.FileBean;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IUploadFileService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
@Component
public class PictureChatHandler extends SimpleChannelInboundHandler<PictureMessage> {
    @Resource
    private IUserTokenService userTokenService;
    @Resource
    private UserTokenChannel userTokenChannel;
    @Resource
    private IMessageService messageService;
    @Resource
    private IUploadFileService uploadFileService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PictureMessage message) throws Exception {
        // 查找user对应的channel
        //TODO: wmy 将发送的信息图片保存下来，返回给用户
        FileBean fileBean = message.getFileBean();


        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", message.getToId());
        log.debug("发送给的uid:" + message.getToId());
        List<UserToken> list = userTokenService.list(queryWrapper);
        boolean hasSendMsg = false;
        Channel findChannel = null;
        for (UserToken userToken : list) {
            String token = userToken.getToken();
            Channel channel = userTokenChannel.getChannel(token);
            if (channel != null) {
                hasSendMsg = true;
                log.debug("找到用户userToken 发送信息" + channel);
                findChannel = channel;
                if (!channelHandlerContext.channel().equals(findChannel)) {
                    findChannel.writeAndFlush(message);
                }
                break;
            }
        }
        message.setMsgId(null);

        // 给原用户发送信息，标识信息已收到
        Message successMsg = MessageFactory.factoryWithMessageType(MessageType.SEND_SUCCESS_MESSAGE);
        successMsg.setContent(String.valueOf(message.getTimestamp()));
        successMsg.setToId(message.getFromId());
        successMsg.setToEntity(MessageEntityType.USER);

        // 添加到数据库中
        if (!hasSendMsg) {
            messageService.saveOfflineMessage(message);
        } else {
            messageService.saveOnlineMessage(message);
        }
        successMsg.setContent(new SuccessContentJsonModel(message.getMsgId(),
                message.getTimestamp(),
                message.getMessageType(),
                String.valueOf(message.getTimestamp())).jsonString());
        successMsg.setMsgId(message.getMsgId());
        successMsg.setFromEntity(MessageEntityType.SERVER);
        successMsg.setFromId(0L);
        channelHandlerContext.writeAndFlush(successMsg);
    }
}
