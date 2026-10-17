package com.example.myapp.services;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.myapp.services.*.*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("--- מתחילה לבצע את הפונקציה: " + methodName + " ---");
        try {
            Object result = joinPoint.proceed();
            System.out.println("--- הפונקציה " + methodName + " הסתיימה בהצלחה ---");
            return result;
        } catch (Throwable throwable) {
            System.out.println("--- הפונקציה " + methodName + " נכשלה עם שגיאה ---");
            throw throwable;
        }
    }
}
