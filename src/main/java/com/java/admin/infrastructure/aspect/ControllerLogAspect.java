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
 * Controller 请求日志切面
 * 自动记录所有 Controller 方法的请求和响应信息
 */
@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    /**
     * 环绕通知：记录 Controller 方法的请求和响应日志
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

        // 记录请求信息 (DEBUG级别，避免过多日志)
        log.debug("Request started - Method: {}, URI: {}, Controller: {}, Action: {}, Args: {}",
                method, uri, className, methodName, args);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - start;

            // 记录成功响应
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

            // 记录异常
            log.error("Controller exception - Method: {}, URI: {}, Cost: {}ms, Error: {}",
                    method, uri, cost, e.getMessage(), e);

            throw e;
        }
    }
}