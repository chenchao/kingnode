--第二期脚本
--修改壹卡会内容大类
--增加排序字段
alter table kn_eka_categories add seq integer ;
--修改排序和名称
update kn_eka_categories set seq=6,name='壹卡会商家' where id=1;
update kn_eka_categories set seq=1 where id=2;
update kn_eka_categories set seq=4,name='健身' where id=3;
update kn_eka_categories set seq=7,name='更多服务' where id=4;
update kn_eka_small_class set name='健身' where id=10;
--修改壹卡会为生活服务
update kn_resource set name='生活服务' where id=100;
update kn_resource set name='生活服务管理' where id=101;

--增加壹卡会字段
alter table kn_eka add is_agreement varchar(20) ;
alter table kn_eka add star varchar(20) ;
--插入大类
insert into kn_eka_categories(id,name,seq) values(5,'酒店',3);
insert into kn_eka_categories(id,name,seq) values(6,'美食',2);
insert into kn_eka_categories(id,name,seq) values(7,'保健预约',5);

--插入小类
insert into kn_eka_small_class(id,name,categories_Id) values(23,'酒店',5);
insert into kn_eka_small_class(id,name,categories_Id) values(24,'餐厅',6);
insert into kn_eka_small_class(id,name,categories_Id) values(25,'外卖',6);
insert into kn_eka_small_class(id,name,categories_Id) values(26,'保健',7);

--修改论坛表  增加类型和截止时间字段
alter table kn_social_blog add type int ;
alter table kn_social_blog add end_Time bigint ;

--新增保健预约菜单
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (130,'ENABLE','health',1,'保健预约','130.',14,0,'MENU','/health');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (131,'ENABLE','health-appointment',2,'排号及诊断','130.131.',1,130,'MENU','/health/appointment');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (132,'ENABLE','health-history',2,'历史诊断','130.132.',2,130,'MENU','/health/history');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (133,'ENABLE','health-settings',2,'设置','130.133.',3,130,'MENU','/health/settings');

-- --基本权限数据新增
-- insert into kn_role_resource (role_id,res_id) values (2,130);
-- insert into kn_role_resource (role_id,res_id) values (2,131);
-- insert into kn_role_resource (role_id,res_id) values (2,132);
-- insert into kn_role_resource (role_id,res_id) values (2,133);



--修改生活服务的排序问题
--修改赞和踩数
update kn_eka set praise_Nums=0 where praise_Nums<0;
update kn_eka set tread_Nums=0 where tread_Nums<0;

--修改距离字段类型
update kn_eka set distance='0' where distance is null or distance = '';
update kn_eka set star='0' where star is null or star = '';
commit;
alter table kn_eka modify distance bigint;
alter table kn_eka modify star bigint;