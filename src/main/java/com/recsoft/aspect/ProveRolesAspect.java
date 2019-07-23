package com.recsoft.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class ProveRolesAspect {

    @Around("@annotation(com.recsoft.aspect.ProveRoleSeller)")
    public Object poincutActionSeller(
            ProceedingJoinPoint joinPoint) throws Throwable {
        List<Object> list = Arrays.asList(joinPoint.getArgs());

        return joinPoint.proceed();
    }

}
