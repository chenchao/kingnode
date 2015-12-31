package com.kingnode.approval.rest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.approval.dto.ApplyTypeDTO;
import com.kingnode.approval.dto.DynamicDTO;
import com.kingnode.approval.dto.KnApprovalDTO;
import com.kingnode.approval.dto.KnApprovalFileDTO;
import com.kingnode.approval.dto.KnApprovalProcessDTO;
import com.kingnode.approval.dto.KnApprovalVariablesDTO;
import com.kingnode.approval.dto.KnFlowFormDTO;
import com.kingnode.approval.entity.KnApproval;
import com.kingnode.approval.entity.KnApprovalFile;
import com.kingnode.approval.entity.KnApprovalProcess;
import com.kingnode.approval.entity.KnApprovalVariables;
import com.kingnode.approval.entity.KnFlow;
import com.kingnode.approval.entity.KnFlowForm;
import com.kingnode.approval.service.ApprovalService;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.xsimple.rest.DetailDTO;
import com.kingnode.xsimple.rest.ListDTO;
import com.kingnode.xsimple.rest.RestStatus;
import com.kingnode.xsimple.util.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping({"/api/v1/approval","/api/secure/v1/approval"})
public class ApprovalRestController{
    @Autowired
    private ApprovalService as;
    /**
     * 加载流程表单
     *
     * @param id 表单id
     */
    @RequestMapping(value="/read-flow-form/{id}", method={RequestMethod.GET})
    public DetailDTO ReadFlowForm(@PathVariable("id") Long id){
        KnFlowForm flowForm=as.ReadKnFlowForm(id);
        KnFlowFormDTO dto=BeanMapper.map(flowForm,KnFlowFormDTO.class);
        return new DetailDTO<>(true,dto);
    }
    /**
     * 统计用户各类型申请提交次数
     */
    @RequestMapping(value="/read-apply-type", method={RequestMethod.GET})
    public ListDTO<ApplyTypeDTO> ReadApplyType(){
        List<ApplyTypeDTO> list=Lists.newArrayList();
        List<Object[]> listData=as.ReadApprovalCount(Users.id());
        for(Object[] obj : listData){
            ApplyTypeDTO dto=new ApplyTypeDTO();
            dto.setFormId(Long.parseLong(obj[0].toString()));
            dto.setName(obj[1].toString());
            dto.setNum(Integer.parseInt(obj[2].toString()));
        }
        return new ListDTO<>(true,list);
    }
    /**
     * 创建申请流程
     *
     * @param dto 申请对象DTO
     */
    @RequestMapping(value="/save-approval", method={RequestMethod.POST})
    public RestStatus SaveApproval(@RequestBody KnApprovalDTO dto){
        KnApproval ka=BeanMapper.map(dto,KnApproval.class);
        ka.setCreateTime(new Date().getTime());
        ka.setUserId(Users.id());
        ka.setUserName(Users.name());
        ka.setNodeType(KnApproval.NodeType.start);
        ka=as.SaveKnApproval(ka);
        if(dto.getFlow()!=null){
            KnFlow flow=BeanMapper.map(dto.getFlow(),KnFlow.class);
            as.SaveKnFlow(flow);
        }
        if(dto.getFile()!=null&&!dto.getFile().isEmpty()){
            for(KnApprovalFileDTO f : dto.getFile()){
                KnApprovalFile file=BeanMapper.map(f,KnApprovalFile.class);
                file.setKa(ka);
                as.SaveKnApprovalFile(file);
            }
        }
        if(dto.getProcess()!=null&&!dto.getProcess().isEmpty()){
            for(KnApprovalProcessDTO p : dto.getProcess()){
                KnApprovalProcess process=BeanMapper.map(p,KnApprovalProcess.class);
                process.setKa(ka);
                as.SaveKnApprovalProcess(process);
            }
        }
        if(dto.getVariables()!=null&&!dto.getVariables().isEmpty()){
            for(KnApprovalVariablesDTO v : dto.getVariables()){
                KnApprovalVariables variables=BeanMapper.map(v,KnApprovalVariables.class);
                variables.setKa(ka);
                as.SaveKnApprovalVariables(variables);
            }
        }
        return new RestStatus(true);
    }
    /**
     * 用户审批流程动态
     *
     * @param pageNo   页码
     * @param pageSize 每页显示数
     */
    @RequestMapping(value="/dynamic", method={RequestMethod.GET})
    public ListDTO<DynamicDTO> dynamic(@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="5") Integer pageSize){
        List<DynamicDTO> list=Lists.newArrayList();
        return new ListDTO<>(true,list);
    }
    /**
     * 申请详细
     *
     * @param id 申请ID
     *
     * @return 申请详细信息
     */
    @RequestMapping(value="/detail/{id}", method={RequestMethod.GET})
    public DetailDTO<KnApprovalDTO> detail(@PathVariable(value="id") Long id){
        KnApproval approval=as.ReadKnApproval(id);
        KnApprovalDTO dto=BeanMapper.map(approval,KnApprovalDTO.class);
        return new DetailDTO<>(true,dto);
    }
    /**
     * 当前用户申请列表
     *
     * @return 当前用户申请集合DTO
     */
    @RequestMapping(value="/current-user-approval-list", method={RequestMethod.GET})
    public ListDTO<KnApprovalDTO> currentUserApprovalList(@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="5") Integer pageSize){
        List<KnApprovalDTO> list=Lists.newArrayList();
        List<KnApproval> approvals=as.ReadKnApprovalByUser(Users.id(),pageNo,pageSize);
        for(KnApproval approval : approvals){
            KnApprovalDTO dto=BeanMapper.map(approval,KnApprovalDTO.class);
            list.add(dto);
        }
        return new ListDTO<>(true,list);
    }
    /**
     * 当前用户申请搜索
     *
     * @param content  搜索条件，申请内容
     * @param pageNo   页码
     * @param pageSize 每页显示数
     *
     * @return 当前用户申请集合DTO
     */
    @RequestMapping(value="/current-user-approval-search", method={RequestMethod.GET})
    public ListDTO<KnApprovalDTO> currentUserApprovalSearch(@RequestParam(value="content") String content,@RequestParam(value="p", defaultValue="0") Integer pageNo,@RequestParam(value="s", defaultValue="5") Integer pageSize){
        List<KnApprovalDTO> list=Lists.newArrayList();
        if(Strings.isNullOrEmpty(content)){
            content="%%";
        }else{
            content="%"+content+"%";
        }
        List<KnApproval> approvals=as.ReadKnApprovalByUserSearch(Users.id(),content,pageNo,pageSize);
        for(KnApproval approval : approvals){
            KnApprovalDTO dto=BeanMapper.map(approval,KnApprovalDTO.class);
            list.add(dto);
        }
        return new ListDTO<>(true,list);
    }
    /**
     * 更新未查看状态
     */
    @RequestMapping(value="/approval-status/{id}", method={RequestMethod.GET})
    public RestStatus approvalStatus(@PathVariable(value="id") Long id){
        as.UpdateKnApprovalProcessForNodeType(id);
        return new RestStatus(true);
    }
    /**
     * 用户审批
     *
     * @param dto
     *
     * @return RestStatus对象
     */
    @RequestMapping(value="/user-approve", method={RequestMethod.POST})
    public RestStatus userApprove(@RequestBody KnApprovalProcessDTO dto){
        RestStatus rs=new RestStatus(true);
        KnApproval ka=new KnApproval();
        ka.setId(dto.getApprovalId());
        KnApprovalProcess process=BeanMapper.map(dto,KnApprovalProcess.class);
        process.setKa(ka);
        process.setCreateTime(new Date().getTime());
        as.SaveKnApprovalProcess(process);
        if(dto.getNextUserId()!=null||"disagree".equals(dto.getResultType().toString())){
            KnApprovalProcess nextProcess=new KnApprovalProcess();
            nextProcess.setKa(ka);
            nextProcess.setUserName(dto.getNextUserName());
            nextProcess.setUserId(dto.getNextUserId());
            nextProcess.setCreateTime(new Date().getTime());
            if("disagree".equals(dto.getResultType().toString())){
                nextProcess.setResultType(KnApprovalProcess.ResultType.toInitiate);
            }else{
                nextProcess.setResultType(KnApprovalProcess.ResultType.noView);
            }
            as.SaveKnApprovalProcess(nextProcess);
        }
        if(dto.getNextUserId()==null&&"agree".equals(dto.getResultType().toString())){
            as.UpdateKnApprovalForNodeType(dto.getApprovalId(),KnApproval.NodeType.end);
        }
        return rs;
    }
    /**
     * 用户附件列表
     *
     * @param fileTypes 文件类型 例如 EXCEL 如果多个文件类型，使用逗号隔开(EXCEL,WORD)
     */
    @RequestMapping(value="/read-user-file", method={RequestMethod.GET})
    public ListDTO<KnApprovalFileDTO> ReadUserFile(@RequestParam(value="fileTypes") String fileTypes){
        List<KnApprovalFile> files=as.ReadUserFile(Users.id(),as.getFileTypes(fileTypes));
        List<KnApprovalFileDTO> list=Lists.newArrayList();
        for(KnApprovalFile file : files){
            KnApprovalFileDTO dto=BeanMapper.map(file,KnApprovalFileDTO.class);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            dto.setCreateDate(sdf.format(file.getCreateTime()));
            list.add(dto);
        }
        return new ListDTO<>(true,list);
    }
}