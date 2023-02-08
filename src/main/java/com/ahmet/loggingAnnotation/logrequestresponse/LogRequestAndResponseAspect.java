package com.ahmet.loggingAnnotation.logrequestresponse;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
@Slf4j
public class LogRequestAndResponseAspect {
    @Around("@annotation(LogRequestAndResponse)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if(getRequest(joinPoint)) {
            logBefore(joinPoint, ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
        }
        return joinPoint.proceed();
    }

    public void logBefore(JoinPoint joinPoint, HttpServletRequest request) {
        log.info("Path: " + request.getServletPath() + " ; Method: " + joinPoint.getSignature().getName() + " ; Arguments:  " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "@annotation(LogRequestAndResponse)", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        if(getResponse(joinPoint)) {
            log.info("Method( " + joinPoint.getSignature().getName() + " ) Return value : " + result.toString());
        }
    }

    @AfterThrowing(pointcut = "@annotation(LogRequestAndResponse)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
        log.error("Cause : " + exception.getCause());
    }

    private boolean getRequest(JoinPoint joinPoint){
        return getAnnotation(joinPoint).request();
    }

    private boolean getResponse(JoinPoint joinPoint){
        return getAnnotation(joinPoint).response();
    }

    private LogRequestAndResponse getAnnotation(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getAnnotation(LogRequestAndResponse.class);
    }
}
