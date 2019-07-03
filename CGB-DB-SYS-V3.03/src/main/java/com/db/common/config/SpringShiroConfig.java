package com.db.common.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.db.sys.service.realm.ShiroUserRealm;
/**
 * @Configuration 描述的bean一般为一个配置类
 * @author Tarena
 *
 */
@Configuration
public class SpringShiroConfig {
	/**
	 * @Bean一般用户描述方法,然后将方法的返回值
	 * 交给Spring管理,其中@Bean注解中的内容为Bean
	 * 对象的key。
	 * @return
	 */
	
	@Bean("securityManager")
	public SecurityManager newSecurityManager(
			ShiroUserRealm realm,
			CacheManager cacheManager,
			RememberMeManager rememberMeManager){
		DefaultWebSecurityManager sm = 
				new DefaultWebSecurityManager();
		sm.setRealm(realm);
		sm.setCacheManager(cacheManager);
		sm.setRememberMeManager(rememberMeManager);
		sm.setSessionManager(newSessionManager());
		return sm;//不是java.lang包中的SecurityManager
	}
	
	@Bean("shiroFilterFactory")
	public ShiroFilterFactoryBean newShiroFilterFactoryBean(
			SecurityManager securityManager){
		ShiroFilterFactoryBean fBean = new ShiroFilterFactoryBean();
		fBean.setSecurityManager(securityManager);
		fBean.setLoginUrl("/doLoginUI.do");
		Map<String,String> filterMap=new LinkedHashMap<String,String>();
		filterMap.put("/bower_components/**","anon");
		filterMap.put("/build/**","anon");
		filterMap.put("/dist/**","anon");
		filterMap.put("/plugins/**","anon");
		filterMap.put("/user/doLogin.do", "anon");
		filterMap.put("/doLogout.do", "logout");
		filterMap.put("/**","authc");
		fBean.setFilterChainDefinitionMap(filterMap);
		return fBean;
	}
	//=========================
	@Bean("lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor newLifecycleBeanPostProcessor(){
		return new LifecycleBeanPostProcessor();
	}
	
	@DependsOn("lifecycleBeanPostProcessor")
	@Bean
	public DefaultAdvisorAutoProxyCreator DefaultAdvisorAutoProxyCreator(){
		return new DefaultAdvisorAutoProxyCreator();
	}
	@Bean
	public AuthorizationAttributeSourceAdvisor newAuthorizationAttributeSourceAdvisor(SecurityManager securityManager){
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}
	
	@Bean
	public MemoryConstrainedCacheManager newMemoryConstrainedCacheManager(){
		return new MemoryConstrainedCacheManager();
	}
	
	@Bean
	public CookieRememberMeManager newCookieManager(){
		 CookieRememberMeManager cookieManager = new CookieRememberMeManager();
		 SimpleCookie cookie = new SimpleCookie("rememberMe");
		 cookie.setMaxAge(24*7*60*60);
		 cookieManager.setCookie(cookie);
		 return cookieManager;
	}
	
	public DefaultWebSessionManager newSessionManager(){
		DefaultWebSessionManager sManager = new DefaultWebSessionManager();
		sManager.setGlobalSessionTimeout(21600000);
		sManager.setDeleteInvalidSessions(true);
		sManager.setSessionValidationSchedulerEnabled(true);
		return sManager;
	}
}
