package com.kingnode.xsimple.shiro;
import javax.annotation.PostConstruct;

import com.kingnode.diva.utils.Encodes;
import com.kingnode.xsimple.ShiroUser;
import com.kingnode.xsimple.entity.IdEntity.ActiveType;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.service.system.ResourceService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
public class ShiroDbRealm extends AuthorizingRealm{
    protected ResourceService resourceService;
    /**
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException{
        UsernamePasswordToken upt=(UsernamePasswordToken)token;
        String userName=upt.getUsername();
        KnUser user=resourceService.FindUserByLoginName(userName);
        if(user!=null){
            if(ActiveType.DISABLE.equals(user.getStatus())){
                throw new DisabledAccountException();
            }
            byte[] salt=Encodes.decodeHex(user.getSalt());
            return new SimpleAuthenticationInfo(new ShiroUser(user.getId(),userName,user.getName()),user.getPassword(),ByteSource.Util.bytes(salt),getName());
        }else{
            throw new UnknownAccountException();
        }
    }
    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals){
        ShiroUser shiroUser=(ShiroUser)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        for(KnRole role : resourceService.CacheRoles(shiroUser.id)){
            // 基于Role的权限信息
            info.addRole(role.getCode());
            // 基于Permission的权限信息
            info.addStringPermissions(role.getPermissionList());
        }
        return info;
    }
    /**
     * 设定Password校验的Hash算法与迭代次数.
     */
    @PostConstruct
    public void initCredentialsMatcher(){
        HashedCredentialsMatcher matcher=new HashedCredentialsMatcher(ResourceService.HASH_ALGORITHM);
        matcher.setHashIterations(ResourceService.HASH_INTERATIONS);
        setCredentialsMatcher(matcher);
    }
    public void setResourceService(ResourceService resourceService){
        this.resourceService=resourceService;
    }
}