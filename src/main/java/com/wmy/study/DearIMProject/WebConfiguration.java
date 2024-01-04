package com.wmy.study.DearIMProject;

import com.wmy.study.DearIMProject.interceptor.LoginInterceptor;
import com.wmy.study.DearIMProject.interceptor.RequestLogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Bean
    public RequestLogInterceptor requestLogInterceptor() {
        return new RequestLogInterceptor();
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(requestLogInterceptor()).addPathPatterns("/*/**");

        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/*/**")
//                .excludePathPatterns("/hello/method1")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("user/sendCheckCode")
                .excludePathPatterns("/user/checkCode")
                .excludePathPatterns("/message/send");


//        registry.addInterceptor(recordInterceptor())
//                .addPathPatterns("/record/*")
//                .excludePathPatterns("/record/queryBookRecords")
//                .excludePathPatterns("/record/add");
//
//        registry.addInterceptor(bookInterceptor())
//                .addPathPatterns("/book/*")
//                .excludePathPatterns("/book/add")
//                .excludePathPatterns("/book/queryDefault")
//                .excludePathPatterns("/book/queryAll");
    }
}
