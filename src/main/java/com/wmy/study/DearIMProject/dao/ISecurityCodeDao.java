package com.wmy.study.DearIMProject.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wmy.study.DearIMProject.domain.SecurityCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ISecurityCodeDao extends BaseMapper<SecurityCode> {
}
