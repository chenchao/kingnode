package com.kingnode.xsimple.service.safety;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import com.kingnode.xsimple.dao.application.KnApplicationInfoDao;
import com.kingnode.xsimple.dao.push.KnCertificateDao;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.push.KnCertificateInfo;
import com.kingnode.xsimple.util.PathUtil;
import com.kingnode.xsimple.util.file.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author dengfeng@kingnode.com (dengfeng)
 */
@Component @Transactional(readOnly=true)
public class CertificateService{

    private KnCertificateDao kcerDao ;

    private KnApplicationInfoDao kappDao ;

    @Autowired
    public void setKcerDao(KnCertificateDao kcerDao){
        this.kcerDao=kcerDao;
    }

    @Autowired
    public void setKappDao(KnApplicationInfoDao kappDao){
        this.kappDao=kappDao;
    }

    /**
     *
     * @param platformType  平台类型
     * @param certificatePwd  证书密码,不加密
     * @param workStatus 证书状态,目前就可用以及不可用
     * @param title 应用标题
     * @param dt
     * @return
     */
    public DataTable<KnCertificateInfo> SearchList(final String platformType,final String certificatePwd,final String workStatus
            ,final String title ,DataTable<KnCertificateInfo> dt){
        PageRequest pageable=new PageRequest(dt.pageNo(),dt.getiDisplayLength(),getSort(dt));
        Page<KnCertificateInfo> page=kcerDao.findAll(new Specification<KnCertificateInfo>(){
            @Override
            public Predicate toPredicate(Root<KnCertificateInfo> root,CriteriaQuery<?> cq,CriteriaBuilder cb){
                Root<KnApplicationInfo> r=cq.from(KnApplicationInfo.class);
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<KnApplicationInfo>get("applicationInfo") ,r.<Long>get("id")));

                if(!Strings.isNullOrEmpty(platformType)){
                    expressions.add(cb.like(root.<String>get("platformType"),"%"+platformType+"%"));
                }
                if(!Strings.isNullOrEmpty(certificatePwd)){
                    expressions.add(cb.equal(root.<String>get("certificatePwd"), certificatePwd));
                }
                if(!Strings.isNullOrEmpty(workStatus)){
                    expressions.add(cb.equal(root.<Setting.DeleteStatusType>get("workStatus") , Setting.WorkStatusType.valueOf(workStatus) ));
                }

                if(!Strings.isNullOrEmpty(title)){
                    expressions.add(cb.like(r.<String>get("title"),"%"+title+"%"));
                }
                return predicate;
            }
        },pageable);
        dt.setiTotalDisplayRecords(page.getTotalElements());
        dt.setAaData(page.getContent());
        return dt;
    }

    @Transactional(readOnly=false)
    public void Delete(List<Long> ids)throws  Exception{
        List<KnCertificateInfo> list=(List<KnCertificateInfo>)kcerDao.findAll(ids);
        for(KnCertificateInfo knCertificateInfo : list){
            String path=knCertificateInfo.getCertificatePath();
            if(!Strings.isNullOrEmpty(path)){
                FileUtil.getInstance().deleteFile(PathUtil.getRootPath()+path);
            }
        }
        kcerDao.delete(list);
    }

    public KnCertificateInfo ReadCertificateInfo(Long id){
        return kcerDao.findOne(id) ;
    }

    public List<KnApplicationInfo> GetApplicationList(Long apid){
        List<KnApplicationInfo> list = new ArrayList<>() ;
        if(null!=apid){
            KnApplicationInfo applicationInfo = kappDao.findOne( apid ) ;
            list.add( applicationInfo );
        } else{
            list=(List<KnApplicationInfo>)kappDao.findAll();
        }
        return list ;
    }

    @Transactional(readOnly=false)
    public void Save(String platformType,String certificatePath,String certificatePwd,String packageName,String isSupport,String remark,String workStatus,String apid ){

        KnApplicationInfo applicationInfo = kappDao.findOne( Long.parseLong(apid) ) ;
        KnCertificateInfo knCertificateInfo = new KnCertificateInfo( platformType, certificatePath, certificatePwd, packageName, isSupport, remark,  Setting.WorkStatusType.valueOf(workStatus), applicationInfo) ;

        kcerDao.save(knCertificateInfo) ;
    }

    @Transactional(readOnly=false)
    public void Update(KnCertificateInfo knCertificateInfo ,String platformType,String certificatePath,String certificatePwd,String packageName,String isSupport,String remark,String workStatus,String apid ){

        if( !Strings.isNullOrEmpty(certificatePath) ){
            knCertificateInfo.setCertificatePath( certificatePath );
        }
        knCertificateInfo.setPlatformType(platformType);
        knCertificateInfo.setCertificatePwd( certificatePwd );
        knCertificateInfo.setPackageName( packageName );
        knCertificateInfo.setIsSupport( isSupport);
        knCertificateInfo.setWorkStatus( Setting.WorkStatusType.valueOf(workStatus ));
        KnApplicationInfo applicationInfo = kappDao.findOne(Long.parseLong(apid)) ;
        knCertificateInfo.setApplicationInfo( applicationInfo );
        kcerDao.save(knCertificateInfo) ;
    }


    private Sort getSort(DataTable<KnCertificateInfo> dt){
        dt.setiSortCol_0("0");
        dt.setsSortDir_0(Sort.Direction.ASC.toString());
        Sort.Direction d=Sort.Direction.DESC;
        if("asc".equals(dt.getsSortDir_0())){
            d=Sort.Direction.ASC;
        }
        String[] column=new String[]{"id" ,"platformType","certificatePwd","workStatus","applicationInfo.title"};
        return new Sort(d,column[Integer.parseInt(dt.getiSortCol_0())]);
    }
}
