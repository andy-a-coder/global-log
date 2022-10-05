package com.andy.log.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class LogApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>{

    /**
     * 对org.springframework.web.method.support.InvocableHandlerMethod.doInvoke做字节码增强
     */
    static {
        try {
            log.debug("start to advice InvocableHandlerMethod...");
            ClassPool cPool = ClassPool.getDefault();
            cPool.importPackage("com.andy.log.utils");
            cPool.insertClassPath(new ClassClassPath(ApplicationContextInitializer.class));
            CtClass cClass = cPool.get("org.springframework.web.method.support.InvocableHandlerMethod");
            CtMethod doInvoke = cClass.getDeclaredMethod("doInvoke");
            doInvoke.insertBefore("LogTools.printParems(getMethod(), getBeanType(), args);");
            doInvoke.insertAfter("LogTools.printResult(getMethod(), getBeanType(), $_);");
            cClass.toClass();
            log.debug("advice InvocableHandlerMethod finished");
        } catch (Exception e) {
            log.warn("对InvocableHandlerMethod.doInvoke做日志增强失败", e);
        }
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // do nothing
    }
}
