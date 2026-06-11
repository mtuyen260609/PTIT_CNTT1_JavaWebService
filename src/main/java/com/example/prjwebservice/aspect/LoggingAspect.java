package com.example.prjwebservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.prjwebservice.model.dto.request.GradeRequest;
import com.example.prjwebservice.model.dto.response.SubmissionResponse;

@Aspect
@Component
public class LoggingAspect {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterReturning(
            pointcut = "execution(* com.example.prjwebservice.service.SubmissionService.grade(..)) && args(request)",
            returning = "result"
    )
    public void logGrading(JoinPoint joinPoint, GradeRequest request, SubmissionResponse result) {
        logger.info("[INFO] Lecturer ID: {} graded Submission ID: {} with Score: {}",
                result.getLecturerId(), result.getId(), result.getScore());
    }
}
