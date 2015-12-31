package com.kingnode.workflow.rest;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.workflow.dto.KnApplyDTO;
import com.kingnode.workflow.dto.KnDepartmentDTO;
import com.kingnode.workflow.dto.KnDynamicDTO;
import com.kingnode.workflow.dto.KnEmployeeDTO;
import com.kingnode.workflow.dto.KnHistoryApproveDTO;
import com.kingnode.workflow.dto.KnNodeDTO;
import com.kingnode.workflow.dto.KnTemplateDTO;
import com.kingnode.workflow.entity.KnApply;
import com.kingnode.workflow.entity.KnCurrentApprove;
import com.kingnode.workflow.entity.KnHistoryApprove;
import com.kingnode.workflow.entity.KnNode;
import com.kingnode.workflow.entity.KnTemplate;
import com.kingnode.workflow.entity.KnWorkflowAttach;
import com.kingnode.workflow.service.KnWorkflowService;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnOrganization;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import com.kingnode.xsimple.util.Users;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 对应手机app接口
 *
 * @author huanganding
 */
@RestController @RequestMapping({"/api/v1/workflow","/api/secure/v1/workflow"}) public class WorkflowRestController{
    private static org.slf4j.Logger log=LoggerFactory.getLogger(WorkflowRestController.class);
    @Autowired private KnWorkflowService ws;
    /**
     * 添加或修改流程模板
     *
     * @param id    模板ID
     * @param title 模板名称
     * @param nodes 审批节点
     *              <p>
     *              示例：http://192.168.15.18:8080/api/v1/workflow/template-save
     *              post：｛
     *              "title": "请假申请",  //模板标题,必录
     *              "id": "",             //模板ID,为空表示新增,否则修改
     *              "nodes": [            //审批节点
     *              {
     *              "type": "USER",       //类型分为 用户||岗位  USER(用户)\\ DEPARTMENT(岗位) 必录
     *              "approveId ": "123",  //审批人ID：如类型为用户,该值为用户ID,否则为岗位ID 必录
     *              },
     *              ...
     *              ]}
     *              </p>
     *
     * @return {
     * "status":true,
     * "errorCode":null,
     * "errorMessage":null,
     * "detail":"true"
     * }
     */
    @RequestMapping(value="/template-save", method={RequestMethod.POST})
    public DetailDTO<String> saveTemplate(@RequestParam(value="id") Long id,@RequestParam(value="title") String title,@RequestParam(value="nodes") String nodes){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        boolean result=true;
        String errorCode=100+"";
        String errorMessage="创建流程模板失败";
        try{
            KnTemplate template=new KnTemplate();
            if(id!=null&&id>0){
                errorCode=103+"";
                errorMessage="更新流程模板失败";
                template=ws.ReadKnTemplate(id);
            }
            template.setTitle(title);
            List<KnNodeDTO> nodeList=JsonMapper.nonDefaultMapper().fromJson(nodes,new TypeReference<List<KnNodeDTO>>(){
            });
            ws.SaveKnTemplate(template,nodeList);
        }catch(Exception e){
            result=false;
            detailDTO.setErrorCode(errorCode);
            detailDTO.setErrorMessage(errorMessage);
            log.info(errorMessage,e);
        }
        detailDTO.setStatus(result);
        detailDTO.setDetail(result+"");
        return detailDTO;
    }
    /**
     * 获取系统所有用户（包含自己）
     *
     * @param p 页码
     * @param s 条数
     *          <p>
     *          示例：http://192.168.15.18:8080/api/v1/workflow/emp-list    post：｛"p":0,"s":10,"userName":"张三"｝
     *          </p>
     *
     * @return {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "id": "",               //用户ID
     * "empName": "",          //用户姓名
     * "empIcon": ""           //用户头像地址
     * } ,
     * ....
     * ]}
     */
    @RequestMapping(value="/emp-list", method={RequestMethod.POST})
    public ListDTO<KnEmployeeDTO> employeeList(@RequestParam(value="p", defaultValue="0") Integer p,@RequestParam(value="s", defaultValue="10") Integer s,@RequestParam(value="userName", defaultValue="") String userName){
        List<KnEmployeeDTO> dtoList=Lists.newArrayList();
        boolean result=true;
        try{
            List<KnEmployee> list=ws.ListAllEmployee(p,s,userName);
            for(KnEmployee obj : list){
                KnEmployeeDTO dto=new KnEmployeeDTO();
                dto.setId(obj.getId());
                dto.setEmpName(obj.getUserName());
                dto.setEmpIcon(obj.getImageAddress());
                dtoList.add(dto);
            }
        }catch(Exception e){
            result=false;
            log.info("获取系统所有用户（包含自己）失败",e);
        }
        return new ListDTO<>(result,dtoList);
    }
    /**
     * 获取系统所有部门(组织)
     *
     * @param p 页码
     * @param s 条数
     *          <p>
     *          示例：http://192.168.15.18:8080/api/v1/workflow/dept-list    post：｛"p":0,"s":10,"deptName":"张三"｝
     *          </p>
     *
     * @return {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "id": "",               //部门ID
     * "name": "",             //部门名称
     * } ,
     * ....
     * ]}
     */
    @RequestMapping(value="/dept-list", method={RequestMethod.POST})
    public ListDTO<KnDepartmentDTO> departmentList(@RequestParam(value="p", defaultValue="0") Integer p,@RequestParam(value="s", defaultValue="10") Integer s,@RequestParam(value="deptName", defaultValue="") String deptName){
        List<KnDepartmentDTO> dtoList=Lists.newArrayList();
        boolean result=true;
        try{
            List<KnOrganization> list=ws.ListAllDepartment(p,s,deptName);
            for(KnOrganization obj : list){
                KnDepartmentDTO dto=new KnDepartmentDTO();
                dto.setId(obj.getId());
                dto.setName(obj.getName());
                dtoList.add(dto);
            }
        }catch(Exception e){
            result=false;
            log.info("获取系统所有部门(组织)失败",e);
        }
        return new ListDTO<>(result,dtoList);
    }
    /**
     * 删除流程模板
     *
     * @param id 模板ID
     *           <p>
     *           示例：http://192.168.15.18:8080/api/v1/workflow/del-template     get：?id=1
     *           </p>
     *
     * @return {
     * "status":true,
     * "errorCode":null,
     * "errorMessage":null,
     * "detail":"true"
     * }
     */
    @RequestMapping(value="/template-del", method={RequestMethod.GET}) public DetailDTO<String> delTemplate(@RequestParam(value="id") Long id){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        boolean result=true;
        try{
            ws.RemoveTemplate(id);
        }catch(Exception e){
            result=false;
            detailDTO.setErrorCode(104+"");
            detailDTO.setErrorMessage("删除流程模板失败");
            log.info("删除流程模板失败",e);
        }
        detailDTO.setStatus(result);
        detailDTO.setDetail(result+"");
        return detailDTO;
    }
    /**
     * 查询流程模板列表
     *
     * @param p 页码
     * @param s 条数
     *          <p>
     *          示例：http://192.168.15.18:8080/api/v1/workflow/template-list    post：｛"p":0,"s":10,"tempName":"请假"｝
     *          </p>
     *
     * @return {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "id": "",               //模板ID
     * "title": "",             //模板名称
     * } ,
     * ....
     * ]}
     */
    @RequestMapping(value="/template-list", method={RequestMethod.POST})
    public ListDTO<KnTemplateDTO> templateList(@RequestParam(value="p", defaultValue="0") Integer p,@RequestParam(value="s", defaultValue="10") Integer s,@RequestParam(value="tempName", defaultValue="") String tempName){
        List<KnTemplateDTO> dtoList=Lists.newArrayList();
        boolean result=true;
        try{
            List<KnTemplate> list=ws.ListKnTemplate(p,s,tempName);
            for(KnTemplate obj : list){
                KnTemplateDTO dto=new KnTemplateDTO();
                dto.setId(obj.getId());
                dto.setTitle(obj.getTitle());
                dtoList.add(dto);
            }
        }catch(Exception e){
            result=false;
            log.info("查询流程模板列表失败",e);
        }
        return new ListDTO<>(result,dtoList);
    }
    /**
     * 获取流程模板详情
     *
     * @param id 模板ID
     *           <p>
     *           示例：http://192.168.15.18:8080/api/v1/workflow/template-detail    get：id=1
     *           </p>
     *
     * @return {
     * "status":true,
     * "errorCode":null,
     * "errorMessage":null,
     * "detail": {
     * "id": "",                    //流程模板ID
     * "title": "",                 //模板名称
     * "nodes": [                   //流程模板节点
     * {
     * "type": "USER",              //类型分为 用户||岗位  USER(用户)\\ DEPARTMENT(岗位)
     * " approveId ": "123",        //审批人ID：如类型为用户,该值为用户ID,否则为岗位ID
     * " approveName": "张三"       //审批人名称：如类型为用户,该值为用户名称,否则为岗位名称
     * },
     * {
     * "type": " DEPARTMENT ",      //类型分为 用户||岗位  USER(用户)\\ DEPARTMENT(岗位)
     * " approveId ": "123",        //审批人ID：如类型为用户,该值为用户ID,否则为岗位ID
     * " approveName": "移动事业部" //审批人名称：如类型为用户,该值为用户名称,否则为岗位名称
     * }
     * ...
     * ]}
     * }
     */
    @RequestMapping(value="/template-detail", method={RequestMethod.GET}) public DetailDTO<KnTemplateDTO> detailTemplate(@RequestParam(value="id") Long id){
        DetailDTO<KnTemplateDTO> detailDTO=new DetailDTO<>();
        KnTemplateDTO dto=new KnTemplateDTO();
        boolean result=true;
        try{
            KnTemplate template=ws.ReadKnTemplate(id);
            dto.setTitle(template.getTitle());
            dto.setId(template.getId());
            List<KnNode> nodeList=ws.ListKnNodeByTemplateId(id);
            if(nodeList.size()>0){
                List<KnNodeDTO> dtoList=Lists.newArrayList();
                for(KnNode node:nodeList){
                    KnNodeDTO nodeDTO=new KnNodeDTO();
                    nodeDTO.setId(node.getId());
                    nodeDTO.setTempId(id);
                    nodeDTO.setType(node.getType().toString());
                    nodeDTO.setApproveId(node.getApproveId());
                    nodeDTO.setApproveName(ws.QueryApproveName(node));
                    dtoList.add(nodeDTO);
                }
                dto.setNodes(dtoList);
            }
        }catch(Exception e){
            dto=null;
            result=false;
            detailDTO.setErrorCode(106+"");
            detailDTO.setErrorMessage("获取流程模板详情失败");
            log.info("获取流程模板详情失败",e);
        }
        detailDTO.setStatus(result);
        detailDTO.setDetail(dto);
        return detailDTO;
    }
    /**
     * 获取所有流程模板
     *
     * @param p 页码
     * @param s 条数
     *          <p>
     *          示例：http://192.168.15.18:8080/api/v1/workflow/template-all    get：?P=0&S=10
     *          </p>
     *
     * @return {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [ {
     * "id": "",                //模板ID
     * "title": "",             //模板名称
     * "count": "",             //提交次数
     * "type": "",              //模板类型
     * } ,
     * ....
     * ]}
     */
    @RequestMapping(value="/template-all", method={RequestMethod.GET})
    public ListDTO<KnTemplateDTO> templateAll(@RequestParam(value="p", defaultValue="0") Integer p,@RequestParam(value="s", defaultValue="10") Integer s){
        log.info(p+"");
        log.info(s+"");

        List<KnTemplateDTO> dtoList=Lists.newArrayList();
        boolean result=true;
        try{
            List<KnTemplate> list=ws.ListKnTemplate(p,s,"");
            for(KnTemplate obj : list){
                KnTemplateDTO dto=new KnTemplateDTO();
                dto.setId(obj.getId());
                dto.setTitle(obj.getTitle());
                dto.setCount(ws.CountKnApply(obj.getId(),Users.id()));
                //获取第一个节点的审批人
                KnNode node=ws.FirstNode(obj.getId());
                KnEmployee employee=ws.QueryApproveEmployee(node);
                if(employee!=null){
                    dto.setCurrApproveId(employee.getId());
                    dto.setCurrApproveName(employee.getUserName());
                }
                dtoList.add(dto);
            }
        }catch(Exception e){
            result=false;
            log.info("获取所有流程模板",e);
        }
        return new ListDTO<>(result,dtoList);
    }
    /**
     * 添加或修改申请记录
     *
     * @param knApply 申请对象
     *               <p>
     *               示例：http://192.168.15.18:8080/api/v1/workflow/apply-save
     *               post：{
     *               "detail":"{
     *               "id": "",
     *               "tempId": "",               //流程模板ID
     *               "startTime": "yyyy-MM-dd",  //开始日期
     *               "endTime": "yyyy-MM-dd",    //结束日期
     *               "isHalfDay": "true",        //半天
     *               "description": "",          //描述
     *               "files": [                  //附件
     *               {
     *               "url": "",                  //附件位置
     *               "name": "",                 //附件名
     *               " fileType ": ""            //附件类型
     *               },
     *               {
     *               "sha": "",
     *               "name": "",
     *               "fileType": ""
     *               }
     *               ],
     *               "other": {                  //其他未约束字段存储位置 以JSON格式优先存储 便于前端解析，后端存储
     *               "type": "事假类型",
     *               "address": "位置"
     *               }
     *               }"
     *               }
     *               </p>
     *
     * @return {
     * "status":true,
     * "errorCode":null,
     * "errorMessage":null,
     * "detail":"true"
     * }
     */
    @RequestMapping(value="/apply-save", method={RequestMethod.POST})
    public DetailDTO<String> saveApply(@RequestParam(value="knApply") String knApply){
        log.info(knApply);

        DetailDTO<String> detailDTO=new DetailDTO<>();
        boolean result=true;
        String errorMessage="新增申请失败";
        try{
            KnApplyDTO dto=JsonMapper.nonDefaultMapper().fromJson(knApply,KnApplyDTO.class);
            if(dto.getId()!=null&&dto.getId()>0){
                errorMessage="修改申请失败";
            }
            ws.SaveKnApplyAndPush(dto);
        }catch(Exception e){
            result=false;
            detailDTO.setErrorCode(108+"");
            detailDTO.setErrorMessage(errorMessage);
            log.info(Users.id()+errorMessage,e);
        }
        detailDTO.setStatus(result);
        detailDTO.setDetail(result+"");
        return detailDTO;
    }
    /**
     * 查询流程模板列表
     *
     * @param p 页码
     * @param s 条数
     *          <p>
     *          示例：http://192.168.15.18:8080/api/v1/workflow/apply-list    POST：?P=0&S=10&tempId=0
     *          </p>
     *
     * @return {
     * "status" : true,
     * "errorCode" : null,
     * "errorMessage" : null,
     * "list" : [
     * {
     * "id":"",                 //申请流程ID
     * "tempName":"",           //流程模板名称
     * "description":"",        //申请描述
     * "status":"",             //当前申请状态
     * "currApproveName":"",    //当前审批人姓名
     * "files":[//附件
     * {
     * "sha": "",                //附件位置
     * "name": "",               //附件名
     * " fileType ": ""          //附件类型
     * },
     * {
     * "sha": "",
     * "name": "",
     * "fileType": ""
     * }
     * ],
     * "applyTime":"yyyy-MM-dd HH:mm" //申请时间
     * }
     * ]
     * }
     */
    @RequestMapping(value="/apply-list", method={RequestMethod.POST})
    public ListDTO<KnApplyDTO> applyList(@RequestParam(value="p", defaultValue="0") Integer p,@RequestParam(value="s", defaultValue="10") Integer s,@RequestParam(value="type") String type,@RequestParam(value="search") String search){
        log.info(p+"");
        log.info(s+"");
        log.info(type);
        log.info(search);

        List<KnApplyDTO> dtoList=Lists.newArrayList();
        boolean result=true;
        try{
            List<KnApply> list=ws.ListKnApply(p,s,type,search);
            //修改可见
            /*if("approve".equals(type)){
                ws.ChangeApproveHasView(list);
            }*/
            for(KnApply obj : list){
                KnApplyDTO dto=new KnApplyDTO();
                dto.setId(obj.getId());
                dto.setUserId(obj.getUserId());
                KnEmployee employee=ws.ReadKnEmployee(obj.getUserId());
                dto.setUserName(employee==null?null:employee.getUserName());
                dto.setTempId(obj.getTempId());
                dto.setTempName(ws.ReadKnTemplate(obj.getTempId()).getTitle());
                dto.setDescription(obj.getDescription());
                //申请发起时间
                dto.setApplyTime(new DateTime(obj.getCreateTime()).toString("yyyy-MM-dd HH:mm"));
                List<KnWorkflowAttach> files=ws.ListKnWorkflowAttach(obj.getId(),KnWorkflowAttach.BusType.APPLY);
                if(files!=null&&files.size()>0){
                    dto.setFiles(JsonMapper.nonEmptyMapper().toJson(files));
                }
                //获取状态
                dto.setStatus(ws.ListKnHistoryApprove(obj.getId()).get(0).getApproveStatus().toString());
                dtoList.add(dto);
            }
        }catch(Exception e){
            result=false;
            log.info("列表类型："+type);
            log.info("查询流程模板列表失败",e);
        }
        return new ListDTO<>(result,dtoList);
    }

    /**
     * 获取获取申请详情
     *
     * @param id 模板ID
     *           <p>
     *           示例：http://192.168.15.18:8080/api/v1/workflow/apply-detail    get：id=1
     *           </p>
     *
     * @return {
     * "status":true,
     * "errorCode":null,
     * "errorMessage":null,
     * "detail": {
     * "id": "",                    //流程模板ID
     * "title": "",                 //模板名称
     * "nodes": [                   //流程模板节点
     * {
     * "type": "USER",              //类型分为 用户||岗位  USER(用户)\\ DEPARTMENT(岗位)
     * " approveId ": "123",        //审批人ID：如类型为用户,该值为用户ID,否则为岗位ID
     * " approveName": "张三"       //审批人名称：如类型为用户,该值为用户名称,否则为岗位名称
     * },
     * {
     * "type": " DEPARTMENT ",      //类型分为 用户||岗位  USER(用户)\\ DEPARTMENT(岗位)
     * " approveId ": "123",        //审批人ID：如类型为用户,该值为用户ID,否则为岗位ID
     * " approveName": "移动事业部" //审批人名称：如类型为用户,该值为用户名称,否则为岗位名称
     * }
     * ...
     * ]}
     * }
     */
    @RequestMapping(value="/apply-detail", method={RequestMethod.GET})
    public DetailDTO<KnApplyDTO> detailApply(@RequestParam(value="id") Long id){
        log.info(id+"");

        DetailDTO<KnApplyDTO> detailDTO=new DetailDTO<>();
        KnApplyDTO dto=new KnApplyDTO();
        boolean result=true;
        try{
            //修改状态为已阅
            KnCurrentApprove currentApprove=ws.ReadCurrentApprove(id);
            if(currentApprove!=null){
                ws.View(currentApprove);
                //获取下一个审批人
                if( currentApprove.getIsNormalAppr().equals(IdEntity.ActiveType.ENABLE) ){
                    KnNode node = null;
                    if(currentApprove.getIsAgin().equals(IdEntity.ActiveType.ENABLE)){
                        node = ws.FirstNode(currentApprove.getTempId());
                    }else{
                        node=ws.ReadKnNode(currentApprove.getNodeId());
                    }
                    //node=ws.ReadKnNode(currentApprove.getNodeId());
                    dto.setCurrApproveId(ws.FindNextApproverId(node));
                    KnEmployee emp=ws.ReadKnEmployee(node.getApproveId());
                    dto.setCurrApproveName(emp==null?null:emp.getUserName());
                }

            }
            KnApply obj=ws.ReadKnApply(id);
            dto.setId(obj.getId());
            dto.setUserId(obj.getUserId());
            KnEmployee employee=ws.ReadKnEmployee(obj.getUserId());
            dto.setUserName(employee==null?null:employee.getUserName());
            dto.setUserIcon(employee==null?null:employee.getImageAddress());
            dto.setTempId(obj.getTempId());
            dto.setTempName(ws.ReadKnTemplate(obj.getTempId()).getTitle());
            dto.setDescription(obj.getDescription());
            dto.setOther(obj.getOther());


            //开始时间、结束时间、半天
            if(obj.getStartTime()!=null&&obj.getStartTime()>0){
                dto.setStartTime(new DateTime(obj.getStartTime()).toString("yyyy-MM-dd"));
            }
            if(obj.getEndTime()!=null&&obj.getEndTime()>0){
                dto.setEndTime(new DateTime(obj.getEndTime()).toString("yyyy-MM-dd"));
            }
            if(obj.getIsHalfDay()!=null&&obj.getIsHalfDay()==IdEntity.ActiveType.ENABLE){
                dto.setIsHalfDay("true");
            }
            //申请发起时间
            dto.setApplyTime(new DateTime(obj.getCreateTime()).toString("yyyy-MM-dd HH:mm"));
            List<KnWorkflowAttach> files=ws.ListKnWorkflowAttach(obj.getId(),KnWorkflowAttach.BusType.APPLY);
            if(files==null){
                files=Lists.newArrayList();
            }
            //审批历史
            List<KnHistoryApprove> approves=ws.ListKnHistoryApprove(id);
            dto.setStatus(approves.get(0).getApproveStatus().toString());
            List<KnHistoryApproveDTO> approveInfos=new ArrayList<>();
            for(KnHistoryApprove approve:approves){
                //需要展示信息
                KnHistoryApproveDTO info=new KnHistoryApproveDTO();
                info.setId(approve.getId());
                info.setApproveId(approve.getApproveId());
                KnEmployee emp=ws.ReadKnEmployee(approve.getApproveId());
                info.setApproveName(emp==null?null:emp.getUserName());
                info.setApproveOpinion(approve.getApproveOpinion());
                info.setApproveStatus(approve.getApproveStatus());
                info.setApproveTime(new DateTime(approve.getSubmitTime()).toString("yyyy-MM-dd HH:mm"));
                List<KnWorkflowAttach> infoFiles=ws.ListKnWorkflowAttach(approve.getId(),KnWorkflowAttach.BusType.APPROVE);
                if(infoFiles!=null&&infoFiles.size()>0){
                    //附件叠加到一起
                    files.addAll(infoFiles);
                }
                approveInfos.add(info);
            }
            if(files.size()>0){
                dto.setFiles(JsonMapper.nonEmptyMapper().toJson(files));
            }
            dto.setApproveInfos(approveInfos);
        }catch(Exception e){
            dto=null;
            result=false;
            detailDTO.setErrorCode(110+"");
            detailDTO.setErrorMessage("获取申请详情失败");
            e.printStackTrace();
        }
        detailDTO.setStatus(result);
        detailDTO.setDetail(dto);
        return detailDTO;
    }

    @RequestMapping(value="/approve-back", method={RequestMethod.POST})
    public DetailDTO<String> approveBack(@RequestParam(value="applyId") Long applyId,@RequestParam(value="approveOpinion") String approveOpinion){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        //TODO:需要根据ID获取历史记录，并删除

        //TODO:创建一条心的审批记录，将审批人修改为自己
        ws.Withdraw(applyId);

        //TODO:状态，需要try
        detailDTO.setStatus(true);
        detailDTO.setDetail(true+"");
        return detailDTO;
    }
    /**
     * 审批
     * @return
     */
    @RequestMapping(value="/approveWf", method={RequestMethod.POST})
       public DetailDTO<String> ApproveWf(@RequestParam(value="detail") String detail){
//    public DetailDTO<String> approve(@RequestBody KnHistoryApproveDTO dto){
        DetailDTO<String> detailDTO=new DetailDTO<>();
        int result = ws.Approve(detail);
        if(result==1){
            detailDTO.setErrorCode(1+"");
            detailDTO.setErrorMessage("没有传detail参数");
        }
        //TODO:状态，需要try
        detailDTO.setStatus(true);
        detailDTO.setDetail(true+"");
        return detailDTO;
    }


    //============陈超增加1========//
    /**
     * 获取与登录人相关的流程动态
     * @param p
     * @param s
     * @return
     */
    @RequestMapping(value="/dynamic", method={RequestMethod.GET})
    public ListDTO<KnDynamicDTO> Dynamic(@RequestParam(value="p") int p,@RequestParam(value="s") int s){
        List<KnDynamicDTO> dtos = ws.BuildKnDynamicDTO(p,s);
        return new ListDTO<KnDynamicDTO>(true,dtos);
    }
    //============陈超增加2========//
}
