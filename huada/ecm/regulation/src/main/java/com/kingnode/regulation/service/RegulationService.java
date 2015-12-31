package com.kingnode.regulation.service;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.collect.Lists;
import com.kingnode.regulation.bean.KnFile;
import com.kingnode.regulation.dao.KnClassificationDao;
import com.kingnode.regulation.dao.KnRegulationFileDao;
import com.kingnode.regulation.dto.ReturnListDTO;
import com.kingnode.regulation.entity.KnClassification;
import com.kingnode.regulation.entity.KnRegulationFile;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.util.Utils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 *         办公制度service
 */
@Component @Transactional(readOnly=false)
public class RegulationService{
    private static Logger logger=LoggerFactory.getLogger(RegulationService.class);
    private KnRegulationFileDao fileDao;
    private KnClassificationDao classDao;
    public KnRegulationFile ReadKnRegulationFile(Long id){
        return fileDao.findOne(id);
    }
    /**
     * 保存上传的文件
     *
     * @param file 上传的文件
     *
     * @return KnFile 包含上传文件属性对象
     */
    public KnFile UploadFile(MultipartFile file){
        if(file!=null&&file.getSize()>0){
            KnFile fileObject=new KnFile();
            WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
            String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);//扩展名
            String uuid=UUID.randomUUID().toString();
            String fileName=KnRegulationFile.getUploadPath()+uuid+"."+fileExt;
            File localFile=new File(webApplicationContext.getServletContext().getRealPath(fileName));
            if(!localFile.getParentFile().exists()){
                localFile.getParentFile().mkdirs();
            }
            try{
                file.transferTo(localFile);
                fileObject.setExt(fileExt);
                fileObject.setOriginalFileName(file.getOriginalFilename());
                fileObject.setSavePath(fileName);
                fileObject.setFileSize(file.getSize());
                fileObject.setFileFormat(fileExt.toUpperCase());
                return fileObject;
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        //如果为null则表示没有上传成功
        return null;
    }
    /**
     * 保存资料
     *
     * @param file 资料
     *
     * @return
     */
    public KnRegulationFile Save(KnRegulationFile file){
        return fileDao.save(file);
    }
    /**
     * 删除资料
     *
     * @param ids 被删除的id
     */
    public void Delete(List<Long> ids){
        for(Long id : ids){
            KnRegulationFile file=ReadKnRegulationFile(id);
            String dname=file.getClassification();
            fileDao.delete(id);
            List<KnRegulationFile> files=fileDao.findByClassification(dname);
            if(files.size()==0){
                KnClassification classifi=findByClassification(dname);
                if(classifi!=null){
                    classDao.delete(classifi);
                }
            }
        }
    }
    /**
     * 资料分页列表
     *
     * @param searchParams 搜索参数
     * @param dt           table 参数,封住了KnRegulationFile属性
     * @param sort         排序参数
     *
     * @return
     */
    public DataTable<KnRegulationFile> PageListFile(final Map<String,Object> searchParams,DataTable<KnRegulationFile> dt,Sort sort){
        PageRequest pageRequest=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),sort);
        Page<KnRegulationFile> page=this.fileDao.findAll(new Specification<KnRegulationFile>(){
            @Override
            public Predicate toPredicate(Root<KnRegulationFile> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(searchParams.size()!=0){
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_classification"))){
                        expressions.add(criteriaBuilder.like(knResourceRoot.<String>get("classification"),"%"+searchParams.get("LIKE_classification").toString()+"%"));
                    }
                    if(!Utils.isEmptyStr(searchParams.get("LIKE_uploadTime"))){
                        String timeStr=searchParams.get("LIKE_uploadTime").toString();
                        Long time1=DateTime.parse(timeStr,DateTimeFormat.forPattern("yyyy-MM-dd")).getMillis();
                        Long time2=time1+(24*60*60);
                        expressions.add(criteriaBuilder.between(knResourceRoot.<Long>get("uploadTime"),time1,time2));
                    }
                }
                //                if(typeList!=null){
                //                    expressions.add(knResourceRoot.<KnRegulationFile.FileFormat>get("fileFormat").in(typeList));
                //                }
                return predicate;
            }
        },pageRequest);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setiTotalRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }
    public void SaveClassification(KnClassification classification){
        classDao.save(classification);
    }
    public KnClassification findByClassification(String classification){
        return this.classDao.findByClassification(classification);
    }
    /** =============reset接口开始========== */
    public ReturnListDTO<KnRegulationFile> PageListFile(Integer pageNo,Integer pageSize,final Long id,String name){
        KnClassification classification=this.classDao.findOne(id);
        if(classification==null){
            return new ReturnListDTO<>();
        }
        String classificationName=classification.getClassification();
        return PageListFile(pageNo,pageSize,classificationName,name);
    }
    public ReturnListDTO<KnRegulationFile> PageListFile(Integer pageNo,Integer pageSize,final String name){
        return PageListFile(pageNo,pageSize,"",name);
    }

    /**
     * 列出文件列表,接口调用
     *
     * @param pageNo         分页页码
     * @param pageSize       分页数量大小
     * @param classification
     *
     * @return
     */
    public ReturnListDTO<KnRegulationFile> PageListFile(Integer pageNo,Integer pageSize,final String classification,final String name){
        ReturnListDTO<KnRegulationFile> dto=new ReturnListDTO();
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.DESC,"uploadTime"));
        Page<KnRegulationFile> page=this.fileDao.findAll(new Specification<KnRegulationFile>(){
            @Override
            public Predicate toPredicate(Root<KnRegulationFile> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                List<Predicate> predicates=Lists.newArrayList();
                if(!Utils.isEmptyStr(classification)){
                    predicates.add(cb.equal(root.<String>get("classification"),classification));
                }
                if(!Utils.isEmptyStr(name)){
                    Predicate p=cb.or(cb.like(root.<String>get("originalFileName"),"%"+name+"%"),cb.like(root.<String>get("profileName"),"%"+name+"%"));
                    predicates.add(cb.and(p));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageRequest);
        dto.setList(page.getContent());
        dto.setTotalSize(page.getTotalElements());
        dto.setStatus(true);
        return dto;
    }
    /**
     * 根据分类名称查找分类列表
     *
     * @param pageNo         分页页码
     * @param pageSize       分页数量大小
     * @param classification 分类名称
     *
     * @return
     */
    public ReturnListDTO<KnClassification> PageListClassification(Integer pageNo,Integer pageSize,final String classification){
        ReturnListDTO<KnClassification> dto=new ReturnListDTO();
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,null);
        Page<KnClassification> page=this.classDao.findAll(new Specification<KnClassification>(){
            @Override
            public Predicate toPredicate(Root<KnClassification> knResourceRoot,CriteriaQuery<?> criteriaQuery,CriteriaBuilder criteriaBuilder){
                Predicate predicate=criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(!Utils.isEmptyStr(classification)){
                    expressions.add(criteriaBuilder.equal(knResourceRoot.<String>get("classification"),"%"+classification+"%"));
                }
                return predicate;
            }
        },pageRequest);
        dto.setList(page.getContent());
        dto.setTotalSize(page.getTotalElements());
        dto.setStatus(true);
        return dto;
    }
    /**
     * 增加修改浏览次数接口
     *
     * @param id
     *
     * @return
     */
    public DetailDTO<KnRegulationFile> AddReadCount(Long id){
        DetailDTO dto=new DetailDTO();
        KnRegulationFile file=ReadKnRegulationFile(id);
        Long count=file.getReadCount();
        if(count==null){
            count=0L;
        }
        count=count+1;
        file.setReadCount(count);
        file=Save(file);
        dto.setDetail(file);
        dto.setStatus(true);
        return dto;
    }
    /** =============reset接口结束========== */
    @Autowired
    public void setFileDao(KnRegulationFileDao fileDao){
        this.fileDao=fileDao;
    }
    @Autowired
    public void setClassDao(KnClassificationDao classDao){
        this.classDao=classDao;
    }
}
