--xSimple平台说明
1.关于clientInfo.properties属性文件说明在clinetInfo.txt文件中
2.关于数据库文件的说明:
相应的数据库信息在data中进行初始化,如果不需要修改相应的初始化文件等,在data文件夹下根据相应的数据库信息进行修改
如果不需要进行初始化的操作,在applicationContext.xml中注释掉以下部分：
<jdbc:initialize-database data-source="dataSource" > ;
     <jdbc:script location="classpath:data/h2/cleanup-data.sql" />
     <jdbc:script location="classpath:data/h2/import-data.sql" encoding="UTF-8" />
</jdbc:initialize-database>
3.关于数据库指向的说明:
数据库的指向需要在/WEB-INF/web.xml中进行配置指向,在文件的末尾
<!-- 切换环境 -->
  <context-param>  
    <param-name>spring.profiles.active</param-name>  
    <param-value>test</param-value> 
</context-param>  中进行环境的切换