package com.recsoft.aspect;

import com.recsoft.data.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class ProveRolesAspect {

    @Pointcut(value = "@annotation(proveRole) && args(user,..)")
    public void callAt(ProveRole proveRole, User user) {
    }

    @Around(value = "callAt(proveRole, user)", argNames = "joinPoint,proveRole,user")
    public Object poincutActionSeller(
            ProceedingJoinPoint joinPoint,
            ProveRole proveRole,
            User user) throws Throwable {

        List<Object> list = Arrays.asList(joinPoint.getArgs());

        if (!proveRole.nameRole().equals(user.getRole().getName())){
            throw new Throwable("User with roles access not allow.");
        }

        return joinPoint.proceed();
    }

}
