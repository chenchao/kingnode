package com.kingnode.eka.service;
import java.io.File;
import java.io.FileInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.eka.dao.KnEkaBusDao;
import com.kingnode.eka.dao.KnEkaCategoriesDao;
import com.kingnode.eka.dao.KnEkaCommentLogDao;
import com.kingnode.eka.dao.KnEkaDao;
import com.kingnode.eka.dao.KnEkaSmallClassDao;
import com.kingnode.eka.entity.KnEka;
import com.kingnode.eka.entity.KnEkaBus;
import com.kingnode.eka.entity.KnEkaCategories;
import com.kingnode.eka.entity.KnEkaCommentLog;
import com.kingnode.eka.entity.KnEkaSmallClass;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.util.Users;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 *         考勤管理service
 */
@SuppressWarnings("ConstantConditions") @Service @Transactional(readOnly=true)
public class EkaService{
    private static org.slf4j.Logger log=LoggerFactory.getLogger(EkaService.class);

    public static final Long BusId=11l;//

    private KnEkaDao ked;
    private KnEkaCategoriesDao kecd;
    private KnEkaSmallClassDao kescd;
    private KnEkaCommentLogDao kecld;
    @Autowired
    private KnEkaBusDao kebd;

    @Transactional
    public void save(KnEka eka,String[] busTitles,String[] busComments){
        eka=ked.save(eka);
        //保存公交车站  公交路线得信息
        if(eka.getSmall().getId().intValue()==BusId.intValue()){
            this.saveKnEkaBus(eka.getId(),busTitles,busComments);
        }
    }

    /**
     * 保存
     * @param ekaId
     * @param busTitles
     * @param busComments
     */
    public void saveKnEkaBus(Long ekaId,String[] busTitles,String[] busComments){
        //先删除原有得线路问题
        List<KnEkaBus> dblist = this.ListEkaBus(ekaId);
        kebd.delete(dblist);
        if(busTitles!=null && busTitles.length>0){
            List<KnEkaBus> list = Lists.newArrayList();
            for(int i=0;i<busTitles.length;i++){
                if(StringUtils.isNotEmpty(busTitles[i]) || StringUtils.isNotEmpty(busComments[i])){
                    KnEkaBus ekaBus = new KnEkaBus();
                    ekaBus.setEkaId(ekaId);
                    ekaBus.setTitle(busTitles[i]);
                    ekaBus.setComment(busComments[i]);
                    list.add(ekaBus);
                }
            }
            kebd.save(list);
        }
    }

    @Transactional
    public void save(List<KnEka> ekas){
        ked.save(ekas);
    }

    public KnEka FindKnEka(Long id){
        return ked.findOne(id);
    }

    /**
     * 删除壹卡会
     *
     * @param ids
     */
    @Transactional(readOnly=false)
    public void deleteEka(String[] ids){
        if(ids!=null&&ids.length>0){
            for(String id : ids){
                ked.delete(Long.valueOf(id));
            }
        }
    }
    /**
     * 获取壹卡会评论
     * @param
     * @return
     */
    public KnEkaCommentLog ReadKnEkaCommentLog(Long ekaId){
        return kecld.queryEkaCommentLog(Users.id(),ekaId);
    }

    /**
     * 获取所有公交线路信息
     * @param ekaId
     * @return
     */
    public  List<KnEkaBus> ListEkaBus(Long ekaId){
        return kebd.findByEkaId(ekaId);
    }

    /**
     * 点赞或点踩
     * @param ekaId
     * @param commentType
     */
    @Transactional(readOnly=false)
    public boolean ConfirmCommentLog(Long ekaId,String commentType){
        //点赞或是点踩 先删除 再保存 如果是同一个用户  只能赞一次
        KnEkaCommentLog knlog = kecld.queryEkaCommentLog(Users.id(),ekaId);
        if(knlog != null && knlog.getId() != null && knlog.getId() > 0){
            kecld.delete(knlog);
        }
        KnEkaCommentLog ekaCommentLog = new KnEkaCommentLog();
        ekaCommentLog.setEkaId(ekaId);
        ekaCommentLog.setUserId(Users.id());
        ekaCommentLog.setCommentType(KnEkaCommentLog.CommentType.valueOf(commentType));
        kecld.save(ekaCommentLog);

        //修改点赞或踩数量
        KnEka eka = ked.findOne(ekaId);
        if(commentType.equals(KnEkaCommentLog.CommentType.PRAISE.toString())){
            eka.setPraiseNums(eka.getPraiseNums()+1);
        }else if(commentType.equals(KnEkaCommentLog.CommentType.TREAD.toString())){
            eka.setTreadNums(eka.getTreadNums()+1);
        }
        ked.save(eka);
        return true;
    }

    /**
     * 取消赞或踩
     * @param ekaId
     * @param commentType
     */
    @Transactional(readOnly=false)
    public boolean CancelCommentLog(Long ekaId,String commentType){
        //点赞或是点踩 先删除 再保存
        KnEkaCommentLog knlog = kecld.queryEkaCommentLog(Users.id(),ekaId);
        if(knlog != null && knlog.getId() != null && knlog.getId() > 0){
            kecld.delete(knlog);
        }

        //修改点赞或踩数量
        KnEka eka = ked.findOne(ekaId);
        if(commentType.equals(KnEkaCommentLog.CommentType.PRAISE.toString())){
            eka.setPraiseNums(eka.getPraiseNums()-1>=0?eka.getPraiseNums()-1:0);
        }else if(commentType.equals(KnEkaCommentLog.CommentType.TREAD.toString())){
            eka.setTreadNums(eka.getTreadNums()-1>=0?eka.getTreadNums()-1:0);
        }
        ked.save(eka);
        return true;
    }



    /**
     * 获取所有大类型
     * @return
     */
    public List<KnEkaCategories> FindAllCategories(){
        Iterable<KnEkaCategories> ries = kecd.findAll(new Sort(Sort.Direction.ASC,"seq"));
        List<KnEkaCategories> list = Lists.newArrayList();
        if( ries != null && ries.iterator().hasNext() ){
            Iterator<KnEkaCategories> iterator = ries.iterator();
            while(iterator.hasNext()){
                list.add(iterator.next());
            }
        }
        return list;
    }
    /**
     * 获取小类型名称
     * @param smallClasses
     * @return
     */
    public String FindSmallNames(Set<KnEkaSmallClass> smallClasses){
        String result = "";
        if( smallClasses != null && smallClasses.size() > 0 ){
            for(KnEkaSmallClass smallClass : smallClasses){
                if( smallClass.getName() != null && !"".equals(smallClass.getName()) ){
                    result += smallClass.getName()+",";
                }
            }
            if(result.length()>0){
                result = result.substring(0,result.length()-1);
            }
        }
        return result;
    }

    /**
     * 查询壹卡会信息,用于外部接口
     *
     * @param
     * @param
     *
     * @return
     */
    public Page<KnEka> PageKnEka(final Integer pageNo,final Integer pageSize,final Long cateId,final Long smalId,final String info){
        List<Sort.Order> ords = Lists.newArrayList();
        if(smalId>0 && smalId==23l){
            ords.add(new Sort.Order(Sort.Direction.DESC,"star"));
        }else{
            ords.add(new Sort.Order(Sort.Direction.ASC,"distance"));
        }

        //ords.add(new Sort.Order(Sort.Direction.DESC,"createTime"));
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(ords));

        Specification<KnEka> spec=new Specification<KnEka>(){
            @Override
            public Predicate toPredicate(Root<KnEka> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                if(cateId!=null && cateId.intValue()>0){
                    predicates.add(cb.equal(root.<KnEkaCategories>get("categories").<Long>get("id"),cateId));
                }
                if(smalId!=null && smalId.intValue()>0){
                    predicates.add(cb.equal(root.<KnEkaCategories>get("small").<Long>get("id"),smalId));
                }
                if(!Strings.isNullOrEmpty(info)){
                    predicates.add(cb.like(root.<String>get("name"),"%"+info+"%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnEka> page=ked.findAll(spec,pageRequest);
        return page;
    }

    /**
     * 查询壹卡会信息
     *
     * @param params
     * @param dt
     *
     * @return
     */
    public DataTable<KnEka> PageKnEka(Map<String,Object> params,DataTable<KnEka> dt){
        final String catetype=params.get("LIKE_catetype")!=null&&!"".equals(params.get("LIKE_catetype").toString())?params.get("LIKE_catetype").toString():"";
        final String name=params.get("LIKE_name")!=null&&!"".equals(params.get("LIKE_name").toString())?"%"+params.get("LIKE_name").toString()+"%":"%%";
        final String smaltype=params.get("LIKE_smaltype")!=null&&!"".equals(params.get("LIKE_smaltype").toString())?params.get("LIKE_smaltype").toString():"";
        List<Sort.Order> ords = Lists.newArrayList();
        //ords.add(new Sort.Order(Sort.Direction.ASC,"distance"));
        ords.add(new Sort.Order(Sort.Direction.DESC,"createTime"));
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),new Sort(ords));
        Specification<KnEka> spec=new Specification<KnEka>(){
            @Override
            public Predicate toPredicate(Root<KnEka> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                if(!Strings.isNullOrEmpty(catetype)){
                    predicates.add(cb.equal(root.<KnEkaCategories>get("categories").<Long>get("id"),Long.valueOf(catetype)));
                }
                if(!Strings.isNullOrEmpty(name)){
                    predicates.add(cb.like(root.<String>get("name"),name));
                }
                if(!Strings.isNullOrEmpty(smaltype)){
                    predicates.add(cb.equal(root.<KnEkaSmallClass>get("small").<Long>get("id"),Long.valueOf(smaltype)));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<KnEka> page=ked.findAll(spec,pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    /**
     * 导入excel 中的壹卡会信息
     * @param target
     * @param uploadFileName
     * @return
     */
    public Map<String,Object> importEka(File target,String uploadFileName){
        Map<String,Object> wbMap = FindWb(target,uploadFileName);
        if(wbMap.get("wb") != null){
            Workbook wb=(Workbook)wbMap.get("wb");
            Map<String,Object> dataMap = FindDataList(wb);
            if(dataMap.get("list")!=null){
                List<KnEka> ekas = (List<KnEka>)dataMap.get("list");
                //保存数据
                this.save(ekas);
                dataMap.clear();
                dataMap.put("success",true);
            }
            return dataMap;
        }
        return wbMap;
    }

    /**
     * 获取Workbook对象
     * @param target
     * @param uploadFileFileName
     * @return
     */
    public Map<String,Object> FindWb(File target,String uploadFileFileName){
        Map<String,Object> map=new HashMap<String,Object>();
        try{
            Workbook wb=null;
            if(uploadFileFileName.endsWith("xlsx")){
                try{
                    wb=new XSSFWorkbook(new FileInputStream(target));// 操作Excel2007的版本，扩展名是.xlsx
                }catch(Exception e){
                    map.put("errorBack","请上传xlsx或xls文件。");
                    log.info("分析Excel，出错 ---->",e);
                }
            }else if(uploadFileFileName.endsWith("xls")){
                try{
                    wb=new HSSFWorkbook(new FileInputStream(target));// 操作Excel2003以前（包括2003）的版本，扩展名是.xls
                }catch(Exception e){
                    map.put("errorBack","请上传xlsx或xls文件。");
                    log.info("分析Excel，出错 ---->",e);
                }
            }else{
                map.put("errorBack","请上传xlsx或xls文件。");
            }
            if(wb!=null){
                map.put("wb",wb);
                //map=getImportListInfo(wb,tip);
            }
        }catch(Exception e){
            log.info("分析Excel，组装显示在页面中 , 出错",e);
        }
        return map;
    }
    /**
     * 从excel表中获取数据
     * @param wb
     * @return
     */
    private Map<String,Object> FindDataList(Workbook wb){
        Map<String,Object> map = new HashMap<String,Object>();
        List<KnEka> list = Lists.newArrayList();
        //直接获取第一个sheet
        Sheet sh=wb.getSheetAt(0);
        int rowNum=sh.getLastRowNum()+1;
        if(rowNum==1){//没有数据
            map.put("errorBack","没有数据。");
        }
        try{
            for(int i=1;i<rowNum;i++){
                Row row=sh.getRow(i);
                KnEka eka=getKnEkaByRow(row);
                list.add(eka);
            }
            map.put("list",list);
        }catch(Exception e){
            map.put("errorBack",e.getMessage());
            e.printStackTrace();
        }
        return map;
    }
    /**
     * 组装对象
     * @param row
     * @return
     */
    private KnEka getKnEkaByRow(Row row){
        Cell c1=row.getCell(0); //一级类别
        Cell c2=row.getCell(1); //二级类别
        Cell c3=row.getCell(2); //图片
        Cell c4=row.getCell(3); //名称
        Cell c5=row.getCell(4); //地址
        Cell c6=row.getCell(5); //电话
        Cell c7=row.getCell(6); //距离
        Cell c8=row.getCell(7); //说明
        Cell c9=row.getCell(8); //经度
        Cell c10=row.getCell(9); //纬度
        KnEka eka = new KnEka();
        dealTypes(this.getValueByCell(c1),this.getValueByCell(c2),eka,row);
        eka.setName(this.getValueByCell(c4));
        eka.setAddress(this.getValueByCell(c5));
        eka.setAcall(this.getValueByCell(c6));
        eka.setDistance(Long.parseLong(this.getValueByCell(c7)));
        eka.setImage(this.getValueByCell(c3));
        eka.setInstructions(this.getValueByCell(c8));
        eka.setLongitude(this.getValueByCell(c9));
        eka.setLatitude(this.getValueByCell(c10));
        return eka;
    }
    /**
     * 处理一二级类别
     * @param categories
     * @param smallName
     * @param eka
     * @param row
     */
    private void dealTypes(String categories,String smallName,KnEka eka,Row row){
        if( categories == null || "".equals(categories.trim()) )
            throw new RuntimeException("第"+(row.getRowNum()+1)+"行［一级类别］为空。");
        if( smallName == null || "".equals(smallName.trim()) )
            throw new RuntimeException("第"+(row.getRowNum()+1)+"行［二级类别］为空。");
        KnEkaCategories cate = kecd.findByName(categories);
        if( cate == null || cate.getId() == null )
            throw new RuntimeException("第"+(row.getRowNum()+1)+"行,找不到［一级类别］为["+categories+"]的数据,请核实后再上传。");
        KnEkaSmallClass small = kescd.findByName(smallName);
        if( small == null || small.getId() == null )
            throw new RuntimeException("第"+(row.getRowNum()+1)+"行,找不到［二级类别］为["+smallName+"]的数据,请核实后再上传。");
        eka.setCategories(cate);
        eka.setSmall(small);
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
     * 获取本机的ip地址
     *
     * @return
     */
    public String FindLocalhostIp(){
        String ipStr="";
        try{
            Enumeration allNetInterfaces=NetworkInterface.getNetworkInterfaces();
            InetAddress ip=null;
            while(allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface=(NetworkInterface)allNetInterfaces.nextElement();
                Enumeration addresses=netInterface.getInetAddresses();
                while(addresses.hasMoreElements()){
                    ip=(InetAddress)addresses.nextElement();
                    if(ip!=null&&ip instanceof Inet4Address&&ip.getHostAddress().toString().indexOf("127")<0){
                        ipStr=ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }
        return ipStr;
    }
    public String FindImgHttpHead(){
        String ip=FindLocalhostIp();
        String http="http:"+File.separator+File.separator+ip+":8080";
        return http;
    }
    @Autowired
    public void setKed(KnEkaDao ked){
        this.ked=ked;
    }
    @Autowired
    public void setKecd(KnEkaCategoriesDao kecd){
        this.kecd=kecd;
    }
    @Autowired
    public void setKescd(KnEkaSmallClassDao kescd){
        this.kescd=kescd;
    }
    @Autowired
    public void setKecld(KnEkaCommentLogDao kecld){
        this.kecld=kecld;
    }
    //    @Autowired
//    public void setRs(RegulationService rs){
//        this.rs=rs;
//    }
}
