package com.andy.log.config;

import java.util.List;

import lombok.Data;

@Data
public class DesensitizationMethod {
    public static final Integer TYPE_REQEUEST = 0;
    public static final Integer TYPE_RESULT = 1;
    public static final Integer TYPE_ALL = 2;
    
    // 需要脱敏的方法
    private String methodName;
    // 需要脱敏的字段列表
    private List<String> columns;
    // 脱敏类型：0-请求参数；1-返回值；2-all
    private int type = 0;
}
