package com.burntoburn.easyshift.log;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimedAspect {

    @Around("execution(* com.burntoburn.easyshift..controller..*(..)) || execution(* com.burntoburn.easyshift..service..*(..)) || execution(* com.burntoburn.easyshift..repository..*(..))")
    public Object timeExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Sample sample = Timer.start();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(
                    Timer.builder("custom.api.timer")
                            .description("Timer for API execution")
                            .tag("class", className)
                            .tag("method", methodName)
                            .register(Metrics.globalRegistry)
            );
        }
    }
}
