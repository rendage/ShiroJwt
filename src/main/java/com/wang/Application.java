package com.wang;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author Wang926454
 * @date 2018/8/9 15:42
 */
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
@tk.mybatis.spring.annotation.MapperScan("com.wang.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
