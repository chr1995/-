package com.db.common.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.db.common.annotation.RequiredLog;
import com.db.common.util.IPUtils;
import com.db.common.util.ShiroUtil;
import com.db.sys.dao.SysLogDao;
import com.db.sys.entity.SysLog;

/**
 * 通过此类为系统中的某些业务操作添加扩展功能
 * 日志扩展功能
 * @author Tarena
 * @Aspect 描述的类为一个切面对象,这样的类中通常会有两大部分构成
 * 1)Pointcut:切入点(织入扩展功能的点)
 * 2)Advice:通知(扩展功能)
 */
@Order(1)
@Aspect
@Service
public class SysLogAspect {
	@Autowired
	private SysLogDao sysLogDao;
	/**
	 * 定义切入点,切入点的定义要借助@Pointcut
	 * 其中bean(bean名字或表达式)为切入点表达式
	 * 语法结构:
	 * 1)bean(bean的名字) 例如bean(sysUserServiceImpl)
	 * 2)bean(bean的表达式)例如bean(*ServiceImpl)
	 */
	//@Pointcut("bean(sysUserServiceImpl)")
	@Pointcut("@annotation(com.db.common.annotation.RequiredLog)")
	public void doLog(){}
	/**
	 * @Around 此注解描述的方法为一个通知,这个通知中可以在目标方法执行之前和之后
	 * 做一些事情
	 * @jp 	       连接点对象(封装了目标方法信息)
	 * @param
	 * @return 目标方法的执行结果
	 */
	//切面方法
	@Around("doLog()")
	public Object around(ProceedingJoinPoint jp) throws Throwable{
		long t1=System.currentTimeMillis();
		
		//processd()执行下一个切面方法,
		//没有下一个切面,则执行目标方法
		//由于使用@Around,环绕通知,随意可以在两个方法之间执行
		//目标方法,
		Object result = jp.proceed();
		
		long t2=System.currentTimeMillis();
		//如何输出这个是哪个类的哪个方法,执行了多少时间
		Method targetMethod=getTargetMethod(jp);
	    String methodName=getTargetMethodName(targetMethod);
		System.out.println(methodName+"execution time:"+(t2-t1));
		saveObject(jp,(t2-t1));
		return result;
	}
	/**将用户行为日志信息写入到数据库
	 * @throws SecurityException 
	 * @throws NoSuchMethodException */
	private void saveObject(ProceedingJoinPoint jp,long time) throws NoSuchMethodException, SecurityException{
		//1.获取用户行为日志信息
		String username = ShiroUtil.getUser().getUsername();
		Method targetMethod = getTargetMethod(jp);
		String method=getTargetMethodName(targetMethod);
		String params=Arrays.toString(jp.getArgs());
		RequiredLog rlog = targetMethod.getDeclaredAnnotation(RequiredLog.class);
		String operation="operation";
		if(rlog!=null&&!StringUtils.isEmpty(rlog.value())){
			operation=rlog.value();
		}
		String ip=IPUtils.getIpAddr();
		//2.封装用户行为日志信息
		SysLog log=new SysLog();
		log.setUsername(username);
		log.setMethod(method);
		log.setParams(params);
		log.setOperation(operation);
		log.setIp(ip);
		log.setTime(time);
		log.setCreatedTime(new Date());
		//3.将日志信息持久化
		 sysLogDao.insertObject(log);
	}
	
	
	/**获取目标方法的名称:类全名+方法名*/
	   private String getTargetMethodName(Method method) {
		   return new StringBuilder(method.getDeclaringClass().getName())
		   .append(".").append(method.getName()).toString();
	   }
	
	/**基于连接点信息获取目标方法对象
	 * @throws SecurityException 
	 * @throws NoSuchMethodException */
	private Method getTargetMethod(ProceedingJoinPoint jp) throws NoSuchMethodException, SecurityException{
		Class<? extends Object> targetCls = 
				jp.getTarget().getClass();
		//获取方法签名信息(包含方法名,参数列表等信息)
		 MethodSignature ms=(MethodSignature) jp.getSignature();
		   //3.获取目标方法对象
		   Method method=
		   targetCls.getDeclaredMethod(ms.getName(),ms.getParameterTypes());
		   return method;
	}
}
