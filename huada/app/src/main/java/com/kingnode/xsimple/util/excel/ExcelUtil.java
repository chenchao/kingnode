package com.kingnode.xsimple.util.excel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kingnode.xsimple.Setting;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
/**
 * 项目名称：knd_util_web
 * 类名称：ExcelUtil
 * 类描述：   专门用于Excel表格的工具类
 * 创建人：Thinkpad
 * 创建时间：2014-6-27 下午2:47:10
 * 修改人：Thinkpad
 * 修改时间：2014-6-27 下午2:47:10
 * 修改备注：
 */
public class ExcelUtil{
    private static ExcelUtil instance=null;
    private ExcelUtil(){
    }
    public static ExcelUtil getInstance(){
        if(instance==null){
            synchronized(ExcelUtil.class){
                if(instance==null){
                    instance=new ExcelUtil();
                }
            }
        }
        return instance;
    }
    /**
     * @param dataMap       要导出的数据
     * @param excelFilePath excel文件的存放位置
     *
     * @return 文件的存放路径
     *
     * @description excel 2007版本的导出方法 支持多个sheet导出 导出的文件后缀为.xlsx
     * @author：cici
     * @updateTime：2014-6-27 下午3:10:06
     */
    public String exportXlsxExcel(Map<String,List<String[]>> dataMap,String excelFilePath){
        String fileDir=excelFilePath+Setting.BASEADDRESS+Setting.excelAddress;
        String fileLocal=fileDir+"/"+DateTime.now().toString("yyyyMMddHHmmss")+".xlsx";
        // 输出流
        OutputStream os=null;
        try{
            File file=new File(fileDir);
            if(!file.exists()){
                file.mkdir();
            }
            os=new FileOutputStream(fileLocal);
            // 工作区
            XSSFWorkbook wb=new XSSFWorkbook();
            // 创建第一个sheet
            XSSFSheet sheet=null;
            List<String[]> dataList=null;
            Set<String> keyTitle=dataMap.keySet();
            for(String title : keyTitle){
                sheet=wb.createSheet(title);
                dataList=dataMap.get(title);
                for(int i=0;null!=dataList&&i<dataList.size();i++){
                    // 生成第一行
                    XSSFRow row=sheet.createRow(i);
                    String[] arr=dataList.get(i);
                    for(int j=0;null!=arr&&j<arr.length;j++){
                        // 给这一行的第一列赋值
                        XSSFCell cell=row.createCell(j);
                        cell.setCellValue(arr[j]);
                        if(i==0){
                            CellStyle style=wb.createCellStyle();
                            style.setAlignment(CellStyle.ALIGN_CENTER);
                            style.setFillBackgroundColor(HSSFColor.DARK_RED.index);
                            cell.setCellStyle(style);
                        }
                    }
                }
            }
            // 写文件
            wb.write(os);
        }catch(Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try{
                // 关闭输出流
                if(os!=null){
                    os.close();
                }
            }catch(IOException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return fileLocal;
    }
    /**
     * @param dataMap       要导出的数据
     * @param excelFilePath excel文件的存放位置
     *
     * @return 文件存放的路径
     *
     * @description excel 2003版本的导出方法 支持多个sheet导出 导出的文件后缀为.xls
     * @author：cici
     * @updateTime：2014-6-27 下午3:11:48
     */
    public String exportXlsExcel(Map<String,List<String[]>> dataMap,String excelFilePath){
        FileOutputStream fout=null;
        String fileLocal="";
        String fileDir=excelFilePath+Setting.BASEADDRESS+Setting.excelAddress;
        try{
            File file=new File(fileDir);
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
            fileLocal=fileDir+"/"+DateTime.now().toString("yyyyMMddHHmmss")+".xls";
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
