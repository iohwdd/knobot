package com.iohw.knobot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: iohw
 * @date: 2025/5/5 11:35
 * @description: Web应用启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.iohw.knobot.**",})
@MapperScan(basePackages = {"com.iohw.knobot.*.mapper",})
public class KnobotWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnobotWebApplication.class, args);
    }

}
