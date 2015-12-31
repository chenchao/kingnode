package com.kingnode.eka.rest;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.eka.dto.KnEkaBusDTO;
import com.kingnode.eka.dto.KnEkaCategoriesDTO;
import com.kingnode.eka.dto.KnEkaCommentLogDTO;
import com.kingnode.eka.dto.KnEkaDTO;
import com.kingnode.eka.dto.KnEkaSmallClassDTO;
import com.kingnode.eka.entity.KnEka;
import com.kingnode.eka.entity.KnEkaBus;
import com.kingnode.eka.entity.KnEkaCategories;
import com.kingnode.eka.entity.KnEkaCommentLog;
import com.kingnode.eka.entity.KnEkaSmallClass;
import com.kingnode.eka.service.EkaService;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController
@RequestMapping({"/api/v1/eka","/api/secure/v1/eka"})
public class EkaRestController{
    @Autowired
    private EkaService es;

    /**
     * 获取壹卡会类型
     *
     * 示例：http://localhost:8080/api/v1/eka/categories/list
     *
     * 参数：无
     *
     * 返回：
     * {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "name" : "壹卡会",
     * "id" : 1,
     * "image" : null,
     * "smallNames" : "购物,餐饮美食,生活服务,休闲娱乐,丽人,家装",
     * "smalss" : [ {
     * "name" : "购物",
     * "id" : 1,
     * "image" : null
     * }, {
     * "name" : "餐饮美食",
     * "id" : 2,
     * "image" : null
     * }, {
     * "name" : "生活服务",
     * "id" : 3,
     * "image" : null
     * }, {
     * "name" : "休闲娱乐",
     * "id" : 4,
     * "image" : null
     * }, {
     * "name" : "丽人",
     * "id" : 5,
     * "image" : null
     * }, {
     * "name" : "家装",
     * "id" : 6,
     * "image" : null
     * } ]
     * }, {
     * "name" : "购物",
     * "id" : 2,
     * "image" : null,
     * "smallNames" : "超市,便利店,商场",
     * "smalss" : [ {
     * "name" : "超市",
     * "id" : 7,
     * "image" : null
     * }, {
     * "name" : "便利店",
     * "id" : 8,
     * "image" : null
     * }, {
     * "name" : "商场",
     * "id" : 9,
     * "image" : null
     * } ]
     * }, {
     * "name" : "运动健身",
     * "id" : 3,
     * "image" : null,
     * "smallNames" : "华大运动健身房",
     * "smalss" : [ {
     * "name" : "华大运动健身房",
     * "id" : 10,
     * "image" : null
     * } ]
     * }, {
     * "name" : "生活服务",
     * "id" : 4,
     * "image" : null,
     * "smallNames" : "公交车站,邮局,派出所,银行,打印店,照相馆,医院",
     * "smalss" : [ {
     * "name" : "公交车站",
     * "id" : 11,
     * "image" : null
     * }, {
     * "name" : "邮局",
     * "id" : 12,
     * "image" : null
     * }, {
     * "name" : "派出所",
     * "id" : 13,
     * "image" : null
     * }, {
     * "name" : "银行",
     * "id" : 14,
     * "image" : null
     * }, {
     * "name" : "打印店",
     * "id" : 15,
     * "image" : null
     * }, {
     * "name" : "照相馆",
     * "id" : 16,
     * "image" : null
     * }, {
     * "name" : "医院",
     * "id" : 17,
     * "image" : null
     * } ]
     * } ]
     * }
     */
    @RequestMapping(value="/categories/list", method={RequestMethod.GET})
    public ListDTO<KnEkaCategoriesDTO> List(){
        List<KnEkaCategories> categorieses=es.FindAllCategories();
        List<KnEkaCategoriesDTO> list=Lists.newArrayList();
        if(categorieses!=null&&categorieses.size()>0){
            for(KnEkaCategories categories : categorieses){
                KnEkaCategoriesDTO dto=BeanMapper.map(categories,KnEkaCategoriesDTO.class);
                dto.setSmallNames(es.FindSmallNames(categories.getSmalls()));
                Set<KnEkaSmallClass> smallClasses=categories.getSmalls();
                if(smallClasses!=null&&smallClasses.size()>0){
                    List<KnEkaSmallClassDTO> smallClassDTOs=Lists.newArrayList();
                    for(KnEkaSmallClass small : smallClasses){
                        KnEkaSmallClassDTO smallClassDTO=BeanMapper.map(small,KnEkaSmallClassDTO.class);
                        smallClassDTOs.add(smallClassDTO);
                    }
                    dto.setSmalss(smallClassDTOs);
                }
                list.add(dto);
            }
        }
        return new ListDTO(true,list);
    }

    /**
     * 获取壹卡会列表信息
     *
     * 示例：http://localhost:8080/api/v1/eka/list
     *
     * @param pageNo   页码  默认0
     * @param pageSize 每页条数  默认10
     * @param cateId   壹卡会 大类id 默认0
     * @param smalId   壹卡会 小类id 默认0
     * @param name     壹卡会 标题 默认“”
     *
     *                 返回：
     *                 {
     *                 "status" : true,
     *                 "errorCode" : null,
     *                 "errorMessage" : null,
     *                 "list" : [ {
     *                 "image" : "/uf/51e4642f508247cca6c54e0efacb9e54.png",
     *                 "name" : "32434324",
     *                 "call" : "",
     *                 "address" : "4234324",
     *                 "distance" : "33",
     *                 "instructions" : "432434",
     *                 "longitude" : "42343",
     *                 "latitude" : "423434",
     *                 "id" : 1
     *                 } ]
     *                 }
     */
    @RequestMapping(value="/list", method={RequestMethod.POST})
    public ListDTO<KnEkaDTO> EkaList(@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="10") Integer pageSize,@RequestParam(value="cateId", defaultValue="0") Long cateId,@RequestParam(value="smalId", defaultValue="0") Long smalId,@RequestParam(value="name", defaultValue="") String name){
        Page<KnEka> page=es.PageKnEka(pageNo,pageSize,cateId,smalId,name);
        List<KnEka> list=page.getContent();
        List<KnEkaDTO> dtos=Lists.newArrayList();
        if(list!=null&&list.size()>0){
            for(KnEka eka : list){
                KnEkaDTO dto=BeanMapper.map(eka,KnEkaDTO.class);
                dto.setCall(eka.getAcall());
                KnEkaCommentLogDTO logDTO=new KnEkaCommentLogDTO();
                KnEkaCommentLog log=es.ReadKnEkaCommentLog(eka.getId());
                if(log!=null&&log.getId()!=null){
                    logDTO=BeanMapper.map(log,KnEkaCommentLogDTO.class);
                }
                dto.setCommentLogDTO(logDTO);
                dtos.add(dto);
            }
        }
        return new ListDTO(true,dtos);
    }

    /**
     * 获取壹卡会详情
     *
     * 示例：http://localhost:8080/api/v1/eka/detail?id=1
     *
     * 参数 ：ekaId  壹卡会id
     *
     * 返回：
     * {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "image" : "http://192.168.8.73:8080/uf/51e4642f508247cca6c54e0efacb9e54.png",
     * "name" : "32434324",
     * "call" : "",
     * "address" : "4234324",
     * "distance" : "33",
     * "instructions" : "432434",
     * "longitude" : "42343",
     * "latitude" : "423434",
     * "id" : 1
     * } ]
     * }
     */
    @RequestMapping(value="/detail", method={RequestMethod.GET})
    public ListDTO<KnEkaDTO> EkaDetail(@RequestParam(value="id") Long ekaId){
        KnEka eka=es.FindKnEka(ekaId);
        KnEkaDTO dto=BeanMapper.map(eka,KnEkaDTO.class);
        dto.setCall(eka.getAcall());
        //        dto.setImage(es.FindImgHttpHead()+dto.getImage());
        //如果是公交车站  则增加公交路线信息
        if(eka.getSmall().getId().intValue()==EkaService.BusId.intValue()){
            List<KnEkaBus> buses=es.ListEkaBus(eka.getId());
            List<KnEkaBusDTO> busDTOs=BeanMapper.mapList(buses,KnEkaBusDTO.class);
            dto.setBusDTOs(busDTOs);
        }
        List<KnEkaDTO> dtos=Lists.newArrayList();
        dtos.add(dto);
        return new ListDTO(true,dtos);
    }

    /**
     * 点赞／踩
     *
     * 示例：http://localhost:8080/api/v1/comment/confirm  POST ekaId=1 commentType=PRAISE
     *
     * @return 返回回执成功与否状态，true 成功，false 失败
     * 返回：{"status":true,"errorCode":null,"errorMessage":null,"detail":"true"}
     */
    @RequestMapping(value="/comment/confirm", method={RequestMethod.POST})
    public DetailDTO<String> ConfirmCommentLog(@RequestParam(value="ekaId") Long ekaId,@RequestParam(value="commentType") String commentType){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        boolean flag=false;
        flag=es.ConfirmCommentLog(ekaId,commentType);
        detailDTO.setDetail(flag+"");
        detailDTO.setStatus(flag);
        return detailDTO;
    }

    /**
     * 取消赞／踩
     *
     * 示例：http://localhost:8080/api/v1/comment/cancel  POST ekaId=1 commentType=PRAISE
     *
     * @return 返回回执成功与否状态，true 成功，false 失败
     * 返回：{"status":true,"errorCode":null,"errorMessage":null,"detail":"true"}
     */
    @RequestMapping(value="/comment/cancel", method={RequestMethod.POST})
    public DetailDTO<String> CancelCommentLog(@RequestParam(value="ekaId") Long ekaId,@RequestParam(value="commentType") String commentType){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        boolean flag=false;
        flag=es.CancelCommentLog(ekaId,commentType);
        detailDTO.setDetail(flag+"");
        detailDTO.setStatus(flag);
        return detailDTO;
    }

}
