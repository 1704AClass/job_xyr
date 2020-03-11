package com.ningmeng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        //定义key
        String key = "user_token:9734b68f‐cf5e‐456f‐9bd6‐df578c711390";
        //定义Map
        Map<String, String> mapValue = new HashMap<>();
        mapValue.put("id", "101");
        mapValue.put("username", "ningmeng");
        String value = JSON.toJSONString(mapValue);
        //一步设置过期时间
        stringRedisTemplate.boundValueOps(key).set(value, 60, TimeUnit.SECONDS);
        //读取过期时间，已过期返回‐2
        //longs三种状态： 1 正数 正在死亡 -1 不死亡 -2 已经死亡或者没有
        Long expire = stringRedisTemplate.getExpire(key);
        //根据key获取value
        String s = stringRedisTemplate.opsForValue().get(key);
        System.out.println(s);
    }



}
