package com.andy.log.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Desensitization {
    // 需要过滤的方法信息配置列表
    private List<DesensitizationMethod> methods = new ArrayList<>();
}
