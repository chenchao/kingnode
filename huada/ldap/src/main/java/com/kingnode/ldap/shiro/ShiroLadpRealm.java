package com.kingnode.ldap.shiro;
import java.util.Hashtable;
import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.kingnode.diva.security.utils.Digests;
import com.kingnode.diva.utils.Encodes;
import com.kingnode.ldap.entity.HuadaLdapSetting;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
public class ShiroLadpRealm extends AuthorizingRealm{
    private static Logger logger=LoggerFactory.getLogger(ShiroLadpRealm.class);
    protected ResourceService resourceService;
    private LdapTemplate ldapTemplate;
    /**
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException{
        UsernamePasswordToken upt=(UsernamePasswordToken)token;
        String userName=upt.getUsername();
        KnUser user=resourceService.FindUserByLoginName(userName);
        logger.info("用户名为【"+userName+"】的用户开始域登录");
        if("admin".equals(userName)){
            if(ActiveType.DISABLE.equals(user.getStatus())){
                throw new DisabledAccountException();
            }
            byte[] salt=Encodes.decodeHex(user.getSalt());
            return new SimpleAuthenticationInfo(new ShiroUser(user.getId(),userName,user.getName()),user.getPassword(),ByteSource.Util.bytes(salt),getName());
        }else{
            String pw=new String(upt.getPassword());
            if(login(userName,pw)){
                if(user!=null){
                    if(ActiveType.DISABLE.equals(user.getStatus())){
                        throw new DisabledAccountException();
                    }
                    byte[] salt=Encodes.decodeHex(user.getSalt());
                    byte[] hashPassword=Digests.sha1(pw.getBytes(),salt,1024);
                    return new SimpleAuthenticationInfo(new ShiroUser(user.getId(),userName,user.getName()),Encodes.encodeHex(hashPassword),ByteSource.Util.bytes(salt),getName());
                }else{
                    logger.info("没有找到用户名为【"+userName+"】的用户");
                    throw new UnknownAccountException();
                }
            }else{
                logger.info("用户名为【"+userName+"】的用户ldap域登录失败");
                throw new UnknownAccountException();
            }
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
    private Boolean login(String userName,String password){
        logger.info("userName:{},password:{}",userName,password);
        AndFilter filter=new AndFilter();
        filter.and(new EqualsFilter("objectclass","person")).and(new EqualsFilter("sAMAccountName",userName));
        try{
            String userTemp = userName+"@genomics.cn";
            logger.info("修改后的userName:{}",userTemp);
            logger.info("filter:{}",filter.toString());
            //Boolean t=ldapTemplate.authenticate(LdapUtils.emptyLdapName(),filter.toString(),password);
            logger.info("第一次user:{}",userTemp);
            Boolean t = ldapLogin(userTemp,password);
            logger.info("第一次ladp:{}",t);
            if(t==false){
                t=ldapSencondLogin(userName,password);
            }
            return t;
        }catch(Exception e){
            return ldapSencondLogin(userName,password);
        }
    }

    private Boolean ldapSencondLogin(String userName,String password){
        try{
            String userTemp = userName+"@bgitechsolutions.com";
            logger.info("第二次user:{}",userTemp);
            Boolean t = ldapLogin(userTemp,password);
            logger.info("第二次ladp:{}",t);
            return t;
        }catch(Exception e){
            logger.info("第二次：ldap:{}",e);
            return false;
        }
    }
    public boolean ldapLogin(String name ,String password){
        LdapContext ctx = null;
        Hashtable env = null;
        Control[] connCtls = null;

//        String URL = "ldap://szdc.genomics.cn:389/";
        String URL =HuadaLdapSetting.URL;//"ldap://szdc.genomics.cn:389/";
        String BASEDN = HuadaLdapSetting.BASEDN;//"OU=COLTDSH,OU=BGICOLTD,DC=genomics,DC=cn";
        String FACTORY = HuadaLdapSetting.FACTORY;//"com.sun.jndi.ldap.LdapCtxFactory";

        env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,FACTORY);
        env.put(Context.PROVIDER_URL, URL);//LDAP server
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        //此处若不指定用户名和密码,则自动转换为匿名登录
        env.put(Context.SECURITY_PRINCIPAL, name);
        env.put(Context.SECURITY_CREDENTIALS, password);
        try{
            ctx = new InitialLdapContext(env,connCtls);
        }catch(javax.naming.AuthenticationException e){
            logger.info("认证失败：{}",e);
            return false;
        }catch(Exception e){
            logger.info("认证出错：{}",e);
            return false;
        }finally{

            try {
                ctx.close();
            } catch ( Exception Ignore ) {
                logger.info("关闭出错：{}",Ignore);
            }
        }
        return true;
    }
    public void setResourceService(ResourceService resourceService){
        this.resourceService=resourceService;
    }
    public void setLdapTemplate(LdapTemplate ldapTemplate){
        this.ldapTemplate=ldapTemplate;
    }
}