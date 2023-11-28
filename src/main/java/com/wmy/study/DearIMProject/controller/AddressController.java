package com.wmy.study.DearIMProject.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.ResponseBean;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressbook")
public class AddressController {
    @Resource
    private IUserTokenService userTokenService;
    @Resource
    private IUserService userService;

    @ResponseBody
    @PostMapping("/list")
    public ResponseBean list(String token) {
        User user = userService.getFromToken(token);
        if (user != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user", user);
            return new ResponseBean(true, hashMap);
        }
        return new ResponseBean(false, ErrorCode.ERROR_CODE_RECORD_NOT_FOUND, null);
    }

    @ResponseBody
    @PostMapping("/all")
    public ResponseBean allAddress() {
        List<User> list = userService.list();
        if (list != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("list", list);
            return new ResponseBean(true, hashMap);
        }
        return new ResponseBean(false, ErrorCode.ERROR_CODE_RECORD_NOT_FOUND, null);
    }
}
