package com.example.prjwebservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.prjwebservice.model.dto.request.GradeRequest;
import com.example.prjwebservice.model.dto.response.SubmissionResponse;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.example.prjwebservice.service.impl.*.*(..))")
    public Object logTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        
        logger.info("[TIME] {} executed in {}ms", joinPoint.getSignature().toShortString(), executionTime);
        return proceed;
    }

    @AfterReturning(
            pointcut = "execution(* com.example.prjwebservice.service.SubmissionService.grade(..)) && args(request)",
            returning = "result"
    )
    public void logGrading(JoinPoint joinPoint, GradeRequest request, SubmissionResponse result) {
        logger.info("[INFO] Lecturer ID: {} graded Submission ID: {} with Score: {}",
                result.getLecturerId(), result.getId(), result.getScore());
    }
}
