package com.db.sys.service.realm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.db.sys.dao.SysMenuDao;
import com.db.sys.dao.SysRoleMenuDao;
import com.db.sys.dao.SysUserDao;
import com.db.sys.dao.SysUserRoleDao;
import com.db.sys.entity.SysUser;

@Service
public class ShiroUserRealm extends AuthorizingRealm{
	@Autowired
	private SysUserDao sysUserDao;
	
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	
	@Autowired
	private SysRoleMenuDao sysRoleMenuDao;
	
	@Autowired
	private SysMenuDao sysMenuDao;
	
	/**
	 * 设置凭证匹配器(与用户添加操作使用相同的加密算法)
	 */
	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		//1.构建CredentialsMatcher对象
		HashedCredentialsMatcher hcm=new HashedCredentialsMatcher();
		//2.设置加密算法
		hcm.setHashAlgorithmName("MD5");
		//3.设置加密次数
		hcm.setHashIterations(1);
		super.setCredentialsMatcher(hcm);
	}
	
	/**
	 * 通过此方法完成认证数据的获取及封装,系统
	 * 底层会将认证数据传递认证管理器，由认证
	 * 管理器完成认证操作。
	 */
	//认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		//1.从token中获取用户信息(前端用户输入)
		UsernamePasswordToken upToken=(UsernamePasswordToken) token;
		String username = upToken.getUsername();
		//2.基于用户名从数据库中查询用户信息
		SysUser user = sysUserDao.findUserByUserName(username);
		//3.判定用户是否存在
		if(user==null)
			throw new UnknownAccountException();
		//4.判定用户是否已被禁用
		if(user.getValid()==0)
			throw new LockedAccountException();
		//5.封装用户信息并返回
		ByteSource credentialsSalt=
				ByteSource.Util.bytes(user.getSalt());
		//记住：构建什么对象要看方法的返回值
		SimpleAuthenticationInfo info=
				new SimpleAuthenticationInfo(
					user,//principal (身份)
					user.getPassword(),//hashedCredentials(已加密的密码)
					credentialsSalt, //credentialsSalt
					getName());//realName
		//6.返回封装结果
		return info;//返回值会传递给认证管理器(后续
		//认证管理器会通过此信息完成认证操作)
	}
	
	//授权
	//…
	/**通过此方法完成授权信息的获取及封装*/
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		//测试shiro缓存
		System.out.println("==doGetAuthorizationInfo==");
		//1.获取登录用户id
		SysUser user = (SysUser) principals.getPrimaryPrincipal();
		//2.基于用户id获取用户对应的角色id并判定
		Integer userId = user.getId();
		List<Integer> roleIds=
				sysUserRoleDao.findRoleIdsByUserId(userId);
		if(roleIds==null||roleIds.size()==0)
			throw new AuthorizationException();
		//3.基于角色id获取对应的菜单id并判定
		Integer[] array={};
		List<Integer> menuIds=
		sysRoleMenuDao.findMenuIdsByRoleIds(
				roleIds.toArray(array));
	    if(menuIds==null||menuIds.size()==0)
	    throw new AuthorizationException();
		//4.基于菜单id获取权限标识
	    List<String> permissions=
	    	    sysMenuDao.findPermissions(
	    	    	menuIds.toArray(array));
		//5.封装数据并返回
	    Set<String> set=new HashSet<>();
	    for(String per:permissions){
	    	if(!StringUtils.isEmpty(per)){
	    		set.add(per);
	    	}
	    }
	    SimpleAuthorizationInfo info=
	    		new SimpleAuthorizationInfo();
	    info.setStringPermissions(set);
		return info;//返回给授权管理器
	}
}
