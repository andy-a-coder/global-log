package com.andy.log.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("global.log")
@Data
public class LogConfiguration {
    
    // 最大日志长度限制, 默认1024
    private int resultMaxLength = 1024;
    
    // 脱敏配置
    private Desensitization desensitization;
    
    // 跳过日志打印的方法配置
    private List<String> skipMethods = new ArrayList<>();
}
