package com.wmy.study.DearIMProject.dao;

import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Sqlite3Test {
    @Autowired
    IUserDao userDao;
    @Autowired
    IUserService userService;

    @Test
    public void userTest() {
        User user = userDao.selectById(1);
        User user1 = userService.getById(1);

        System.out.println(user);
        System.out.println(user1);
    }
}
