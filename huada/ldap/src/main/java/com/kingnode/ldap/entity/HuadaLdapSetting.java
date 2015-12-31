package com.kingnode.ldap.entity;
/**
 * 华大的ldap设置
 * @author 448778074@qq.com (cici)
 */
public class HuadaLdapSetting{
    /**
     *   String URL = "ldap://szdc.genomics.cn:389/";
     String BASEDN = "OU=COLTDSH,OU=BGICOLTD,DC=genomics,DC=cn";
     String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
     */
    public static final String URL="ldap://szdc.genomics.cn:389/";
    public static final String FACTORY="com.sun.jndi.ldap.LdapCtxFactory";
    public static final String BASEDN = "OU=COLTDSH,OU=BGICOLTD,DC=genomics,DC=cn";
    //    public static final String BASEDN = "DC=kingnode,DC=com";
    public static final String USERNAME="CN=P_PR,OU=Tech Public,OU=Techs,DC=genomics,DC=cn";
    public static final String PASSWORD="bgitech2014PR";
    public static final String UserSystem = "BGITECH";

}
