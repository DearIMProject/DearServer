package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.dao.IMessageDao;
import com.wmy.study.DearIMProject.dao.IMsgStatusDao;
import com.wmy.study.DearIMProject.domain.MsgStatus;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IMsgStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MsgStatusServiceImpl extends ServiceImpl<IMsgStatusDao, MsgStatus> implements IMsgStatusService {

    @Override
    public List<Long> getMessageReaded(Long msgId) {
        log.info("getMessageReaded: {}", msgId);
        QueryWrapper<MsgStatus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("msg_id", msgId);
        ArrayList<Long> longArrayList = new ArrayList<>();
        for (MsgStatus msgStatus : list(queryWrapper)) {
            longArrayList.add(msgStatus.getUserId());
        }
        return longArrayList;
    }
}
