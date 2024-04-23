package com.sky.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/buy")
    public String buy(){
        String key = "num";
        List<String> keys = Collections.singletonList(key);

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new PathResource("sky-server/src/main/test/java/com/sky/test.lua")));
        script.setResultType(Long.class);

        Long result = stringRedisTemplate.execute(script, keys, "10");

        if (result == null) {
            return "系统出错";
        } else if (result == 0L) {
            return "数量不足";
        } else if (result == 1L) {
            return "购买成功";
        }
        return "数量不足";
    }

}
