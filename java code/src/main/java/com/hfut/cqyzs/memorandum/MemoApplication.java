package com.hfut.cqyzs.memorandum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@MapperScan({"com.hfut.cqyzs.memorandum.mapper.*"})
@SpringBootApplication
public class MemoApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MemoApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(org.springframework.boot.builder.SpringApplicationBuilder builder) {
        return builder.sources(MemoApplication.class);
    }
}
