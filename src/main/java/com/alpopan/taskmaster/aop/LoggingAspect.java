// src/main/java/com/alpopan/taskmaster/aop/LoggingAspect.java
package com.alpopan.taskmaster.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        Class<?> targetClass = pjp.getTarget().getClass();
        Logger log = LoggerFactory.getLogger(targetClass);

        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String method = sig.getName();
        Object[] args = pjp.getArgs();

        log.info("Entering {} with args {}", method, args);
        long start = System.currentTimeMillis();

        Object result = pjp.proceed();

        long took = System.currentTimeMillis() - start;
        log.info("Exiting {}; result = {}; took {} ms", method, result, took);

        return result;
    }
}
