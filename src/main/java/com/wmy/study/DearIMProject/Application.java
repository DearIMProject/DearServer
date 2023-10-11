package com.wmy.study.DearIMProject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wmy.study.DearIMProject.dao")
public class Application {

    public static void main(String[] args) {
        // 加载各种bean
        SpringApplication.run(Application.class, args);


    }

}
