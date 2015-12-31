package com.kingnode.ldap.service;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.google.common.collect.Lists;
import com.kingnode.diva.utils.Threads;
import com.kingnode.xsimple.api.system.LdapUser;
import com.kingnode.xsimple.service.system.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true) public class LdapService implements Runnable{
    protected Logger logger=LoggerFactory.getLogger(getClass());
    @Autowired private LdapTemplate ldapTemplate;
    @Autowired private OrganizationService os;
    private static final String defaultEmail="default@genomics.cn";
    private List<LdapUser> syncLdapUser(){
        long t1=System.currentTimeMillis();
        final List<LdapUser> ldapUser=Lists.newArrayList();
        ldapTemplate.search(LdapUtils.emptyLdapName(),"(objectclass=organizationalUnit)",new AttributesMapper(){
            public Object mapFromAttributes(Attributes attrs1) throws NamingException{
                EqualsFilter filter=new EqualsFilter("objectClass","top");
                try{
                    final String OU=attrs1.get("ou").get().toString();
                    logger.info("------------------------------ou:{}",OU);
                    List<LdapUser> lu=ldapTemplate.search("ou="+attrs1.get("ou").get().toString(),filter.toString(),new AttributesMapper<LdapUser>(){
                        @Override public LdapUser mapFromAttributes(Attributes attrs) throws NamingException{
                            Attribute department=attrs.get("department");
                            if(attrs.get("sAMAccountName")!=null){
                                LdapUser lu=new LdapUser();
                                String sAMAccountName=attrs.get("sAMAccountName")!=null?attrs.get("sAMAccountName").get().toString():"";
                                lu.setLoginName(sAMAccountName);
                                lu.setUserName(attrs.get("cn").get().toString());
                                String mail=defaultEmail;
                                if(attrs.get("mail")!=null){
                                    mail=attrs.get("mail").get().toString();
                                }
                                lu.setEmail(mail);
                                if(department!=null){
                                    lu.setDepartment(department.get().toString());
                                }
                                Attribute userId=attrs.get("description");
                                if(userId!=null){
                                    lu.setUserId(userId.get().toString());
                                }
                                Attribute telephone=attrs.get("telephoneNumber");
                                if(telephone!=null){
                                    lu.setTelephoneNumber(telephone.get().toString());
                                }
                                Attribute phone=attrs.get("mobile");
                                if(phone!=null){
                                    lu.setMobile(phone.get().toString());
                                }
                                logger.info("登录者姓名:{}",lu.getUserName());
                                return lu;
                            }else{
                                return null;
                            }
                        }
                    });
                    ldapUser.addAll(lu);
                }catch(Exception e){
                    logger.info("e:{}",e);
                    return null;
                }
                return null;
            }
        });
        long t2=System.currentTimeMillis();
        logger.info("//============================ 查询Ldap人员耗费了："+(t2-t1)/1000+"秒========================//");
        return ldapUser;
    }
    @Transactional(readOnly=false)
    public void syncEmployee(){
        logger.info("//=========================-------开始同步用户-----==========================//");
        List<LdapUser> lus=syncLdapUser();
        logger.info("lus:{}",lus.size());
        long t1=System.currentTimeMillis();
        try{
            os.syncLdapUser(lus);
        }catch(Exception e){
            logger.error("同步用户报错："+e.getMessage());
            logger.error("同步用户报错{}",e);
        }
        long t2=System.currentTimeMillis();
        logger.info("插入Ldap人员耗费了："+(t2-t1)/1000+"秒");
        logger.info("//==========================-------结束同步用户-----==========================//");
    }
    @Transactional(readOnly=false)
    public void test(){
        List<LdapUser> lus=Lists.newArrayList();
        LdapUser u1=new LdapUser();
        u1.setDepartment("中国");
        u1.setEmail("cc@qq.com");
        u1.setLoginName("cc");
        u1.setUserId("Beg6666");
        u1.setUserName("chenchao");
        LdapUser u2=new LdapUser();
        u2.setDepartment("美国首都");
        u2.setEmail("cc33@qq.com");
        u2.setLoginName("cc333");
        u2.setUserId("Beg6666333");
        u2.setUserName("chenchao333");
        lus.add(u1);
        lus.add(u2);
        os.syncLdapUser(lus);
    }
    private String cronExpression="0 0 6 * * ?";
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    //=================定时任务=============//
    @PostConstruct public void start(){
        threadPoolTaskScheduler=new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix("LdapCronJob");
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.schedule(this,new CronTrigger(cronExpression));
    }
    @PreDestroy public void stop(){
        ScheduledExecutorService scheduledExecutorService=threadPoolTaskScheduler.getScheduledExecutor();
        Threads.normalShutdown(scheduledExecutorService,20,TimeUnit.SECONDS);
    }
    @Override public void run(){
        syncEmployee();
    }
    public void setCronExpression(String cronExpression){
        this.cronExpression=cronExpression;
    }
}
