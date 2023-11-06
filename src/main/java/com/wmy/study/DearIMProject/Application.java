package com.wmy.study.DearIMProject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SpringBootApplication
@MapperScan("com.wmy.study.DearIMProject.dao")
@ConfigurationProperties(prefix = "netty.config")
public class Application {

    public static void main(String[] args) {
        // 加载各种bean
        SpringApplication.run(Application.class, args);
    }
}
