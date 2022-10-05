package com.andy.log.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import com.andy.log.config.DesensitizationMethod;
import com.andy.log.config.LogConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * 日志处理器
 * 
 */
@Slf4j(topic = "AccessLog")
@Configuration
public class LogTools {

    // 脱敏替换表达式定义
    private static String[][] patterns = {
            { "=", "(%s=).*?(,)" },
            { ")", "(%s=).*?(\\))" },
            { ":", "(\"%s\":).*?(,)" },
            { "}", "(%s=).*?(})" },
            { "}", "(\"%s\":).*?(})" },
    };
    // 日志脱敏规则配置
    private static LogConfiguration logConfiguration;

    public static LogConfiguration getLogConfiguration() {
        return logConfiguration;
    }

    @Autowired
    public void setLogConfiguration(LogConfiguration logConfiguration) {
        LogTools.logConfiguration = logConfiguration;
    }

    /**
     * 打印参数
     */
    public static void printParems(Method method, @Nullable Class<?> clazz, Object... args) {
        if (!log.isInfoEnabled())
            return;
        String methodName = getSimpleMethodName(method, clazz);
        if (shouldLog(methodName))
            log.info("Params of method '{}' is {}", methodName, getDesensitizedParams(methodName, args));
    }

    /**
     * 打印返回结果
     */
    public static void printResult(Method method, @Nullable Class<?> clazz, Object resultObj) {
        if (!log.isInfoEnabled())
            return;
        String methodName = getSimpleMethodName(method, clazz);
        if (shouldLog(methodName)) {
            String result = resultObj == null ? "" : resultObj.toString();
            result = result.length() > logConfiguration.getResultMaxLength() ? result.substring(0, logConfiguration.getResultMaxLength()) + "..." : result;
            log.info("Result of Method '{}' is {}", methodName, getDesensitizedResult(methodName,result));
        }
    }

    /**
     * 获取脱敏后的参数文本
     */
    private static String getDesensitizedParams(String methodName, Object... args) {
        return args == null ? null : getDesensitizedText(Arrays.toString(args), methodName, DesensitizationMethod.TYPE_REQEUEST);
    }
    
    /**
     * 获取脱敏后的返回结果文本
     */
    private static String getDesensitizedResult(String methodName, String result) {
        return result == null || "".equals(result) ? "" : getDesensitizedText(result, methodName, DesensitizationMethod.TYPE_RESULT);
    }
    
    /**
     * 获取脱敏后的文本
     */
    private static String getDesensitizedText(String text, String methodName, Integer desensitizationType) {
        DesensitizationMethod getDesensitizationMethod = getDesensitizationMethod(methodName);
        if(getDesensitizationMethod == null
                || (!desensitizationType.equals(getDesensitizationMethod.getType()) && !DesensitizationMethod.TYPE_ALL.equals(getDesensitizationMethod.getType())))
            return text;
        List<String> columns = getDesensitizationMethod.getColumns();
        if(columns == null || columns.size()<1)
            return text;
        for (String column : columns) {
            text= getDesensitizedText(text, column);
        }
        return text;
    }
    
    /**
     * 获取脱敏后的文本
     */
    private static String getDesensitizedText(String text, String desensitizationColumn) {
        if(text.indexOf(desensitizationColumn) > -1) {
            String tmpText = "";
            for (String[] pattern : patterns) {
                tmpText = text;
                if(text.indexOf(pattern[0]) > -1)
                    text = text.replaceAll(String.format(pattern[1], desensitizationColumn), "$1***$2");
                if(!tmpText.equals(text))
                    break;
            }
        }
        return text;
    }
    
    /**
     * 获取方法的脱敏配置
     */
    private static DesensitizationMethod getDesensitizationMethod(String methodName) {
        if(logConfiguration == null 
                || logConfiguration.getDesensitization()==null 
                || logConfiguration.getDesensitization().getMethods() == null
                || logConfiguration.getDesensitization().getMethods().size() < 1)
            return null;
        for (DesensitizationMethod desensitizationMethod : logConfiguration.getDesensitization().getMethods()) {
            if(methodName.equals(desensitizationMethod.getMethodName())) {
                return desensitizationMethod;
            }
        }
        return null;
    }

    /**
     * 获取格式为"类名.方法名"的方法信息
     */
    private static String getSimpleMethodName(Method method, @Nullable Class<?> clazz) {
        if (clazz == null || method == null)
            return null;
        return ClassUtils.getShortName(clazz) + "." + method.getName();
    }

    /**
     * 是否需要打印日志
     */
    private static boolean shouldLog(String methodName) {
        return logConfiguration.getSkipMethods().size() == 0 ||
                (logConfiguration.getSkipMethods().size() > 0 && !logConfiguration.getSkipMethods().contains(methodName));
    }
}
