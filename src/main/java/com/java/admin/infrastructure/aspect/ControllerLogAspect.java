package com.java.admin.infrastructure.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Controller request logging aspect
 * Automatically logs request and response information for all Controller methods
 */
@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    /**
     * Around advice: Logs request and response for Controller methods
     */
    @Around("execution(* com.java.admin.modules.system.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Log request information (DEBUG level to avoid excessive logs)
        log.debug("Request started - Method: {}, URI: {}, Controller: {}, Action: {}, Args: {}",
                method, uri, className, methodName, args);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - start;

            // Log successful response
            if (cost > 1000) {
                log.warn("Slow response - Method: {}, URI: {}, Cost: {}ms, Controller: {}, Action: {}",
                        method, uri, cost, className, methodName);
            } else {
                log.debug("Response completed - Method: {}, URI: {}, Cost: {}ms",
                        method, uri, cost);
            }

            return result;
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;

            // Log exception
            log.error("Controller exception - Method: {}, URI: {}, Cost: {}ms, Error: {}",
                    method, uri, cost, e.getMessage(), e);

            throw e;
        }
    }
}