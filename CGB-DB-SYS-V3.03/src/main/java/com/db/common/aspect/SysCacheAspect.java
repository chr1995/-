package com.db.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

@Aspect
@Service
public class SysCacheAspect {
	/**
	 * 定义切入点,其中@annotation(...)为注解方式切入点表达式定义
	 */
	@Pointcut("@annotation(com.db.common.annotation.RequiredCache)")
	public void doCache(){}
	
	@Around("doCache()")
	public Object around(ProceedingJoinPoint jp) throws Throwable{
		//1.先从缓存取数据
		System.out.println("query data from cache");
		//2.缓存没有则执行业务
		System.out.println("execute target method");
		Object result = jp.proceed();
		//3.将查询结果存储到缓存对象
		System.out.println("put data to cache");
		return result;
	}
}
