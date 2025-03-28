package com.burntoburn.easyshift.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final ThreadLocal<Integer> indentLevel = ThreadLocal.withInitial(() -> 0);

    @Pointcut("execution(* com.burntoburn.easyshift.service..*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.burntoburn.easyshift.repository..*(..))")
    public void repositoryMethods() {}

    @Around("serviceMethods() || repositoryMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        int level = indentLevel.get();
        String indent = "   ".repeat(level);
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethod = String.format("%-60s", className + "." + methodName + "()");

        String layer = className.contains(".repository") ? "🗄️ Repository" : "🧩 Service";

        log.info("{}▶️ {} | START | {}", indent, layer, fullMethod);

        indentLevel.set(level + 1); // 깊이 증가

        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedTime = System.currentTimeMillis() - start;
            indentLevel.set(level); // 호출 이후에 깊이 복원
            log.info("{}✅ {} | END   | {} | {} ms", indent, layer, fullMethod, elapsedTime);
        }
    }
}
