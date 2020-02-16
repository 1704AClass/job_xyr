package com.ningmeng.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//扫描类
@ComponentScan(basePackages = {"com.ningmeng.framework"})
@ComponentScan(basePackages = {"com.ningmeng.test"})
public class ManageCmsApplication {
    public static void main(String[]args){
        SpringApplication.run(ManageCmsApplication.class,args);
    }
}
