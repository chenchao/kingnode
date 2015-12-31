====
创建开发分支,开始进行工单的开发方式！

===
发布代码方式
scp kingnode-* root@180.150.179.167:/opt/knd/apache-tomcat-7.0.55/webapps/ROOT/WEB-INF/lib/
 

===备份的代码

    @Transactional(readOnly=false)
    public void importOrg(File file,String fileName) throws IOException{
        Workbook wb=null;
        if(fileName.endsWith("xlsx")){
            wb=new XSSFWorkbook(new FileInputStream(file));// 操作Excel2007的版本，扩展名是.xlsx
        }else if(fileName.endsWith("xls")){
            wb=new HSSFWorkbook(new FileInputStream(file));// 操作Excel2003以前（包括2003）的版本，扩展名是.xls
        }
        Sheet sheet=wb.getSheetAt(0);
        int rowNum=sheet.getLastRowNum()+2;
        for(int i=2;i<rowNum;i++){
            Row row=sheet.getRow(i);
            if(row!=null){
                Cell cell=row.getCell(0);
                KnOrganization org=new KnOrganization();
                String o=cell.getStringCellValue();
                String[] v=o.split("\\/");
                if(v.length>1){
                    KnOrganization _org=organizationService.ReadOrg(v[v.length-2]);
                    org.setSupId(_org.getId());
                    org.setName(v[v.length-1]);
                }else{
                    org.setSupId(0L);
                    org.setName(v[0]);
                }
                org.setActive(IdEntity.ActiveType.ENABLE);
                cell=row.getCell(1);
                String type=cell.getStringCellValue();
                if("公司".equals(type)){
                    org.setOrgType(KnOrganization.OrgType.COMPANY);
                }else if("机构".equals(type)){
                    org.setOrgType(KnOrganization.OrgType.ORGANIZATION);
                }else if("部门".equals(type)){
                    org.setOrgType(KnOrganization.OrgType.DEPARTMENT);
                }
                org.setDescription(o);
                organizationService.SaveOrg(org);
                System.out.println(org);
            }
        }
    }
    @Transactional(readOnly=false)
    public void importEmployee(File file,String fileName) throws IOException{
        Workbook wb=null;
        if(fileName.endsWith("xlsx")){
            wb=new XSSFWorkbook(new FileInputStream(file));// 操作Excel2007的版本，扩展名是.xlsx
        }else if(fileName.endsWith("xls")){
            wb=new HSSFWorkbook(new FileInputStream(file));// 操作Excel2003以前（包括2003）的版本，扩展名是.xls
        }
        Sheet sheet=wb.getSheetAt(0);
        int rowNum=sheet.getLastRowNum()+2;
        Map<String,String> map=Maps.newHashMap();
        Map<String,KnUser> um=Maps.newHashMap();
        for(int i=2;i<rowNum;i++){
            Row row=sheet.getRow(i);
            if(row!=null){
                Cell cell=row.getCell(1);//获取账号
                String v=cell.getStringCellValue();
                KnUser ku=knUserDao.findByLoginName(v);
                KnEmployee kn=new KnEmployee();
                if(ku==null){
                    ku=new KnUser();
                    ku.setSalt(Encodes.encodeHex(Digests.generateSalt(ResourceService.SALT_SIZE)));
                    kn.setLoginName(v);
                    ku.setLoginName(v);
                    kn.setJob(IdEntity.ActiveType.ENABLE);
                    ku.setStatus(IdEntity.ActiveType.ENABLE);
                    kn.setSex(KnEmployee.Gender.NONE);
                    kn.setEmail(v+"@fangdd.com");
                    ku.setEmail(kn.getEmail());
                }
                kn.setId(ku.getId());
                cell=row.getCell(0);//他系统ID
                kn.setUserId(String.valueOf(Double.valueOf(cell.getNumericCellValue()).intValue()));
                cell=row.getCell(2);//密码
                v=cell.getStringCellValue();
                ku.setPlainPassword(v);
                cell=row.getCell(3);//用户姓名
                v=cell.getStringCellValue();
                ku.setName(v);
                kn.setUserName(v);
                cell=row.getCell(4);//主管人员，后面后面生成主管信息
                if(cell!=null){
                    v=cell.getStringCellValue();
                    map.put(v,ku.getLoginName());
                }
                cell=row.getCell(5);//确定用户的组织机构
                v=cell.getStringCellValue();
                List<KnOrganization> orgs=orgDao.findByDescription(v);
                KnOrganization _org=orgs.get(orgs.size()-1);
                KnEmployeeOrganization keo=new KnEmployeeOrganization();
                KnEmployeeOrganizationId id=new KnEmployeeOrganizationId();
                id.setOrg(_org);
                cell=row.getCell(6);//确定部门负责人
                v=cell.getStringCellValue();
                keo.setCharge("是".equals(v)?1:0);
                cell=row.getCell(7);//用户类
                v=cell.getStringCellValue();
                kn.setUserType(v);
                //
                //
                cell=row.getCell(8);//来自系统
                v=cell.getStringCellValue();
                kn.setUserSystem(v);
                cell=row.getCell(9);//角色
                v=cell.getStringCellValue();
                KnRole role=knRoleDao.findByName(v);
                organizationService.SaveKnEmployee(ku,kn,new Long[]{role.getId()});
                um.put(ku.getLoginName(),ku);
                id.setEmp(kn);
                keo.setId(id);
                keo.setMajor(1);
                empOrgDao.save(keo);
            }
        }
        //处理岗位分管的问题
        Map<String,KnEmployee> em=Maps.newHashMap();
        for(String str : map.keySet()){
            String leader=map.get(str);
            KnEmployee e=em.get(leader);
            if(e==null){
                e=organizationService.ReadKnEmployee(leader);
                em.put(leader,e);
            }
            KnEmployeePosition p=empPosDao.findByIdPosIdAndIdEmpId(1L,e.getId());
            if(p==null){
                p=new KnEmployeePosition();
                KnEmployeePositionId id=new KnEmployeePositionId(new KnPosition(1L),e);
                p.setId(id);
                p.setMajor(IdEntity.ActiveType.ENABLE);
                empPosDao.save(p);
            }
            KnUser u=um.get(str);
            if(u!=null){
                organizationService.JoinPositionBranched(1l,"GM","主管",e.getId(),e.getUserName(),u.getId(),u.getName());
            }
        }
    }
    
    
    @Transactional(readOnly=false)
    public void importEmployee(File file,String fileName) throws IOException{
        Workbook wb=null;
        if(fileName.endsWith("xlsx")){
            wb=new XSSFWorkbook(new FileInputStream(file));// 操作Excel2007的版本，扩展名是.xlsx
        }else if(fileName.endsWith("xls")){
            wb=new HSSFWorkbook(new FileInputStream(file));// 操作Excel2003以前（包括2003）的版本，扩展名是.xls
        }
        Sheet sheet=wb.getSheetAt(0);
        int rowNum=sheet.getLastRowNum()+2;
        Map<String,String> map=Maps.newHashMap();
        for(int i=2;i<rowNum;i++){
            Row row=sheet.getRow(i);
            if(row!=null){
                Cell cell=row.getCell(1);//获取账号
                KnEmployee u=knEmployeeDao.findByLoginName(cell.getStringCellValue());
                cell=row.getCell(4);//主管人员，后面后面生成主管信息
                if(cell!=null){
                    String v=cell.getStringCellValue();
                    KnEmployee ke=knEmployeeDao.findByLoginName(v);
                    if(ke!=null){

                        organizationService.JoinPositionBranched(1L,"GM","主管",ke.getId(),ke.getUserName(),u.getId(),u.getUserName());
                    }else{
                        map.put(v,v);
                    }
                }
            }
        }
        for(String str:map.keySet()){
            System.out.println(str);
        }
    }