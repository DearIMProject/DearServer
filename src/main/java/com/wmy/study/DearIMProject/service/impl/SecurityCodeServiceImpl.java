package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.dao.ISecurityCodeDao;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.SecurityCode;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.service.ISecurityCodeService;
import com.wmy.study.DearIMProject.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SecurityCodeServiceImpl extends ServiceImpl<ISecurityCodeDao, SecurityCode>
        implements ISecurityCodeService {
    @Autowired
    private ISecurityCodeDao codeDao;
    @Autowired
    private IUserService userService;

    @Override
    public SecurityCode generateSecurityCode(String uniKey) {
        // 生成6位的验证码
        int result = (int) ((Math.random() * 9 + 1) * 100000);
        String code = result + "";
        SecurityCode securityCode = new SecurityCode();
        securityCode.setUniKey(uniKey);
        securityCode.setCode(code);
        // 5分钟过期
        securityCode.setExpireTime(new Date().getTime() + 5 * 60 * 1000);
        int count = codeDao.insert(securityCode);
        if (count != 0) {
            return securityCode;
        }
        return null;
    }

    @Override
    public SecurityCode checkAndSave(SecurityCode code) {
        // 判断是否已发送过
        QueryWrapper<SecurityCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uni_key", code.getCode());
        SecurityCode one = getOne(queryWrapper);
        // 找到且未过期
        if (one != null && one.getExpireTime() > new Date().getTime()) {
            log.debug("one = " + one.toString());
            return one;
        }
        if (one != null) {
            // 不为空时
            log.debug("one 被删除");
            removeById(one.getCodeId());
        }
        SecurityCode securityCode = generateSecurityCode(code.getUniKey());

        if (securityCode != null) {
            log.debug("isSaveSuccess = true");
            log.debug(securityCode.toString());
            return securityCode;
        }
        log.debug("isSaveSuccess = false");
        return null;
    }

    @Override
    public boolean checkCodeIsRight(String email, String code) throws BusinessException {
        if (email == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "email is empty!");
        }
        if (code == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "code is empty!");
        }
        QueryWrapper<SecurityCode> wrapper = new QueryWrapper<>();
        wrapper.eq("uni_key", email);
        SecurityCode one = getOne(wrapper);
        if (one != null
                && one.getExpireTime() > new Date().getTime() // 未过期
                && one.getCode().equals(code)) {
            removeById(one.getCodeId());
            // 若为注册验证码
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("email", email);
            User user = userService.getOne(userQueryWrapper);
            if (user.getStatus() == 0) {
                user.setStatus(1);
                return userService.updateById(user);
            }
        }
        return false;
    }

    @Override
    public SecurityCode sendCheckCode(String email) throws BusinessException {
        if (email == null || email.isEmpty()) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "email 为空");
        }
        QueryWrapper<SecurityCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uni_key", email);
        SecurityCode securityCode = getOne(queryWrapper);
        if (securityCode != null) {
            // 已经有发过code 需要刷新
            removeById(securityCode.getCodeId());
        }
        return generateSecurityCode(email);
    }
}
