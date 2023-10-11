package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.SecurityCode;

public interface ISecurityCodeService extends IService<SecurityCode> {
    /**
     * 生成验证码
     *
     * @param uniKey
     * @return
     */
    SecurityCode generateSecurityCode(String uniKey);

    /**
     * 判断验证码是否要发送
     *
     * @param code
     * @return
     */
    SecurityCode checkAndSave(SecurityCode code);

    /**
     * 判断验证码是否正确
     *
     * @param email
     * @param code
     * @return
     */
    boolean checkCodeIsRight(String email, String code) throws BusinessException;

    /**
     * 再次发送验证码
     *
     * @param email
     * @return
     */
    SecurityCode sendCheckCode(String email) throws BusinessException;
}
