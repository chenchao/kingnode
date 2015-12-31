---插入资源菜单
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (1,'ENABLE','main',1,'驾驶舱','1.',1,0,'MENU','/main');

--系统管理
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (2,'ENABLE','system',1,'系统设置','2.',2,0,'MENU','/system');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (3,'ENABLE','system-resource',2,'资源管理','2.3.',1,2,'MENU','/system/resource');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (4,'ENABLE','system-role',2,'角色管理','2.4.',2,2,'MENU','/system/role');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (5,'ENABLE','system-user',2,'用户管理','2.5.',3,2,'MENU','/system/user');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (6,'ENABLE','system-employee',2,'员工管理','2.6.',4,2,'MENU','/system/employee');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (7,'ENABLE','system-organization',2,'组织管理','2.7.',5,2,'MENU','/system/organization');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (8,'ENABLE','system-position',2,'岗位管理','2.8.',6,2,'MENU','/system/position');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (9,'ENABLE','system-team',2,'团队管理','2.9.',7,2,'MENU','/system/team');

--应用管理
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (20,'ENABLE','application',1,'应用管理','20.',3,0,'MENU','/application');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (21,'ENABLE','application-list',2,'应用清单','20.21.',1,20,'MENU','/application/list');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (22,'ENABLE','application-version',2,'应用版本','20.22.',2,20,'MENU','/application/version');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (23,'ENABLE','application-setup',2,'应用设置','20.23.',3,20,'MENU','/application/setup');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (24,'ENABLE','application-role',2,'角色管理','20.24.',4,20,'MENU','/application/role');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (25,'ENABLE','application-module',2,'模块管理','20.25.',5,20,'MENU','/application/module');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (26,'ENABLE','application-function',2,'功能管理','20.26.',6,20,'MENU','/application/function');

--安全管理
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (30,'ENABLE','safety',1,'安全管理','30.',4,0,'MENU','/safety');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (31,'ENABLE','safety-channel',2,'设备管理','30.31',1,30,'MENU','/safety/channel');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (32,'ENABLE','safety-certificate',2,'证书管理','30.32.',3,30,'MENU','/safety/certificate');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (33,'ENABLE','safety-message-send',2,'消息发送','30.33.',4,30,'MENU','/safety/message-send');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (34,'ENABLE','safety-message-search',2,'消息查询','30.34.',5,30,'MENU','/safety/message-search');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (35,'ENABLE','safety-status',2,'设备状态','30.35.',6,30,'MENU','/safety/status');

--平台管理
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (40,'ENABLE','platform',1,'平台管理','40.',5,0,'MENU','/platform');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (41,'ENABLE','platform-service',2,'客服管理','40.41.',1,40,'MENU','/platform/service');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (42,'ENABLE','platform-opinion-feedback',2,'意见反馈','40.42.',2,40,'MENU','/platform/opinion-feedback');

--新闻
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (50,'ENABLE','news',1,'资讯管理','50.',6,0,'MENU','/news');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (51,'ENABLE','news-category',2,'新闻类别','50.51.',1,50,'MENU','/news/category');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (52,'ENABLE','news-article',2,'新闻管理','50.52.',3,50,'MENU','/news/article');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (53,'ENABLE','news-comment',2,'评论管理','50.53.',4,50,'MENU','/news/comment');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (54,'ENABLE','news-notice',2,'公告列表','50.54.',5,50,'MENU','/news/notice');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (55,'ENABLE','news-welfare',2,'工会福利','50.54.',5,50,'MENU','/news/welfare');

--图书借阅
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (60,'ENABLE','library',1,'图书借阅','60.',7,0,'MENU','/library');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (61,'ENABLE','library-list',2,'书籍管理','60.61.',1,60,'MENU','/library/list');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (62,'ENABLE','library-borrow-manage',2,'借书管理','60.62.',2,60,'MENU','/library/borrow-manage');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (63,'ENABLE','library-return-borrow',2,'还书管理','60.63.',3,60,'MENU','/library/return-borrow');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (64,'ENABLE','library-borrow-recode',2,'借阅记录','60.64.',4,60,'MENU','/library/borrow-recode');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (65,'ENABLE','library-borrow-rule',2,'借阅规则','60.65.',5,60,'MENU','/library/borrow-rule');

--公务车管理
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (70,'ENABLE','auto',1,'公务车管理','70.',8,0,'MENU','/auto');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (71,'ENABLE','auto-approval',2,'车辆审批','70.71.',1,70,'MENU','/auto/approval');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (72,'ENABLE','auto-provide',2,'车辆发放','70.72.',2,70,'MENU','/auto/provide');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (73,'ENABLE','auto-return',2,'车辆归还','70.73.',3,70,'MENU','/auto/return');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (74,'ENABLE','auto-borrow-record',2,'借车记录','70.74.',4,70,'MENU','/auto/borrow-record');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (75,'ENABLE','auto-manage',2,'车辆管理','70.75.',5,70,'MENU','/auto/manage');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (76,'ENABLE','auto-driver-manage',2,'司机管理','70.76.',6,70,'MENU','/auto/driver-manage');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (77,'ENABLE','auto-manage-setting',2,'参数设置','70.77.',7,70,'MENU','/auto/manage/setting');

--论坛管理
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (80,'ENABLE','social',1,'论坛管理','80.',9,0,'MENU','/social');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (81,'ENABLE','social-settings-mailbox',2,'领导信箱','80.81.',1,80,'MENU','/social/settings/MAILBOX');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (82,'ENABLE','social-settings-advice',2,'投诉建议','80.82.',2,80,'MENU','/social/settings/ADVICE');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (83,'ENABLE','social-settings-contribute',2,'我要投稿','80.83.',3,80,'MENU','/social/settings/CONTRIBUTE');

--会议室管理
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (90,'ENABLE','meeting',1,'华大会议管理','90.',10,0,'MENU','/meeting');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (91,'ENABLE','meeting-blacklist',2,'释放被锁定人员','90.91.',1,90,'MENU','/meeting/blacklist');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (92,'ENABLE','meeting-room',2,'会议室管理','90.92.',2,90,'MENU','/meeting/room');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (93,'ENABLE','meeting-rule',2,'会议室使用规则管理','90.93.',3,90,'MENU','/meeting/rule');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (94,'ENABLE','meeting-equipment',2,'会议室设备管理','90.94.',4,90,'MENU','/meeting/equipment');

--壹卡会
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (100,'ENABLE','eka',1,'壹卡会','100.',11,0,'MENU','/eka');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (101,'ENABLE','eka-list',2,'壹卡会管理','100.101.',1,100,'MENU','/eka/list');

--办公制度
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (110,'ENABLE','regulation',1,'办公制度','110.',12,0,'MENU','/regulation');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (111,'ENABLE','regulation-profile-create',2,'新增文件','110.111.',1,110,'MENU','/regulation/profile/create');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (112,'ENABLE','regulation-profile',2,'文件管理','110.112.',2,110,'MENU','/regulation/profile');

--接口人
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (120,'ENABLE','affairs',1,'接口人','120.',13,0,'MENU','/affairs');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (121,'ENABLE','affairs-create',2,'新增接口人','120.121.',1,120,'MENU','/affairs/create');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (122,'ENABLE','affairs-list',2,'接口人管理','120.122.',2,120,'MENU','/affairs/list');

--插入模块
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (200,'ENABLE','',1,'移动平台','200.',0,0,'MODULE','');
insert into kn_resource (id,active,code,depth,name,path,seq,sup_id,type,url) values (201,'ENABLE','',1,'我的工作台','201.',0,200,'MODULE','');


--角色相关
insert into kn_role (id, create_id, create_time, update_id, update_time, active, code, description, name) values (1, null, null, null, null, 'ENABLE', 'Admin', 'admin:view,admin:edit', '管理员权限');
insert into kn_role (id, create_id, create_time, update_id, update_time, active, code, description, name) values (2, null, null, null, null, 'ENABLE', 'User', 'user:view,user:edit', '基本用户权限');

--插入角色资源关系表--管理员权限
insert into kn_role_resource (role_id,res_id) values (1,1);
insert into kn_role_resource (role_id,res_id) values (1,2);
insert into kn_role_resource (role_id,res_id) values (1,3);
insert into kn_role_resource (role_id,res_id) values (1,4);
insert into kn_role_resource (role_id,res_id) values (1,5);
insert into kn_role_resource (role_id,res_id) values (1,6);
insert into kn_role_resource (role_id,res_id) values (1,7);
insert into kn_role_resource (role_id,res_id) values (1,8);
insert into kn_role_resource (role_id,res_id) values (1,9);
insert into kn_role_resource (role_id,res_id) values (1,20);
insert into kn_role_resource (role_id,res_id) values (1,21);
insert into kn_role_resource (role_id,res_id) values (1,22);
insert into kn_role_resource (role_id,res_id) values (1,23);
insert into kn_role_resource (role_id,res_id) values (1,24);
insert into kn_role_resource (role_id,res_id) values (1,25);
insert into kn_role_resource (role_id,res_id) values (1,26);
insert into kn_role_resource (role_id,res_id) values (1,30);
insert into kn_role_resource (role_id,res_id) values (1,31);
insert into kn_role_resource (role_id,res_id) values (1,32);
insert into kn_role_resource (role_id,res_id) values (1,33);
insert into kn_role_resource (role_id,res_id) values (1,34);
insert into kn_role_resource (role_id,res_id) values (1,35);
insert into kn_role_resource (role_id,res_id) values (1,40);
insert into kn_role_resource (role_id,res_id) values (1,41);
insert into kn_role_resource (role_id,res_id) values (1,42);

--华大会议管理菜单授权--基本用户权限
insert into kn_role_resource (role_id,res_id) values (2,50);
insert into kn_role_resource (role_id,res_id) values (2,51);
insert into kn_role_resource (role_id,res_id) values (2,52);
insert into kn_role_resource (role_id,res_id) values (2,53);
insert into kn_role_resource (role_id,res_id) values (2,54);
insert into kn_role_resource (role_id,res_id) values (2,55);
insert into kn_role_resource (role_id,res_id) values (2,60);
insert into kn_role_resource (role_id,res_id) values (2,61);
insert into kn_role_resource (role_id,res_id) values (2,62);
insert into kn_role_resource (role_id,res_id) values (2,63);
insert into kn_role_resource (role_id,res_id) values (2,64);
insert into kn_role_resource (role_id,res_id) values (2,65);
insert into kn_role_resource (role_id,res_id) values (2,70);
insert into kn_role_resource (role_id,res_id) values (2,71);
insert into kn_role_resource (role_id,res_id) values (2,72);
insert into kn_role_resource (role_id,res_id) values (2,73);
insert into kn_role_resource (role_id,res_id) values (2,74);
insert into kn_role_resource (role_id,res_id) values (2,75);
insert into kn_role_resource (role_id,res_id) values (2,76);
insert into kn_role_resource (role_id,res_id) values (2,77);
insert into kn_role_resource (role_id,res_id) values (2,80);
insert into kn_role_resource (role_id,res_id) values (2,81);
insert into kn_role_resource (role_id,res_id) values (2,82);
insert into kn_role_resource (role_id,res_id) values (2,83);
insert into kn_role_resource (role_id,res_id) values (2,90);
insert into kn_role_resource (role_id,res_id) values (2,91);
insert into kn_role_resource (role_id,res_id) values (2,92);
insert into kn_role_resource (role_id,res_id) values (2,93);
insert into kn_role_resource (role_id,res_id) values (2,94);
insert into kn_role_resource (role_id,res_id) values (2,100);
insert into kn_role_resource (role_id,res_id) values (2,101);
insert into kn_role_resource (role_id,res_id) values (2,110);
insert into kn_role_resource (role_id,res_id) values (2,111);
insert into kn_role_resource (role_id,res_id) values (2,112);
insert into kn_role_resource (role_id,res_id) values (2,120);
insert into kn_role_resource (role_id,res_id) values (2,121);
insert into kn_role_resource (role_id,res_id) values (2,122);

insert into kn_user (id, create_id, create_time, update_id, update_time, email, login_ip, login_name, login_time, name, user_online, password, salt, status) values (1, null, 1406532246618, null, null, 'admin@kingnode.com', null, 'admin', null, '管理员', 0, '691b14d79bf0fa2215f155235df5e670b64394cc', '7efbd59d9741d34f', 'ENABLE');
insert into kn_user (id, create_id, create_time, update_id, update_time, email, login_ip, login_name, login_time, name, user_online, password, salt, status) values (2, 1, 1411786968142, null, null, 'duanyi@kingnode.com', null, 'duanyi', null, '段毅', 0, '0298146f4c55f34e5b675e46da9c081fc73ae4b0', 'd9fe11bb2bbcd886', 'ENABLE');
insert into kn_user (id, create_id, create_time, update_id, update_time, email, login_ip, login_name, login_time, name, user_online, password, salt, status) values (3, 1, 1411786968204, null, null, 'lijiancheng@kingnode.com', null, 'lijiancheng', null, '李建成', 0, 'f1bee0dbe7a00233e39568ffb9fd5bb48cfa4d76', '707429e7745959d2', 'ENABLE');
insert into kn_user (id, create_id, create_time, update_id, update_time, email, login_ip, login_name, login_time, name, user_online, password, salt, status) values (4, 1, 1411786968236, null, null, 'zengxi@kingnode.com', null, 'zengxi', null, '曾熙', 0, '2e57781a0edcf27bebc96f1440c5c754d322e5ca', 'e188fc46beab1f60', 'ENABLE');
insert into kn_user (id, create_id, create_time, update_id, update_time, email, login_ip, login_name, login_time, name, user_online, password, salt, status) values (5, 1, 1411786968251, null, null, 'anfanghe@kingnode.com', null, 'anfanghe', null, '安芳鹤', 0, '999faeeda415aeb0339e61265944f136b018bd95', 'd468c339255e70c3', 'ENABLE');
insert into kn_user (id, create_id, create_time, update_id, update_time, email, login_ip, login_name, login_time, name, user_online, password, salt, status) values (6, 1, 1411786968264, null, null, 'lijie@kingnode.com', null, 'lijie', null, '李洁', 0, '9d72a87cbaad9e107b49f2674400ece66910faea', '2473394a6de6a3ea', 'ENABLE');
insert into kn_user (id, create_id, create_time, update_id, update_time, email, login_ip, login_name, login_time, name, user_online, password, salt, status) values (7, 1, 1411786968278, null, null, 'chenhaohua@kingnode.com', null, 'chenhaohua', null, '陈浩华', 0, '71f12bd00420a99d4f2fb7b25100445f0c63bee0', 'd52f2c3e21a242f2', 'ENABLE');
insert into kn_user (id, create_id, create_time, update_id, update_time, email, login_ip, login_name, login_time, name, user_online, password, salt, status) values (8, 1, 1411786968292, null, null, 'liuxiaolian@kingnode.com', null, 'liuxiaolian', null, '刘晓莲', 0, '7870a03f6efaaac54ae752bd65bd05ca6499a15c', 'c750587877cd6a04', 'ENABLE');
insert into kn_user (id, create_id, create_time, update_id, update_time, email, login_ip, login_name, login_time, name, user_online, password, salt, status) values (9, 1, 1411786968304, null, null, 'xiejintao@kingnode.com', null, 'xiejintao', null, '谢锦桃', 0, '40518d99b0c7f5cf6a231a363cc0ef5b01cf7a2e', '7fa139680c80291a', 'ENABLE');

--插入用户角色关系表
INSERT INTO kn_user_role (role_id,user_id) VALUES (1,1);
INSERT INTO kn_user_role (role_id,user_id) VALUES (1,2);

insert into kn_employee (id, address, email, image_address, job, login_name, mark_name, phone, sex, signature, telephone, user_id, user_name, user_system, user_type, weixin_id) values (2, null, 'duanyi@kingnode.com', null, 'ENABLE', 'duanyi', '', null, 'NONE', null, null, '1', '段毅', 'BIGTECH', 'employee', '');
insert into kn_employee (id, address, email, image_address, job, login_name, mark_name, phone, sex, signature, telephone, user_id, user_name, user_system, user_type, weixin_id) values (3, null, 'lijiancheng@kingnode.com', null, 'ENABLE', 'lijiancheng', '', null, 'NONE', null, null, '2', '李建成', 'BIGTECH', 'employee', '');
insert into kn_employee (id, address, email, image_address, job, login_name, mark_name, phone, sex, signature, telephone, user_id, user_name, user_system, user_type, weixin_id) values (4, null, 'zengxi@kingnode.com', null, 'ENABLE', 'zengxi', '', null, 'NONE', null, null, '3', '曾熙', 'BIGTECH', 'employee', '');
insert into kn_employee (id, address, email, image_address, job, login_name, mark_name, phone, sex, signature, telephone, user_id, user_name, user_system, user_type, weixin_id) values (5, null, 'anfanghe@kingnode.com', null, 'ENABLE', 'anfanghe', '', null, 'NONE', null, null, '4', '安芳鹤', 'BIGTECH', 'employee', '');
insert into kn_employee (id, address, email, image_address, job, login_name, mark_name, phone, sex, signature, telephone, user_id, user_name, user_system, user_type, weixin_id) values (6, null, 'lijie@kingnode.com', null, 'ENABLE', 'lijie', '', null, 'NONE', null, null, '5', '李洁', 'BIGTECH', 'employee', '');
insert into kn_employee (id, address, email, image_address, job, login_name, mark_name, phone, sex, signature, telephone, user_id, user_name, user_system, user_type, weixin_id) values (7, null, 'chenhaohua@kingnode.com', null, 'ENABLE', 'chenhaohua', '', null, 'NONE', null, null, '6', '陈浩华', 'BIGTECH', 'employee', '');
insert into kn_employee (id, address, email, image_address, job, login_name, mark_name, phone, sex, signature, telephone, user_id, user_name, user_system, user_type, weixin_id) values (8, null, 'liuxiaolian@kingnode.com', null, 'ENABLE', 'liuxiaolian', '', null, 'NONE', null, null, '7', '刘晓莲', 'BIGTECH', 'employee', '');
insert into kn_employee (id, address, email, image_address, job, login_name, mark_name, phone, sex, signature, telephone, user_id, user_name, user_system, user_type, weixin_id) values (9, null, 'xiejintao@kingnode.com', null, 'ENABLE', 'xiejintao', '', null, 'NONE', null, null, '8', '谢锦桃', 'BIGTECH', 'employee', '');

--插入组织
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (1,0,'KND','盈诺德','1.',1,'COMPANY',1,'ENABLE');
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (2,1,'KNDM','移动事业部','1.2.',2,'DEPARTMENT',2,'ENABLE');
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (3,1,'KNDM','总经理办公室','1.3.',2,'DEPARTMENT',2,'ENABLE');
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (4,1,'KNDM','财务部','1.4.',2,'DEPARTMENT',2,'ENABLE');
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (5,1,'KNDM','知识管理中心','1.5.',2,'DEPARTMENT',2,'ENABLE');
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (6,1,'KNDM','人力资源部门','1.6.',2,'DEPARTMENT',2,'ENABLE');
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (7,1,'KNDM','HCM事业部','1.7.',2,'DEPARTMENT',2,'ENABLE');
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (8,1,'KNDM','技术服务中心','1.8.',2,'DEPARTMENT',2,'ENABLE');
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (9,1,'KNDM','市场销售部','1.9.',2,'DEPARTMENT',2,'ENABLE');
insert into kn_organization (id,sup_id,code,name,path,depth,org_type,seq,active) values (10,1,'KNDM','咨询业务部','1.10.',2,'DEPARTMENT',2,'ENABLE');

--初始化岗位表
insert into kn_position (id, create_id, create_time, update_id, update_time, active, code, depth, description, name, path, seq, sup_id) values (1, null, null, null, null, 'ENABLE', 'KND', 1, null, '主管', '1.', 1, 0);

--初始应用数据
insert into kn_application_info (id,title,entitle,icon,api_key,forfirm,remark,work_status,create_id,create_time) values (1,'华大科技','BIGTECH','','b62e1542-f746-468c-ba62-487410a7975c','华大科技有限公司','','usable',1,1411786969058);

--会议室设备表
insert into kn_meeting_equipment (id,name,icon,status) values(1,'投影仪', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg','AVAILABLE');
insert into kn_meeting_equipment (id,name,icon,status) values(2,'白板', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg','AVAILABLE');
insert into kn_meeting_equipment (id,name,icon,status) values(3,'视频设备', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg','AVAILABLE');
insert into kn_meeting_equipment (id,name,icon,status) values(4,'音频设备', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg','AVAILABLE');

--会议室表
insert into kn_meeting_room (id, create_id, create_time, update_id, update_time, addr, icon, name, num, remark, status) values (1, 1, 1411649397317, null, null, '会议室1', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg', '会议室1', 11, null, 'AVAILABLE');
insert into kn_meeting_room (id, create_id, create_time, update_id, update_time, addr, icon, name, num, remark, status) values (2, 1, 1411649412299, null, null, '会议室2', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg', '会议室2', 22, null, 'AVAILABLE');
insert into kn_meeting_room (id, create_id, create_time, update_id, update_time, addr, icon, name, num, remark, status) values (3, 1, 1411649426249, null, null, '会议室3', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg', '会议室3', 33, null, 'AVAILABLE');
insert into kn_meeting_room (id, create_id, create_time, update_id, update_time, addr, icon, name, num, remark, status) values (4, 1, 1411649434949, null, null, '会议室4', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg', '会议室4', 44, null, 'AVAILABLE');
insert into kn_meeting_room (id, create_id, create_time, update_id, update_time, addr, icon, name, num, remark, status) values (5, 1, 1411649444132, null, null, '会议室55', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg', '会议室55', 55, null, 'AVAILABLE');
insert into kn_meeting_room (id, create_id, create_time, update_id, update_time, addr, icon, name, num, remark, status) values (6, 1, 1411649453478, null, null, '会议室6', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg', '会议室6', 66, null, 'AVAILABLE');
insert into kn_meeting_room (id, create_id, create_time, update_id, update_time, addr, icon, name, num, remark, status) values (7, 1, 1411649462757, null, null, '会议室7', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg', '会议室7', 77, null, 'AVAILABLE');
insert into kn_meeting_room (id, create_id, create_time, update_id, update_time, addr, icon, name, num, remark, status) values (8, 1, 1411649473358, null, null, '会议室8', 'http://img.woyaogexing.com/2014/09/26/2aa539e455437a2f!200x200.jpg', '会议室8', 88, null, 'AVAILABLE');

--插入壹卡会内容大类
insert into kn_eka_categories(id,name) values(1,'壹卡会');
insert into kn_eka_categories(id,name) values(2,'购物');
insert into kn_eka_categories(id,name) values(3,'运动健身');
insert into kn_eka_categories(id,name) values(4,'生活服务');
--插入壹卡会内容小类
insert into kn_eka_small_class(id,name,categories_Id) values(1,'购物',1);
insert into kn_eka_small_class(id,name,categories_Id) values(2,'餐饮美食',1);
insert into kn_eka_small_class(id,name,categories_Id) values(3,'生活服务',1);
insert into kn_eka_small_class(id,name,categories_Id) values(4,'休闲娱乐',1);
insert into kn_eka_small_class(id,name,categories_Id) values(5,'丽人',1);
insert into kn_eka_small_class(id,name,categories_Id) values(6,'家装',1);
insert into kn_eka_small_class(id,name,categories_Id) values(7,'超市',2);
insert into kn_eka_small_class(id,name,categories_Id) values(8,'便利店',2);
insert into kn_eka_small_class(id,name,categories_Id) values(9,'商场',2);
insert into kn_eka_small_class(id,name,categories_Id) values(10,'华大运动健身房',3);
insert into kn_eka_small_class(id,name,categories_Id) values(11,'公交车站',4);
insert into kn_eka_small_class(id,name,categories_Id) values(12,'邮局',4);
insert into kn_eka_small_class(id,name,categories_Id) values(13,'派出所',4);
insert into kn_eka_small_class(id,name,categories_Id) values(14,'银行',4);
insert into kn_eka_small_class(id,name,categories_Id) values(15,'打印店',4);
insert into kn_eka_small_class(id,name,categories_Id) values(16,'照相馆',4);
insert into kn_eka_small_class(id,name,categories_Id) values(17,'医院',4);

insert into kn_table_sequnce(sequence_name,sequence_next_hi_value) values('kn_resource',202);
insert into kn_table_sequnce(sequence_name,sequence_next_hi_value) values('kn_role',3);
insert into kn_table_sequnce(sequence_name,sequence_next_hi_value) values('kn_user',10);
insert into kn_table_sequnce(sequence_name,sequence_next_hi_value) values('kn_organization',11);
insert into kn_table_sequnce(sequence_name,sequence_next_hi_value) values('kn_position',2);
insert into kn_table_sequnce(sequence_name,sequence_next_hi_value) values('kn_application_info',2);
insert into kn_table_sequnce(sequence_name,sequence_next_hi_value) values('kn_meeting_equipment',5);
insert into kn_table_sequnce(sequence_name,sequence_next_hi_value) values('kn_meeting_room',9);
