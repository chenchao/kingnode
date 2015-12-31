#!/bin/sh

echo "MVN PACKAGE"
mvn clean && mvn package

echo "Update App"

#开发环境
#用户：dev 密码：knd@2014!QAZ
scp -P 9302 -r web/target/kingnode-xsimple-web-1.0.7/WEB-INF/lib/kingnode-xsimple-ecm*workflow* dev@192.168.8.10:/opt/knd_hdv2/apache-tomcat-7.0.56/webapps/ROOT/WEB-INF/lib/
#测试环境
#scp -P 9302 -r web/target/kingnode-xsimple-web-1.0.7/WEB-INF/lib/kingnode-xsimple-ecm* dev@test.kingnode.com:/opt/knd_hdv2/apache-tomcat-7.0.56/webapps/ROOT/WEB-INF/lib/
#账号： dev     密码：knd@2014!QAZ




