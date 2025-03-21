package com.burntoburn.easyshift.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.burntoburn.easyshift.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        log.info("▶️ [START] {}", methodName);
        Object proceed = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        log.info("✅ [END] {} - {} ms", methodName, elapsedTime);

        return proceed;
    }
}
