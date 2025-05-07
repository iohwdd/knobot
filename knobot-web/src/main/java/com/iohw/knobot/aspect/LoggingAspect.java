package com.iohw.knobot.aspect;

import com.iohw.knobot.common.annotation.MdcDot;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author: iohw
 * @date: 2025/5/7 16:16
 * @description:
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(mdcDot)")
    public Object around(ProceedingJoinPoint joinPoint, MdcDot mdcDot) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long endTime = System.currentTimeMillis();
            String className = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();
            log.info("执行方法：{}.{}，耗时：{} ms", className, methodName, (endTime - startTime));
        }
    }
}
