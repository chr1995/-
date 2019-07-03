package com.db.common.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

@Aspect
@Service
public class OtherAspect {
	@Pointcut("bean(sysUserServiceImpl)")
	public void doAspect(){}
	
	@Before("doAspect()")
	public void before(){
		System.out.println("Before");
	}
	
	@After("doAspect()")
	public void after(){
		System.out.println("After");
	}
	
	@AfterReturning("doAspect()")
	public void afterReturning(){
		System.out.println("@AfterReturning");
	}
	
	@AfterThrowing("doAspect()")
	public void afterThrowing(JoinPoint jp){
		System.out.println("@AfterThrowing");
	}
	
	@Around("doAspect()")
	public Object around(ProceedingJoinPoint jp) throws Throwable{
		System.out.println("@Around before");
		Object result = jp.proceed();
		System.out.println("@Around after");
		return result;
	}
}
