package com.kingnode.xsimple.util.excel;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.dto.ExcelDTO;
import com.kingnode.xsimple.dto.ImportEmpDTO;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnRole;
import com.kingnode.xsimple.entity.system.KnUser;
import com.kingnode.xsimple.service.system.ResourceService;
import com.kingnode.xsimple.util.PathUtil;
import com.kingnode.xsimple.util.Utils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
*
 * 生成应用的下载文件
 *
*/
public class DownExcel{
    private static DownExcel downExcel=null;
    private final Logger log=LoggerFactory.getLogger(DownExcel.class);
    private DownExcel(){
    }
    public static DownExcel getInstall(){
        if(null==downExcel){
            synchronized(DownExcel.class){
                if(downExcel==null){
                    downExcel=new DownExcel();
                }
            }
        }
        return downExcel;
    }
    /**
     * @Description: (下载excel 模板文件)
     */
    public void downloadExcel(String tip,HttpServletResponse response,LinkedList<String> titleList){
        try{
            String targetDirectory=PathUtil.getRootPath(), path="";
            if(!Strings.isNullOrEmpty(tip)){
                if(tip.equals("1")||tip.equals("6")){
                    path=getDownFile(tip,targetDirectory,titleList);//获取下载文件的路径
                }else if(tip.equals("2")){
                    path=targetDirectory+Setting.downloadExcel+"/"+"bilityInf.xls";
                }else if(tip.equals("3")){
                    path=targetDirectory+Setting.downloadExcel+"/"+"companyInfo.xls";
                }else if(tip.equals("4")){
                    path=targetDirectory+Setting.downloadExcel+"/"+"companyPhoneInfo.xls";
                }else if(tip.equals("5")){
                    path=targetDirectory+Setting.downloadExcel+"/"+"userDetail.xls";
                }else if(tip.equals("7")){
                    path=targetDirectory+Setting.downloadExcel+"/"+"crmSignAttAndAdd.xls";
                }else if("8".equals(tip)){
                    path=targetDirectory+Setting.downloadExcel+"/"+"crmAttDeptUser.xls";
                }else if("9".equals(tip)){
                    path=targetDirectory+Setting.downloadExcel+"/"+"orgUser.xlsx";
                }
            }
            File file=new File(path);// path是指欲下载的文件的路径。
            InputStream fis=new BufferedInputStream(new FileInputStream(path));// 以流的形式下载文件。
            byte[] buffer=new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            response.setContentType("application/octet-stream;charset=utf-8");
            response.addHeader("Content-Disposition","attachment;filename="+new String(file.getName().getBytes(),"ISO-8859-1"));
            response.addHeader("Content-Length",""+file.length());
            OutputStream toClient=new BufferedOutputStream(response.getOutputStream());
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            //downLoadFile(response,path);
        }catch(Exception e){
            log.error("下载excel模板文件，错误信息 {}",e);
        }
    }
    /**
     * 具体下载文件
     *
     * @param response 响应的请求
     * @param filePath 文件路径
     */
    private void downLoadFile(HttpServletResponse response,String filePath) throws Exception{
        try{
            response.reset();
            response.setContentType("application/octet-stream");
            String fileName=new File(filePath).getName();
            fileName=response.encodeURL(new String(fileName.getBytes(),"ISO8859_1"));//转码
            response.setHeader("Content-Disposition","inline; filename=\""+fileName+"\"");
            ServletOutputStream out=response.getOutputStream();
            InputStream inStream=new FileInputStream(filePath);
            //循环取出流中的数据
            byte[] b=new byte[1024];
            int len;
            while((len=inStream.read(b))>0){
                out.write(b,0,len);
            }
            response.setStatus(response.SC_OK);
            response.flushBuffer();
            out.close();
            inStream.close();
        }catch(Exception e){
        }
    }
    /**
     * @param @param targetDirectory 路径
     *
     * @Description: (动态生成模板文件)
     * @param: @param tip 标示用户下载excel还是职责下载
     */
    private String getDownFile(String tip,String targetDirectory,LinkedList<String> titleList){
        if(tip.equals("1")){
            return createExcel(tip,titleList.toArray(new String[]{}),targetDirectory+Setting.downloadExcel+"/"+"用户信息导入.xls");
        }else{
            return createExcel(tip,titleList.toArray(new String[]{}),targetDirectory+Setting.downloadExcel+"/"+"appstore用户信息导入.xls");
        }
    }
    /**
     * @Description: (动态生成excel，并含有下拉框)
     * @param: @param roleList 下拉框值 集合
     * @param: @param path    文件生成路径
     * @return: String 返回 生成文件的路径
     */
    private String createExcel(String tip,String[] roleList,String path){
        HSSFWorkbook wb=new HSSFWorkbook();
        //生成第一个sheet
        HSSFFont font=wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short)12);
        // 设置样式
        HSSFCellStyle cellStyle=wb.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        String sheetName="", fistValue="", secondValue="";
        if(tip.equals("1")){
            sheetName="用户导入信息";
            fistValue="导入的用户信息表格（如不清楚响应的操作，可点击'上传excel说明'标签进行查看）";
            secondValue="用户的信息导入时请尽量避免空白行的出现，第一条数据为导入的例子，如您不需要可以进行删除；用户如果不想配置角色，可以不用选择，如果选择了角色，用户在导入过程中将会将用户的角色一起进行导入的操作";
        }else{
            sheetName="appstore用户导入信息";
            fistValue="导入的appstore用户信息表格（如不清楚响应的操作，可点击'上传excel说明'标签进行查看）";
            secondValue="appstore用户的信息导入时请尽量避免空白行的出现，第一条数据为导入的例子，如您不需要可以进行删除；用户如果不想配置角色，可以不用选择，如果选择了角色，用户在导入过程中将会将用户的角色一起进行导入的操作";
        }
        HSSFSheet sheetOne=wb.createSheet(sheetName);
        CellRangeAddress cellRangeAddress=new CellRangeAddress(0,0,0,11);//合并单元格
        sheetOne.addMergedRegion(cellRangeAddress);
        HSSFRow rowOne=sheetOne.createRow(0);
        rowOne.setHeightInPoints(30);//单元格的高度
        HSSFCell cellFisrt=(HSSFCell)rowOne.createCell(0);
        // 定义单元格为字符串类型
        cellFisrt.setCellType(HSSFCell.CELL_TYPE_STRING);
        cellFisrt.setCellStyle(cellStyle);
        cellFisrt.setCellValue(fistValue);
        HSSFRow rowSecond=sheetOne.createRow(1);
        //模板
        HSSFRow row_Two=sheetOne.createRow(2);
        if(tip.equals("1")){
            for(int i=0;i<8;i++){
                HSSFCell cell=(HSSFCell)rowSecond.createCell(i);
                sheetOne.setColumnWidth(i,30*200);
                switch(i){
                case 0:
                    cell.setCellValue("用户ID");
                    break;
                case 1:
                    cell.setCellValue("姓名");
                    break;
                case 2:
                    cell.setCellValue("账号");
                    break;
                case 3:
                    cell.setCellValue("密码");
                    break;
                case 4:
                    cell.setCellValue("用户类型");
                    break;
                case 5:
                    cell.setCellValue("来自系统");
                    break;
                case 6:
                    cell.setCellValue("角色名称");
                    break;
                case 7:
                    cell.setCellValue("邮箱");
                    break;
                default:
                    break;
                }
            }
            //模板数据
            for(int i=0;i<8;i++){
                HSSFCell cell=(HSSFCell)row_Two.createCell(i);
                switch(i){
                case 0:
                    cell.setCellValue("1314");
                    break;
                case 1:
                    cell.setCellValue("张三");
                    break;
                case 2:
                    cell.setCellValue("uaccount");
                    break;
                case 3:
                    cell.setCellValue("123456");
                    break;
                case 4:
                    cell.setCellValue("employee");
                    break;
                case 5:
                    cell.setCellValue("ERP");
                    break;
                case 6:
                    cell.setCellValue("角色名称");
                    break;
                case 7:
                    cell.setCellValue("test@kingnode.com");
                    break;
                default:
                    break;
                }
            }
        }else{
            for(int i=0;i<2;i++){
                HSSFCell cell=(HSSFCell)rowSecond.createCell(i);
                sheetOne.setColumnWidth(i,30*200);
                switch(i){
                case 0:
                    cell.setCellValue("账号");
                    break;
                case 1:
                    cell.setCellValue("密码");
                    break;
                default:
                    break;
                }
            }
            //模板数据
            for(int i=0;i<2;i++){
                HSSFCell cell=(HSSFCell)row_Two.createCell(i);
                switch(i){
                case 0:
                    cell.setCellValue("knd1");
                    break;
                case 1:
                    cell.setCellValue("111111");
                    break;
                default:
                    break;
                }
            }
        }
        //先创建一个Sheet专门用于存储下拉项的值，并将各下拉项的值写入其中  //设置下拉框begin
        if(roleList.length>0){
            HSSFSheet sheet_hiden=wb.createSheet("ShtDictionary");
            for(int i=0;i<roleList.length;i++){
                sheet_hiden.createRow(i).createCell(0).setCellValue(roleList[i]);//设置下拉框值
            }
            // 然后定义一个名称，指向刚才创建的下拉项的区域
            HSSFName range=wb.createName();//"ShtDictionary!$A$1:$A$3";  指定引用位置
            StringBuffer reference=new StringBuffer("ShtDictionary!$A$1:$A$");
            reference.append(roleList.length);
            range.setReference(reference.toString());
            range.setNameName("dicRange");
            //隐藏 sheet
            wb.setSheetHidden(1,true);
            CellRangeAddressList regions=new CellRangeAddressList(2,-1,6,6);//下拉框的位置
            DVConstraint constraint=DVConstraint.createFormulaListConstraint("dicRange");//指定引用位置 放进下拉框
            HSSFDataValidation data=new HSSFDataValidation(regions,constraint);//绑定下拉框和作用区域
            sheetOne.addValidationData(data);//对sheet页生效
        }else{
            CellRangeAddressList regions=new CellRangeAddressList(2,-1,6,6);//下拉框的位置
            DVConstraint constraint=DVConstraint.createExplicitListConstraint(roleList);//将list设置进下拉框  下拉框值不多的时候用
            HSSFDataValidation data=new HSSFDataValidation(regions,constraint);//绑定下拉框和作用区域
            sheetOne.addValidationData(data);//对sheet页生效
        }
        //设置下拉框end
        //生成第二个sheet
        HSSFSheet sheetTwo=wb.createSheet("上传excel说明");
        HSSFRow rowTwo=sheetTwo.createRow(0);
        HSSFCell cellTwo=(HSSFCell)rowTwo.createCell(0);
        CellRangeAddress cellAddress=new CellRangeAddress(0,0,0,25);
        sheetTwo.addMergedRegion(cellAddress);
        rowTwo.setHeightInPoints(30);
        // 定义单元格为字符串类型
        cellTwo.setCellType(HSSFCell.CELL_TYPE_STRING);
        cellTwo.setCellStyle(cellStyle);
        cellTwo.setCellValue(secondValue);
        //将excel保存到本地
        try{
            FileOutputStream fileOut=new FileOutputStream(path);
            wb.write(fileOut);
            fileOut.close();
        }catch(Exception e){
            log.error("生成Excel 文件 出错,错误信息{}",e);
            path="";
        }
        return path;
    }
    /**
     * <分析Excel，组装显示在页面中>
     *
     * @param: @param uploadFileFileName
     * @param: @param directory
     * @param: @param tip 1标示上传用户    2标示上传职责
     * @Date 2014-1-17 下午04:08:06
     */
    public Map<String,Object> getObjInfOfExcel(File target,String uploadFileFileName,String tip){
        Map<String,Object> map=new HashMap<String,Object>();
        try{
            Workbook wb=null;
            if(uploadFileFileName.endsWith("xlsx")){
                try{
                    wb=new XSSFWorkbook(new FileInputStream(target));// 操作Excel2007的版本，扩展名是.xlsx
                }catch(Exception e){
                    map.put("errorBack","errorFileError");
                    log.error("分析Excel，出错 {}",e);
                }
            }else if(uploadFileFileName.endsWith("xls")){
                try{
                    wb=new HSSFWorkbook(new FileInputStream(target));// 操作Excel2003以前（包括2003）的版本，扩展名是.xls
                }catch(Exception e){
                    map.put("errorBack","errorFileError");
                    log.error("分析Excel，出错 {}",e);
                }
            }else{
                map.put("errorBack","errorFileError");
            }
            if(wb!=null){
                map=getImportListInfo(wb,tip);
            }
        }catch(Exception e){
            log.error("分析Excel，组装显示在页面中,错误信息{}",e);
        }
        return map;
    }
    /**
     * @Description: (处理excel文件数据)
     * @param: @param wb Workbook 对象
     * @param: @param tip 标示
     * @Date 2014-6-10 上午10:27:33
     */
    private Map<String,Object> getImportListInfo(Workbook wb,String tip){
        List<Object> objList=new LinkedList<Object>();
        List<Object> objTList=new LinkedList<Object>();
        Map<String,Object> map=new HashMap<String,Object>();
        Map<String,Object> loginMap=new HashMap<String,Object>();
        StringBuffer errorBack=new StringBuffer();
        StringBuffer errorBack_two=new StringBuffer();
        try{
            //多个标签 导入数据
            int tipA=0, tipC=0;
            for(int h=0;h<wb.getNumberOfSheets();h++){//获取每个Sheet表
                int tipB=0, tipD=0;
                Sheet sh=wb.getSheetAt(h);
                String sheetName=sh.getSheetName();
                boolean aBool = sheetName.indexOf("ShtDictionary")!=-1,bBool= sheetName.indexOf("说明")!=-1;
                if(aBool||bBool){
                    continue;
                }
                int rowNum=sh.getLastRowNum()+1;
                if(rowNum==2){//没有数据
                    continue;
                }else{
                    for(int i=2;i<rowNum;i++){
                        Row row=sh.getRow(i);
                        if("1".equals(tip)){//上传用户
                               /*用户id\姓名\账号\密码\类型\来自系统 */
                            Cell c1=row.getCell(0); //用户ID
                            Cell c2=row.getCell(1); //姓名
                            Cell c3=row.getCell(2); //账号
                            Cell c4=row.getCell(3); //密码
                            Cell c5=row.getCell(4); //用户类型
                            Cell c6=row.getCell(5); //来自系统
                            Cell c7=row.getCell(6); //角色名称
                            Cell c8=row.getCell(7); //邮件
                            if(null==c1||null==c3||null==c5||null==c6 ||null ==c8){//判断不能为空
                                if(tipB==0){
                                    if(tipA>0){
                                        errorBack=new StringBuffer(errorBack.substring(0,errorBack.length()-1)).append("行").append(",");
                                    }
                                    errorBack.append("sheet").append(h+1).append("中,第").append(i+1).append(",");
                                }else{
                                    errorBack.append(i+1).append(",");
                                }
                                tipA++;
                                tipB++;
                                continue;
                            }else{
                                List<Object> tempList=new LinkedList<Object>();
                                KnEmployee knEmp=new KnEmployee();
                                knEmp.setUserId(getValueByCell(c1));
                                knEmp.setUserType(getValueByCell(c5));
                                knEmp.setUserSystem(getValueByCell(c6));
                                knEmp.setEmail(getValueByCell(c8));
                                KnUser knUser=new KnUser();
                                knUser.setName(getValueByCell(c2));
                                knUser.setLoginName(getValueByCell(c3));
                                knUser.setPassword(getValueByCell(c4));
                                List<KnRole> tempRoleList=new ArrayList<>();
                                KnRole knRole=new KnRole();
                                knRole.setName(getValueByCell(c7));
                                tempRoleList.add(knRole);
                                knUser.setRole(tempRoleList);
                                tempList.add(knEmp);
                                tempList.add(knUser);
                                objList.add(tempList);
                            }
                        }else if("2".equals(tip)){//职责信息预览信息
                               /*职责id\名称\来自系统 */
                            Cell c1=row.getCell(0); //职责ID
                            Cell c2=row.getCell(1); //名称
                            Cell c3=row.getCell(2); //来自系统
                            if(null==c1||null==c2||null==c3){//判断不能为空
                                if(tipB==0){
                                    if(tipA>0){
                                        errorBack=new StringBuffer(errorBack.substring(0,errorBack.length()-1)).append("行").append(",");
                                    }
                                    errorBack.append("sheet").append(h+1).append("中,第").append(i+1).append(",");
                                }else{
                                    errorBack.append(i+1).append(",");
                                }
                                tipA++;
                                tipB++;
                                continue;
                            }else{
                                /*KnReponsibilityThird knReponsibilityThird=new KnReponsibilityThird();
                                knReponsibilityThird.setResponsibilityId(getValueByCell(c1));
                                knReponsibilityThird.setResponsibilityName(getValueByCell(c2));
                                knReponsibilityThird.setFromSys(getValueByCell(c3));
                                objList.add(knReponsibilityThird);*/
                            }
                        }else if("3".equals(tip)){//公司信息预览信息
                        }else if("5".equals(tip)){//OA用户预览信息
                        }else if("7".equals(tip)){//CRM考勤班次 考勤地址信息
                            /* 考勤班次导入信息如下---ID\班次名称\有效工时\考勤方式\上午上班时间\上午下班时间\下午上班时间\下午下班时间\提醒时间*/
                            /* 考勤地址导入信息如下---班次ID\地址名称\签到类型\经度\纬度\有效距离*/
                            Cell c7 =null,c8=null,c9=null;
                            boolean bool = sheetName.indexOf("班次信息")!=-1;
                            if(bool){
                                 c7=row.getCell(6); //下午上班时间
                                 c8=row.getCell(7); //下午下班时间
                                 c9=row.getCell(8); //提醒时间
                            }
                            Cell c1=row.getCell(0); // 对应考勤班次 id, 考勤地址中的班次id
                            Cell c2=row.getCell(1); //对应考勤班次 班次名称, 考勤地址中的地址名称
                            Cell c3=row.getCell(2); //对应考勤班次 有效工时, 考勤地址中的签到类型
                            Cell c4=row.getCell(3); //对应考勤班次  考勤方式, 考勤地址中的经度
                            Cell c5=row.getCell(4); //对应考勤班次 上午上班时间, 考勤地址中的纬度
                            Cell c6=row.getCell(5); //对应考勤班次 上午下班时间, 考勤地址中的有效距离

                            if(null==c2 ||Utils.isEmptyString(c2)){//判断不能为空
                                if(tipB==0){
                                    if(tipA>0){
                                        errorBack=new StringBuffer(errorBack.substring(0,errorBack.length()-1)).append("行").append(",");
                                    }
                                    errorBack.append("sheet").append(h+1).append("中,第").append(i+1).append(",");
                                }else{
                                    errorBack.append(i+1).append(",");
                                }
                                tipA++;
                                tipB++;
                                continue;
                            }else{
                                ExcelDTO  dto = new ExcelDTO();
                                dto.setCol1(getValueByCell(c1));
                                dto.setCol2(getValueByCell(c2));
                                dto.setCol3(getValueByCell(c3));
                                dto.setCol4(getValueByCell(c4));
                                dto.setCol5(getValueByCell(c5));
                                dto.setCol6(getValueByCell(c6));
                                if(bool){
                                    dto.setCol7(getValueByCell(c7));
                                    dto.setCol8(getValueByCell(c8));
                                    dto.setCol9(getValueByCell(c9));
                                    objList.add(dto);
                                }else{
                                    objTList.add(dto);
                                }
                            }
                        }else if("8".equals(tip)){//CRM 人员与考勤班次关系
                            /* 考勤班次 部门 账号导入信息如下---类型\账号\部门名称\班次名称*/
                            Cell c1=row.getCell(0); // 对应考勤班次 部门 账号 类型
                            Cell c2=row.getCell(1); //对应考勤班次 部门 账号 账号
                            Cell c3=row.getCell(2); //对应考勤班次 部门 账号 部门名称
                            Cell c4=row.getCell(3); //对应考勤班次 部门 账号 班次名称
                            if(null==c1 || Utils.isEmptyString(c1)|| null == c4||Utils.isEmptyString(c4)){//判断不能为空
                                if(tipB==0){
                                    if(tipA>0){
                                        errorBack=new StringBuffer(errorBack.substring(0,errorBack.length()-1)).append("行").append(",");
                                    }
                                    errorBack.append("sheet").append(h+1).append("中,第").append(i+1).append(",");
                                }else{
                                    errorBack.append(i+1).append(",");
                                }
                                tipA++;
                                tipB++;
                                continue;
                            }else{
                                ExcelDTO  dto = new ExcelDTO();
                                dto.setCol1(getValueByCell(c1));
                                dto.setCol2(getValueByCell(c2));
                                dto.setCol3(getValueByCell(c3));
                                dto.setCol4(getValueByCell(c4));
                                objList.add(dto);
                            }
                        }else if("9".equals(tip)){//组织人员
                            boolean bool = sheetName.indexOf("组织信息")!=-1;
                            if(bool){
                                /* 组织信息*/
                                Cell c1=row.getCell(0); // 组织信息，组织ID
                                Cell c2=row.getCell(1); // 组织信息，组织名称
                                Cell c3=row.getCell(2); // 组织信息，组织code
                                Cell c4=row.getCell(3); //组织信息，组织类型
                                Cell c5=row.getCell(4); //组织信息，上级组织信息
                                if(null==c1||Utils.isEmptyString(c1)||null==c4||Utils.isEmptyString(c4)){//判断不能为空
                                    if(tipB==0){
                                        if(tipA>0){
                                            errorBack=new StringBuffer(errorBack.substring(0,errorBack.length()-1)).append("行").append(",");
                                        }
                                        errorBack.append("sheet").append(h+1).append("中,第").append(i+1).append(",");
                                    }else{
                                        errorBack.append(i+1).append(",");
                                    }
                                    tipA++;
                                    tipB++;
                                    continue;
                                }else{
                                    ExcelDTO  dto = new ExcelDTO();
                                    dto.setCol1(getValueByCell(c1));
                                    dto.setCol2(getValueByCell(c2));
                                    dto.setCol3(getValueByCell(c3));
                                    dto.setCol4(getValueByCell(c4));
                                    dto.setCol5(getValueByCell(c5));
                                    objList.add(dto);
                                }
                            }else{
                                /* 人员信息*/
                                Cell c10=row.getCell(0); //人员信息，用户部门ID
                                Cell c1=row.getCell(1); // 人员信息，人员姓名
                                Cell c2=row.getCell(2); // 人员信息，登录名
                                Cell c4=row.getCell(3); //人员信息，用户ID
                                Cell c5=row.getCell(4); //人员信息，用户密码
                                Cell c6=row.getCell(5); // 人员信息，手机号码
                                Cell c8=row.getCell(6); // 人员信息，用户邮箱
                                Cell c7=row.getCell(7); // 人员信息，电话号码
                                Cell c3=row.getCell(8); // 人员信息，性别
                                Cell c11=row.getCell(9);//来着系统
                                if(c2==null&&c10==null){
                                    break;
                                }
                                if((null!=c1&&Utils.isEmptyString(c1))||(null!=c2&&Utils.isEmptyString(c2))){//判断不能为空
                                    if(tipB==0){
                                        if(tipA>0){
                                            errorBack=new StringBuffer(errorBack.substring(0,errorBack.length()-1)).append("行").append(",");
                                        }
                                        errorBack.append("sheet").append(h+1).append("中,第").append(i+1).append(",");
                                    }else{
                                        errorBack.append(i+1).append(",");
                                    }
                                    tipA++;
                                    tipB++;
                                    continue;
                                }else{
                                    if(loginMap.containsKey(getValueByCell(c2))){
                                        if(tipD==0){
                                            if(tipC>1){
                                                errorBack=new StringBuffer(errorBack.substring(0,errorBack.length()-1)).append("行").append(",");
                                            }else if(tipC==0){
                                                errorBack.append("sheet").append(h+1).append("中,第").append(i+1);
                                            }else if(tipC==1){
                                                errorBack.append(",").append("行").append(",");
                                            }
                                        }else{
                                            errorBack.append(i+1).append(",");
                                        }
                                        tipC++;
                                        tipD++;
                                        break;
                                    }else{
                                        loginMap.put(getValueByCell(c2),c2);
                                    }
                                    ImportEmpDTO employee=new ImportEmpDTO();
                                    employee.setUserName(getValueByCell(c1));
                                    employee.setLoginName(getValueByCell(c2));
                                    employee.setSex(getValueByCell(c3));
                                    employee.setUserId(getValueByCell(c4));
                                    employee.setSignature(getValueByCell(c5));
                                    employee.setPhone(getValueByCell(c6));
                                    employee.setTelephone(getValueByCell(c7));
                                    employee.setEmail(getValueByCell(c8));
                                    employee.setUserType("employee");
                                    employee.setUserSystem(getValueByCell(c11));
                                    employee.setParentId(getValueByCell(c10));
                                    objTList.add(employee);
                                }
                            }
                        }
                    }
                }//读取sheet文件
                if(tipA>0){
                    errorBack.append("行").append("_isEmptyError");
                }
                if(tipC>0){
                    errorBack_two.append("行").append("@isValidatorError");
                    //errorBack.append("@").append(errorBack_two);
                    errorBack.append(errorBack_two);
                }
                if("7".equals(tip)||"9".equals(tip)){
                    map.put("objTList",objTList);
                }
                map.put("objList",objList);
                map.put("errorBack",errorBack.toString());
            }
        }catch(Exception e){
            log.error("导入信息出错，信息{}",e);
        }
        return map;
    }
    /**
     * @Description: (获取传入参数实际值)
     * @param: @param cc 待传入参数
     * @return: String    retuStr
     */
    private String getValueByCell(Cell cc){
        String retuStr="";
        if(null!=cc){
            try{
               /*CellType 类型 值
                  CELL_TYPE_NUMERIC 数值型 0
				  CELL_TYPE_STRING 字符串型 1
				  CELL_TYPE_FORMULA 公式型 2
				  CELL_TYPE_BLANK 空值 3
				  CELL_TYPE_BOOLEAN 布尔型 4
				  CELL_TYPE_ERROR 错误 5*/
                int cellType=cc.getCellType();
                if(0==cellType){
                   /* Date excelFromDate=HSSFDateUtil.getJavaDate(cc.getNumericCellValue());
                    retuStr=new DateTime(excelFromDate).toString("yyyy-MM-dd HH:mm");*/
                    retuStr=String.valueOf((long)cc.getNumericCellValue());
                }else if(1==cellType){
                    retuStr=cc.getStringCellValue();
                }else if(2==cellType){
                    retuStr=cc.getCachedFormulaResultType()+"";
                }else if(3==cellType){
                    retuStr=cc.getStringCellValue();
                }else if(4==cellType){
                    retuStr=cc.getBooleanCellValue()+"";
                }else if(5==cellType){
                    retuStr=cc.getStringCellValue();
                }
            }catch(Exception e){
                retuStr=cc.getStringCellValue();
            }
            if(!com.google.common.base.Strings.isNullOrEmpty(retuStr)){
                Pattern p=Pattern.compile("\\s*|\t|\r|\n");
                Matcher m=p.matcher(retuStr.trim());
                retuStr=m.replaceAll("");
            }
        }
        return retuStr;
    }
    /**
     * 通过响应输出流实现文件下载
     *
     * @param response     响应的请求
     * @param fileLocal    文件的绝对路径 请用/斜杠表示路径
     * @param downloadName 自定义的文件名 ( 不要后缀),如果此值为空则使用时间日期做为默认的文件名
     * @param deleFile     下载完成后是否删除文件（true: 删除 , false：不删除）
     */
    public void downLoadFile(HttpServletResponse response,String fileLocal,String downloadName,boolean deleFile){
        InputStream in=null;
        OutputStream out=null;
        try{
            if(!"".equals(downloadName)){
                downloadName=downloadName+fileLocal.substring(fileLocal.lastIndexOf("."));
            }else{
                downloadName=fileLocal.substring(fileLocal.lastIndexOf("/")+1);
            }
            response.setHeader("content-disposition","attachment;filename="+URLEncoder.encode(downloadName,"UTF-8"));
            in=new FileInputStream(fileLocal);
            int len=0;
            byte buffer[]=new byte[1024];
            out=response.getOutputStream();
            while((len=in.read(buffer))>0){
                out.write(buffer,0,len);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(in!=null){
                try{
                    //
                    in.close();
                    if(deleFile){
                        Thread.sleep(1000l);
                        File file=new File(fileLocal);
                        file.delete();
                    }
                }catch(Exception e){
                }
            }
        }
    }
    /**
     * excel 2003版本的导出方法 支持多个sheet导出 导出的文件后缀为.xls
     *
     * @param dataMap       要导出的数据
     * @param excelFilePath excel文件的存放位置
     * @param fileName      excel文件名字
     *
     * @return
     */
    public String exportXlsExcel(Map<String,List<String[]>> dataMap,String excelFilePath,String fileName){
        FileOutputStream fout=null;
        String fileLocal="";
        try{
            File file=new File(excelFilePath);
            if(!file.exists()){
                file.mkdirs();
            }
            // 第一步，创建一个webbook，对应一个Excel文件
            HSSFWorkbook wb=new HSSFWorkbook();
            // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
            HSSFSheet sheet=null;
            List<String[]> dataList=null;
            HSSFCellStyle style=wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            Set<String> keyTitle=dataMap.keySet();
            for(String title : keyTitle){
                sheet=wb.createSheet(title);
                dataList=dataMap.get(title);
                for(int i=0;null!=dataList&&i<dataList.size();i++){
                    // 生成第一行
                    HSSFRow row=sheet.createRow(i);
                    String[] arr=dataList.get(i);
                    for(int j=0;null!=arr&&j<arr.length;j++){
                        // 给这一行的第一列赋值
                        HSSFCell cell=row.createCell(j);
                        cell.setCellValue(arr[j]);
                        cell.setCellStyle(style);
                        if(i==0){
                            HSSFCellStyle tempStyle=style;
                            tempStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                            tempStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
                            cell.setCellStyle(tempStyle);
                        }
                    }
                }
            }
            // 第六步，将文件存到指定位置
            fileLocal=excelFilePath+"/"+fileName+".xls";
            fout=new FileOutputStream(fileLocal);
            wb.write(fout);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                fout.close();
            }catch(IOException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return fileLocal;
    }
}