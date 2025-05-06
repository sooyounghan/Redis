package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        // Lettuce 라는 라이브러리를 통해 Redis 연결 관리 객체 생성
        // Redis 서버에 대한 정보 (Host, Port) 설정
        return new LettuceConnectionFactory(host, port);
    }
}
