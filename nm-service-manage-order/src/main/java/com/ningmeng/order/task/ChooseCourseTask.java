package com.ningmeng.order.task;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

public class ChooseCourseTask {

    // @Scheduled(fixedRate = 5000) //上次执行开始时间后5秒执行
    // @Scheduled(fixedDelay = 5000) //上次执行完毕后5秒执行
    // @Scheduled(initialDelay=3000, fixedRate=5000) //第一次延迟3秒，以后每隔5秒执行一次
    // 秒 分 时 日 月 周 年
    @Scheduled(cron = "0/3 * * * * ?")
    public void task1(){
        System.out.println(new Date());
    }

    @Scheduled(cron = "0/3 * * * * ?")
    public void task2(){
        System.out.println(new Date());
    }
}
