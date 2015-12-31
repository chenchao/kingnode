package com.kingnode.attendance.service;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.client.RestServiceUtil;
import com.kingnode.xsimple.util.excel.DownExcel;
import com.kingnode.xsimple.util.excel.ExcelUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 * 考勤管理service
 */

@Component @Transactional(readOnly=true)
public class AttendanceService{

    private static org.slf4j.Logger logger=LoggerFactory.getLogger(AttendanceService.class);
    @Value("#{commonInfo['organRestUserUrl']}")
    private  String organRestUserUrl;//ReadClientProper.prop.getProperty("organRestUserUrl");// rest访问地址
    /**
     * 分页获取员工考勤信息
     * @param searchParams
     * @param dt
     * @return
     */
    public DataTable PageEmpAttendance(Map<String,Object> searchParams,DataTable dt){
        try {
            Map<String,Object> jsonMap = new HashMap<String,Object>();
            List list = new ArrayList();
            String type = searchParams.get("type")!= null ? searchParams.get("type").toString() : "SEARCH";
            String pageSize = String.valueOf(dt.getiDisplayLength());
            String pageNo = String.valueOf(dt.pageNo());

            int page = "0".equals(pageNo) ? 1 : Integer.valueOf(pageNo) +1;
            String name = searchParams.containsKey("username")?(String)searchParams.get("username"):"" ;
            String startTime = searchParams.containsKey("startTime")?(String)searchParams.get("startTime"):"" ;
            String endTime = searchParams.containsKey("endTime")?(String)searchParams.get("endTime"):"" ;

            //获取结果
            String returnData = getRemWorkdata(name ,startTime,endTime ,page+"" , pageSize+"" ,type) ;
            Map jsonObj = JsonMapper.nonEmptyMapper().fromJson(returnData,Map.class);
            List jsonArr = (List)jsonObj.get("listdata") ;

            int total =(Integer) jsonObj.get("size");//总列表信息数量
            int pageTotal = total ;
            list = jsonArr.subList(0, jsonArr.size()) ;
            dt.setiTotalDisplayRecords(total);
            dt.setAaData(list);

            return dt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前日期
     * @return
     */
    private String currentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * 导出考勤信息
     * @param userName  用户名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param tip 导出类型
     */
    public void exportExcel(String userName,String startTime,String endTime,String tip,String ids,HttpServletResponse response){
        String returnData  = "" ;
        if("1".equals(tip)){
            //选中的记录
            returnData = getRemWorkdata(ids ,"" ,"" ,"" , "" ,"EXCELID") ;
        }else if("2".equals(tip)){
            //获取结果
            returnData = getRemWorkdata(userName ,startTime,endTime ,"1" , "80","SEARCH") ;
        }
        Map jsonObj = JsonMapper.nonEmptyMapper().fromJson(returnData,Map.class);
        List jsonArr = (List)jsonObj.get("listdata") ;

        List<String[]> arrayList = new ArrayList<>() ;
        Map<String ,List<String[]>>  map  = new HashMap<String, List<String[]>>() ;
        arrayList.add( new String[]{"员工名称" ,"用户名","打卡地址" ,"时间" ,"员工编号"} ) ; //列头
        if(!Utils.isEmpityCollection(jsonArr)){
            for(int i = 0 ; i<jsonArr.size() ; i++ ){
                Map obj =(Map) jsonArr.get(i);  // : "用户名",  "mData": "USER_NAME" },
                arrayList.add( new String[]{(String)obj.get("RESOURCE_NAME"),(String)obj.get("USER_NAME"),(String) obj.get("ADDRESS")
                        ,(String)obj.get("PS_DATE") ,(String)obj.get("EMPLID")
                } ) ;
            }
        }
        String excelTitle = "考勤信息" ;
        map.put( excelTitle, arrayList) ;

        String path = AttendanceService.class.getResource("/").getPath();
        DownExcel.getInstall().downLoadFile(response,ExcelUtil.getInstance().exportXlsExcel(map,path),"考勤信息",true) ;
    }

    /**
     *
     * @param name  查询条件的用户名
     * @param startTime  查询条件的开始时间
     * @param endTime  查询条件的结束时间
     * @param page  开始页码
     * @param pagesize  每页多少条记录
     * @param flag
     * @return
     */
    public String  getRemWorkdata(String name ,String startTime ,String endTime ,String page , String pagesize ,String flag ) {
        String returnData = "" ;
        try {
            StringBuffer  strBuff = new StringBuffer("") ;
            if("SEARCH".equals(flag)){
                strBuff.append("{\"service\":\"CRM_INT_PKG.getempregistration\", \"data\":{")
                        .append("\"p_user_name\":\""+name.toUpperCase()+"\",")
                        .append("\"p_date_from\":\""+startTime+"\"")
                        .append(",\"p_date_end\":\""+endTime+"\",")
                        .append("\"p_page\":\""+page+"\"")
                        .append(",\"p_page_cnt\":\""+pagesize+"\"")
                        .append("},\"type\": \"rest\",\"to\":\"XDB\"}") ;

            } else if("NAEMINFO".equals(flag)) {

                strBuff.append("{\"service\":\"CRM_INT_PKG.get_emp_list\", \"data\":{")
                        .append("\"p_user_name\":\""+name.toUpperCase()+"\",")
                        .append("\"p_page\":\""+page+"\"")
                        .append(",\"p_page_cnt\":\""+pagesize+"\"")
                        .append("},\"type\": \"rest\",\"to\":\"XDB\"}") ;

            } else if("EXCELID".equals(flag)) {
                strBuff.append("{\"service\":\"CRM_INT_PKG.export_registration_list\", \"data\":{")
                        .append("\"p_ps_id\":\""+name.toUpperCase()+"\"},\"type\": \"rest\",\"to\":\"XDB\"}") ;
            }

            //							{"service":"CRM_INT_PKG.export_registration_list", "data":{"p_ps_id":"1,2,3,4,5"},"type": "rest","to":"XDB"}
            logger.info("提交参数--->"+strBuff.toString());

            String paras = "data=" + URLEncoder.encode(strBuff.toString(),"UTF-8");
            returnData = RestServiceUtil.getStringResponse(paras,organRestUserUrl,true);
            logger.info( "返回结果."+returnData );
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage());
        }
        return returnData ;
    }
}
