package com.stir.RedisStudy.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RedisBrpopService {

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisBrpopService(RedisConnectionFactory connectionFactory) {
        this.stringRedisTemplate = new StringRedisTemplate(connectionFactory);
    }

    public void brpopExample(String listKey) {
        Thread thread = new Thread(() -> {
            while (true) {
                // BRPOP 명령어 실행 (블로킹)
                List<byte[]> result = stringRedisTemplate.execute((RedisCallback<List<byte[]>>) connection -> {
                    // Timeout을 0으로 지정하면 블로킹 상태에서 무한히 기다립니다.
                    return connection.bRPop(0, listKey.getBytes(StandardCharsets.UTF_8));
                });

                // 블로킹된 결과 처리
                if (result != null && !result.isEmpty()) {
                    byte[] poppedKeyBytes = result.get(0);
                    byte[] valueBytes = result.get(1);

                    String poppedKey = new String(poppedKeyBytes, StandardCharsets.UTF_8);
                    String value = new String(valueBytes, StandardCharsets.UTF_8);

                    System.out.println("Popped from " + poppedKey + ": " + value);
                }
            }
        });

        // 데몬 쓰레드로 실행
        thread.setDaemon(true);
        thread.start();
    }
}