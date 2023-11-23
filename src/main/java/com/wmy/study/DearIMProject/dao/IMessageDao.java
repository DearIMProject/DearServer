package com.wmy.study.DearIMProject.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wmy.study.DearIMProject.Socket.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IMessageDao extends BaseMapper<Message> {
}
