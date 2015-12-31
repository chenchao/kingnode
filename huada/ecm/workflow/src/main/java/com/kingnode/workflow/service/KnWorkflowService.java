package com.kingnode.workflow.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.workflow.config.WorkflowConfig;
import com.kingnode.workflow.dao.KnApplyDao;
import com.kingnode.workflow.dao.KnCurrentApproveDao;
import com.kingnode.workflow.dao.KnHistoryApproveDao;
import com.kingnode.workflow.dao.KnNodeDao;
import com.kingnode.workflow.dao.KnTemplateDao;
import com.kingnode.workflow.dao.KnWorkflowAttachDao;
import com.kingnode.workflow.dto.KnApplyDTO;
import com.kingnode.workflow.dto.KnDynamicDTO;
import com.kingnode.workflow.dto.KnHistoryApproveDTO;
import com.kingnode.workflow.dto.KnNodeDTO;
import com.kingnode.workflow.entity.KnApply;
import com.kingnode.workflow.entity.KnCurrentApprove;
import com.kingnode.workflow.entity.KnHistoryApprove;
import com.kingnode.workflow.entity.KnNode;
import com.kingnode.workflow.entity.KnTemplate;
import com.kingnode.workflow.entity.KnWorkflowAttach;
import com.kingnode.xsimple.dao.system.KnEmployeeDao;
import com.kingnode.xsimple.dao.system.KnEmployeeOrganizationDao;
import com.kingnode.xsimple.dao.system.KnOrganizationDao;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganization;
import com.kingnode.xsimple.entity.system.KnEmployeeOrganizationId;
import com.kingnode.xsimple.entity.system.KnOrganization;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.util.Users;
import com.kingnode.xsimple.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chenchao
 *         流程处理service
 */
@Component @Transactional(readOnly=true) public class KnWorkflowService{
    private static Logger log=LoggerFactory.getLogger(KnWorkflowService.class);
    @Autowired private KnApplyDao applyDao;
    @Autowired private KnCurrentApproveDao currentApproveDao;
    @Autowired private KnHistoryApproveDao historyApproveDao;
    @Autowired private KnNodeDao nodeDao;
    @Autowired private KnTemplateDao templateDao;
    @Autowired private KnWorkflowAttachDao workflowAttachDao;
    @Autowired private OrganizationService organizationService;
    @Autowired private KnEmployeeDao empDao;
    @Autowired private KnOrganizationDao orgDao;
    @Autowired private KnEmployeeOrganizationDao employeeOrganizationDao;
    /**
     * 推送到下一个节点（审批通过的）
     *
     * @param applyId    申请表id
     * @param approverId 审批人id
     * @param status     有两种情况，同意或同意并指定下一审批人。
     */
    @Transactional(readOnly=false) public void Push(Long applyId,Long approverId,String status,String opinion){
        KnCurrentApprove currentApprove=currentApproveDao.findByApplyId(applyId);
        KnHistoryApprove.ApproveStatus finalStatus=null;
        //如果同意并结束，那就直接结束了
        if(status==null||"".equals(status))
            throw new RuntimeException("没有指定同意类型");
        if(status.equals(WorkflowConfig.AGREE_AND_OVER)){
            //同意并结束
            //删除当前审批表中信息
            currentApproveDao.delete(currentApprove);
            finalStatus=KnHistoryApprove.ApproveStatus.END;
            //修改审批历史表
            UpdateHisAppr(currentApprove.getNodeId(),currentApprove.getApplyId(),opinion,finalStatus);
        }else if(status.equals(WorkflowConfig.AGREE_AND_NEXT)){
            //同意并指定下一个审批人
            if(approverId==null||approverId.equals(0l))
                throw new RuntimeException("没有指定下一个审批人");
            finalStatus=KnHistoryApprove.ApproveStatus.PASS;
            //修改审批历史表
            UpdateHisAppr(currentApprove.getNodeId(),currentApprove.getApplyId(),opinion,finalStatus);
            //按正常审批链来审批
            KnNode nextNode=NextNode(currentApprove);
            Long nextApproverId=FindNextApproverId(nextNode);
            if(nextNode==null||nextNode.getId().equals(0l)||currentApprove.getIsNormalAppr().equals(IdEntity.ActiveType.DISABLE)||(nextNode!=null&&!nextNode.getId().equals(0l)&&!nextApproverId.equals(approverId))){
                //虽然这可能是最后一个审批节点，但是用户可以再指定下一个审批人来延长审批链
                //如果是最后一个审批节点（找不到下一个审批节点）
                //或者之前审批是不按正常的审批链来走
                //或者下个审批人不按正常审批链来走
                KnHistoryApprove approveHis=getKnHistoryApprove(approverId,currentApprove,IdEntity.ActiveType.DISABLE,KnHistoryApprove.ApproveRole.APPROVER,IdEntity.ActiveType.DISABLE);
                historyApproveDao.save(approveHis);
                //修改当前审批表中对应记录
                buildKnCurrentApprove(approverId,null,IdEntity.ActiveType.DISABLE,IdEntity.ActiveType.DISABLE,currentApprove,IdEntity.ActiveType.DISABLE);
                currentApproveDao.save(currentApprove);
            }else{
                //这个分支都是正常审批的情况
                //往审批历史表中插入对应记录,需要修改一条历史记录和插入一条新的记录，此处先插入
                KnHistoryApprove approveHis=getKnHistoryApprove(approverId,currentApprove,nextNode,nextApproverId);
                historyApproveDao.save(approveHis);
                //修改当前审批表中对应记录
                buildKnCurrentApprove(approverId,nextNode.getId(),IdEntity.ActiveType.ENABLE,IdEntity.ActiveType.DISABLE,currentApprove,IdEntity.ActiveType.DISABLE);
                currentApproveDao.save(currentApprove);
            }
        }
    }
    /**
     * 获取审批节点
     * @param nodeId
     * @return
     */
    public KnNode ReadKnNode(Long nodeId){
        return nodeDao.findOne(nodeId);
    }

    /**
     * 获取下一个审批人
     *
     * @param nextNode
     *
     * @return
     */
    public Long FindNextApproverId(KnNode nextNode){
        if(nextNode!=null){
//            if(KnNode.NodeType.USER.equals(nextNode.getType())){
//                return nextNode.getApproveId();
//            }else if(KnNode.NodeType.DEPARTMENT.equals(nextNode.getType())){
//                KnEmployee emp = QueryApproveByOrg(nextNode.getApproveId());
//                return emp!=null?emp.getId():0l;//先这样，后续再写根据组织，查找组织里面的主要负责人。
//            }
            try{
                KnEmployee emp = QueryApproveEmployee(nextNode);
                return emp.getId();
            }catch(Exception e){
            }
        }
        return 0l;
        //throw new RuntimeException("节点信息为空KnNode:"+node);
    }
    /**
     * 审批拒绝，回退到申请人
     *
     * @param applyId 申请表id
     */
    @Transactional(readOnly=false) public void Fallback(Long applyId,String opinion){
        //往审批历史表中插入审批拒绝的记录
        KnCurrentApprove currentApprove=currentApproveDao.findByApplyId(applyId);
        //找出模板对应的第一个节点审批人
        List<KnHistoryApprove> starters=historyApproveDao.queryStarter(applyId);
        KnHistoryApprove starter=starters.get(0);
//        KnNode firstNode = FirstNode(currentApprove.getTempId());
//        Long approverId = FindNextApproverId(firstNode);
        //修改审批历史表中的记录
        UpdateHisAppr(currentApprove.getNodeId(),currentApprove.getApplyId(),opinion,KnHistoryApprove.ApproveStatus.REJECT);
        //网审批历史表中插入这条记录
        KnHistoryApprove approveHis=getKnHistoryApprove(starter.getApproveId(),currentApprove,IdEntity.ActiveType.ENABLE,KnHistoryApprove.ApproveRole.APPLYER,IdEntity.ActiveType.ENABLE);
        historyApproveDao.save(approveHis);
        //修改当前审批表中对应记录 相当于给自己审批
        buildKnCurrentApprove(starter.getApproveId(),null,IdEntity.ActiveType.ENABLE,IdEntity.ActiveType.DISABLE,currentApprove,IdEntity.ActiveType.ENABLE);
        currentApproveDao.save(currentApprove);
    }
    /**
     * 撤回  发起人不能撤回
     *
     * @param applyId return 1 代表成功过  2 代表审批信息已经被查看，不能撤回
     */
    @Transactional(readOnly=false) public int Withdraw(Long applyId){
        //先判断是否已经查看，没有查看则可以撤回，否则，不能再撤回
        KnCurrentApprove currentApprove=currentApproveDao.findByApplyId(applyId);
        if(currentApprove.getIsHasView().equals(IdEntity.ActiveType.ENABLE)){
            log.error("审批信息已经被查看，不能再撤回currentApproveId:"+currentApprove.getId());
            return 2;
        }
        //首先把历史表中的最新记录给删掉，然后把之前的审批信息修改成审批中的状态
        //KnNode prevNode=PrevNode(currentApprove);
        List<KnHistoryApprove> curHistoryApproves=historyApproveDao.queryNoApproveApplyId(applyId);
        KnHistoryApprove curHistoryApprove = curHistoryApproves.get(0);
        historyApproveDao.delete(curHistoryApprove);
        List<KnHistoryApprove> prevHistoryApproves=historyApproveDao.queryHistoryApprove(applyId);
        KnHistoryApprove prevHistoryApprove=prevHistoryApproves.get(0);
        prevHistoryApprove.setIsHasView(IdEntity.ActiveType.ENABLE);
        prevHistoryApprove.setApproveStatus(KnHistoryApprove.ApproveStatus.APPROVEING);
        prevHistoryApprove.setSubmitTime(null);
        historyApproveDao.save(prevHistoryApprove);
        //修改当前审批表中的记录
        currentApprove.setApproveId(prevHistoryApprove.getApproveId());
        currentApprove.setIsHasView(IdEntity.ActiveType.ENABLE);
        currentApprove.setNodeId(prevHistoryApprove.getNodeId());
        currentApproveDao.save(currentApprove);
        return 1;
    }
    /**
     * 推动第一个接口
     * @param tempId 模板id
     * @param applyId
     * @param approverId 审批人id
     */
    @Transactional(readOnly=false)
    public void PushFirst(Long tempId,Long applyId,Long approverId){
        KnNode node = FirstNode(tempId);
        Long approverDbId=FindNextApproverId(node);
        //插入当前审批表
        KnCurrentApprove currentApp = currentApproveDao.findCurrentApproveByApplyId(applyId);
        boolean isAgain=true;//是否重新发起
        if(currentApp==null || currentApp.getId()<=0l){
            currentApp=new KnCurrentApprove();
            isAgain=false;
        }
        currentApp.setIsAgin(IdEntity.ActiveType.DISABLE);
        currentApp.setIsHasView(IdEntity.ActiveType.DISABLE);
        currentApp.setNodeId(node.getId());
        currentApp.setApproveId(approverId);
        currentApp.setIsNormalAppr(approverId.equals(approverDbId)?IdEntity.ActiveType.ENABLE:IdEntity.ActiveType.DISABLE);
        currentApp.setApplyId(applyId);
        currentApp.setTempId(tempId);
        currentApproveDao.save(currentApp);
        //先插入两条记录到历史表
        //插入发起人记录
        if(!isAgain){
            KnHistoryApprove starterHis=getKnHistoryApprove(currentApp,KnHistoryApprove.ApproveRole.APPLYER,KnHistoryApprove.ApproveStatus.START,Users.id());
            historyApproveDao.save(starterHis);
        }else{
            //修改审批历史记录
            UpdateHisAppr(null,applyId,"",KnHistoryApprove.ApproveStatus.ANGIN);
        }
        //插入下个审批节点的审批信息
        KnHistoryApprove approveHis=getKnHistoryApprove(currentApp,KnHistoryApprove.ApproveRole.APPROVER,KnHistoryApprove.ApproveStatus.VIEW,currentApp.getApproveId());
        historyApproveDao.save(approveHis);
    }
    /**
     * 组装审批历史表信息
     * @param currentApp
     * @param role
     * @param approveStatus
     * @return
     */
    private KnHistoryApprove getKnHistoryApprove(KnCurrentApprove currentApp,KnHistoryApprove.ApproveRole role,KnHistoryApprove.ApproveStatus approveStatus,Long approveId){
        KnHistoryApprove starterHis=new KnHistoryApprove();
        starterHis.setTempId(currentApp.getTempId());
        starterHis.setApplyId(currentApp.getApplyId());
        starterHis.setApproveId(approveId);
        starterHis.setApproveOpinion(null);
        starterHis.setIsNormalAppr(IdEntity.ActiveType.ENABLE);
        starterHis.setRole(role);
        starterHis.setSubmitTime(null);
        starterHis.setType(KnNode.NodeType.USER);
        starterHis.setNodeId(currentApp.getNodeId());
        starterHis.setApproveStatus(approveStatus);
        starterHis.setIsAgin(IdEntity.ActiveType.DISABLE);
        starterHis.setIsHasView(IdEntity.ActiveType.DISABLE);
        return starterHis;
    }
    /**
     * 审批查看
     * @param currentApp 待审批对象
     */
    @Transactional(readOnly=false)
    public void View(KnCurrentApprove currentApp){
        //KnCurrentApprove currentApp = currentApproveDao.findByApplyId(applyId);
        if(currentApp!=null && currentApp.getApproveId().equals(Users.id())){
            currentApp.setIsHasView(IdEntity.ActiveType.ENABLE);
            currentApproveDao.save(currentApp);
            List<KnHistoryApprove> historyApprove = historyApproveDao.queryNoApproveApplyId(currentApp.getApplyId());
            KnHistoryApprove curHis = historyApprove.get(0);
            curHis.setIsHasView(IdEntity.ActiveType.ENABLE);
            curHis.setApproveStatus(KnHistoryApprove.ApproveStatus.APPROVEING);
            historyApproveDao.save(curHis);
        }
    }
    /**
     * 获取第一个节点对象
     *
     * @param tempId
     *
     * @return
     */
    public KnNode FirstNode(Long tempId){
        return nodeDao.queryFirstNode(tempId);
    }
    /**
     * 获取下一个节点对象,如果已经是最后节点
     *
     * @param currentNode 当前节点信息
     *
     * @return
     */
    public KnNode NextNode(KnCurrentApprove currentNode){
        //如果是重新发起的节点，需要另外处理
        if(currentNode.getIsAgin().equals(IdEntity.ActiveType.ENABLE)){
            return FirstNode(currentNode.getTempId());
        }else{
            return nodeDao.queryNextNode(currentNode.getId());
        }
    }
    /**
     * 获取上一个节点对象,如果是第一个节点,则返回空对象
     *
     * @param currentNode 当前节点信息
     *
     * @return
     */
    public KnNode PrevNode(KnCurrentApprove currentNode){
        //如果是重新发起的节点，需要另外处理
        if(currentNode.getIsAgin().equals(IdEntity.ActiveType.ENABLE)){
            return null;
        }else{
            return nodeDao.queryPrevNode(currentNode.getNodeId());
        }
    }
    /**
     * 组装当前审批信息
     *
     * @param approverId
     * @param nodeId
     * @param isNormal
     * @param hasView
     * @param currentApprove
     */
    private void buildKnCurrentApprove(Long approverId,Long nodeId,IdEntity.ActiveType isNormal,IdEntity.ActiveType hasView,KnCurrentApprove currentApprove,IdEntity.ActiveType isAgin){
        currentApprove.setApproveId(approverId);
        currentApprove.setNodeId(nodeId);
        currentApprove.setIsNormalAppr(isNormal);
        currentApprove.setIsHasView(hasView);
        currentApprove.setIsAgin(isAgin);
    }
    /**
     * 组装最新的历史审批信息，主要用于用户不按正常审批链走
     *
     * @param approverId
     * @param currentApprove
     *
     * @return
     */
    private KnHistoryApprove getKnHistoryApprove(Long approverId,KnCurrentApprove currentApprove,IdEntity.ActiveType isNormal,KnHistoryApprove.ApproveRole role,IdEntity.ActiveType isAgin){
        KnHistoryApprove approveHis=new KnHistoryApprove();
        approveHis.setTempId(currentApprove.getTempId());
        approveHis.setApplyId(currentApprove.getApplyId());
        approveHis.setApproveId(approverId);
        approveHis.setApproveOpinion(null);
        approveHis.setIsNormalAppr(isNormal);
        approveHis.setRole(role);
        approveHis.setSubmitTime(null);
        approveHis.setType(KnNode.NodeType.USER);
        approveHis.setIsAgin(isAgin);
        approveHis.setNodeId(null);
        approveHis.setApproveStatus(KnHistoryApprove.ApproveStatus.VIEW);
        approveHis.setIsHasView(IdEntity.ActiveType.DISABLE);
        return approveHis;
    }
    /**
     * 组装最新的审批历史对象，主要用于用户根据正常审批链走
     *
     * @param approverId
     * @param currentApprove
     * @param nextNode
     * @param nextApproverId
     *
     * @return
     */
    private KnHistoryApprove getKnHistoryApprove(Long approverId,KnCurrentApprove currentApprove,KnNode nextNode,Long nextApproverId){
        KnHistoryApprove approveHis=new KnHistoryApprove();
        approveHis.setTempId(currentApprove.getTempId());
        approveHis.setApplyId(currentApprove.getApplyId());
        approveHis.setApproveId(approverId);
        approveHis.setApproveOpinion(null);
        approveHis.setIsNormalAppr(nextApproverId.equals(approverId)?IdEntity.ActiveType.ENABLE:IdEntity.ActiveType.DISABLE);
        approveHis.setRole(KnHistoryApprove.ApproveRole.APPROVER);
        approveHis.setSubmitTime(null);
        approveHis.setType(nextNode.getType());
        approveHis.setNodeId(nextNode.getId());
        approveHis.setApproveStatus(KnHistoryApprove.ApproveStatus.APPROVEING);
        approveHis.setIsAgin(IdEntity.ActiveType.DISABLE);
        approveHis.setIsHasView(IdEntity.ActiveType.DISABLE);
        return approveHis;
    }
    /**
     * 更新审批历史表中的状态和审批意见
     *
     * @param opinion
     * @param nodeId
     * @param applyId
     * @param status
     */
    private void UpdateHisAppr(Long nodeId,Long applyId,String opinion,KnHistoryApprove.ApproveStatus status){
        //往审批历史表中插入对应记录,需要修改一条历史记录和插入一条新的记录，此处先修改审批表历史信息
        List<KnHistoryApprove> historyApproves=historyApproveDao.queryNoApproveApplyId(applyId);
        if(!Utils.isEmpityCollection(historyApproves)){
            KnHistoryApprove historyApprove = historyApproves.get(0);
            if(historyApprove!=null&&historyApprove.getId()>0){
                historyApprove.setApproveStatus(status);
                historyApprove.setApproveOpinion(opinion);
                historyApprove.setSubmitTime(System.currentTimeMillis());
                historyApprove.setApproveId(Users.id());
                historyApprove.setIsHasView(IdEntity.ActiveType.ENABLE);
                historyApproveDao.save(historyApprove);
            }else
                throw new RuntimeException("找不到历史审批记录nodeId:"+nodeId+",applyId:"+applyId);
        }
    }
    /**
     * 分页查询所有有效员工
     *
     * @param pageNo   页码
     * @param pageSize 数量
     * @param userName 用户名
     *
     * @return List<KnEmployee> 无数据时会返回一个size为0的list对象
     */
    public List<KnEmployee> ListAllEmployee(Integer pageNo,Integer pageSize,final String userName){
        List<KnEmployee> list=Lists.newArrayList();
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.ASC,"userName"));
        Page<KnEmployee> page=empDao.findAll(new Specification<KnEmployee>(){
            @Override public Predicate toPredicate(Root<KnEmployee> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<IdEntity.ActiveType>get("job"),IdEntity.ActiveType.ENABLE));
                if(StringUtils.isNotBlank(userName)){
                    expressions.add(cb.like(root.<String>get("userName"),"%"+userName.trim()+"%"));
                }
                return predicate;
            }
        },pageRequest);
        if(page!=null&&page.getContent()!=null){
            list=page.getContent();
        }
        return list;
    }
    /**
     * 分页查询所有部门(目前取系统中的组织)
     *
     * @param pageNo   页码
     * @param pageSize 数量
     * @param deptName 部门名称
     *
     * @return List<KnOrganization> 无数据时会返回一个size为0的list对象
     */
    public List<KnOrganization> ListAllDepartment(Integer pageNo,Integer pageSize,final String deptName){
        List<KnOrganization> list=Lists.newArrayList();
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.ASC,"name"));
        Page<KnOrganization> page=orgDao.findAll(new Specification<KnOrganization>(){
            @Override public Predicate toPredicate(Root<KnOrganization> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if(StringUtils.isNotBlank(deptName)){
                    expressions.add(cb.like(root.<String>get("name"),"%"+deptName.trim()+"%"));
                }
                return predicate;
            }
        },pageRequest);
        if(page!=null&&page.getContent()!=null){
            list=page.getContent();
        }
        return list;
    }
    /**
     * 根据ID获取模板对象
     * @param id 模板ID
     * @return KnTemplate 可能返回空对象
     */
    public KnTemplate ReadKnTemplate(Long id){
        return templateDao.findOne(id);
    }
    /**
     * 保存模板对象，并保存与之对应的节点集合，若节点的类型或审批人没有数据，将跳过该节点的保存
     * @param template 模板
     * @param nodeList 节点集合
     */
    @Transactional(readOnly=false)
    public void SaveKnTemplate(KnTemplate template,List<KnNodeDTO> nodeList){
        //删除该模板的节点
        if(template.getId()!=null&&template.getId()>0){
            RemoveNodeByTemplateId(template.getId());
            template.setRemoveTag(1);
            templateDao.save(template);
        }
        KnTemplate newTemplate = new KnTemplate();
        newTemplate = BeanMapper.map(template,KnTemplate.class);
        //保存模板对象
        newTemplate.setId(null);
        newTemplate.setRemoveTag(0);
        newTemplate=templateDao.save(newTemplate);
        long pid=0;
        for(KnNodeDTO dto : nodeList){
            KnNode node=new KnNode();
            if(StringUtils.isBlank(dto.getType())||dto.getApproveId()==null){
                continue;
            }
            node.setType(KnNode.NodeType.valueOf(dto.getType()));
            node.setApproveId(dto.getApproveId());
            node.setParentId(pid);
            node.setTempId(newTemplate.getId());
            node.setRemoveTag(0);
            nodeDao.save(node);
            pid=node.getId();
        }
    }
    /**
     * 逻辑删除模板下的所有节点
     * @param id 模板ID
     */
    @Transactional(readOnly=false)
    public void RemoveNodeByTemplateId(Long id){
        List<KnNode> nodes=ListKnNodeByTemplateId(id);
        if(nodes.size()>0){
            for(KnNode node : nodes){
                node.setRemoveTag(1);
            }
            nodeDao.save(nodes);
        }
    }
    /**
     * 根据模板ID获取其下所有节点
     * @param id 模板ID
     * @return List<KnNode> 无数据时会返回一个size为0的list对象
     */
    public List<KnNode> ListKnNodeByTemplateId(final Long id){
        List<KnNode> list=Lists.newArrayList();
        if(id==null||id<=0){
            return list;
        }
        list=nodeDao.findAll(new Specification<KnNode>(){
            @Override public Predicate toPredicate(Root<KnNode> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<Integer>get("tempId"),id));
                expressions.add(cb.equal(root.<Integer>get("removeTag"),0));
                return predicate;
            }
        });
        return list;
    }
    /**
     * 逻辑删除模板对象
     * @param id 模板ID
     */
    @Transactional(readOnly=false)
    public void RemoveTemplate(Long id){
        KnTemplate template=ReadKnTemplate(id);
        template.setRemoveTag(1);
        templateDao.save(template);
        //同时删除该模板下的所有节点
        RemoveNodeByTemplateId(id);
    }
    /**
     * 分页查询所有模板
     *
     * @param pageNo   页码
     * @param pageSize 数量
     * @param tempName 模板名称
     *
     * @return List<KnTemplate> 无数据时会返回一个size为0的list对象
     */
    public List<KnTemplate> ListKnTemplate(Integer pageNo,Integer pageSize,final String tempName){
        List<KnTemplate> list=Lists.newArrayList();
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.ASC,"title"));
        Page<KnTemplate> page=templateDao.findAll(new Specification<KnTemplate>(){
            @Override public Predicate toPredicate(Root<KnTemplate> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<Integer>get("removeTag"),0));
                if(StringUtils.isNotBlank(tempName)){
                    expressions.add(cb.like(root.<String>get("title"),"%"+tempName.trim()+"%"));
                }
                return predicate;
            }
        },pageRequest);
        if(page!=null&&page.getContent()!=null){
            list=page.getContent();
        }
        return list;
    }
    /**
     * 根据节点对象获取节点审批人名称(根据节点的类型来区分)
     * @param node 节点对象
     * @return 节点审批人名称(若类型为DEPARTMENT,返回对应的组织名称,若类型为USER,返回对应用户的名称) 对象不存在将返回空值
     */
    public String QueryApproveName(KnNode node){
        if(node==null){
            return null;
        }
        if("DEPARTMENT".equals(node.getType().toString())){
            KnOrganization organization=orgDao.findOne(node.getApproveId());
            return organization==null?null:organization.getName();
        }

        if("USER".equals(node.getType().toString())){
            KnEmployee employee=empDao.findOne(node.getApproveId());
            return employee==null?null:employee.getUserName();
        }

        if("SUPERIOR".equals(node.getType().toString())){
            KnEmployee employee=QuerySuperiorEmp(Users.id());
            return employee==null?null:employee.getUserName();
        }
        return null;
    }
    /**
     * 根据员工ID获取员工所属组织的主负责人
     * @param id 员工ID
     * @return KnEmployee
     */
    private KnEmployee QuerySuperiorEmp(Long id){
        KnOrganization organization=QueryOrgByEmpId(id);
        if(organization!=null){
            //根据岗位ID获取主负责人
            return QueryApproveByOrg(organization.getId());
        }
        return null;
    }
    /**
     * 根据员工ID获取员工所在组织
     * @param id 员工ID
     * @return KnOrganization
     */
    private KnOrganization QueryOrgByEmpId(Long id){
        List<KnEmployeeOrganization> employeeOrganizations=employeeOrganizationDao.findByIdEmpId(id);
        if(employeeOrganizations!=null&&employeeOrganizations.size()>0){
            KnEmployeeOrganization returnObj=employeeOrganizations.get(0);
            for(KnEmployeeOrganization employeeOrganization:employeeOrganizations){
                if(employeeOrganization.getMajor()==1){
                    returnObj=employeeOrganization;
                    break;
                }
            }
            return returnObj.getId().getOrg();
        }
        return null;
    }
    //==============陈超1==============
    /**
     * 保存附件对象
     * @param attach
     */
    @Transactional(readOnly=false)
    public void SaveFile(KnWorkflowAttach attach){
        workflowAttachDao.save(attach);
    }
    /**
     * 组装流程动态对象列表
     * @param p
     * @param s
     * @return
     */
    public List<KnDynamicDTO> BuildKnDynamicDTO(int p,int s){
        //先查询出所有历史信息，遍历，根据日期，分组
        PageRequest pageRequest=new PageRequest(p,s);
        Page<KnHistoryApprove> page=historyApproveDao.queryHistoryApproveByUserid(Users.id(),pageRequest);
        List<KnHistoryApprove> approves = page.getContent();
        List<KnDynamicDTO> dynamicDTOs = Lists.newArrayList();
        if(!Utils.isEmpityCollection(approves)){
            Map<String , List<KnHistoryApprove>> result = new HashMap();
            for(KnHistoryApprove app : approves){
                String createTime = new DateTime(app.getCreateTime()).toString("yyyy-MM-dd");
                List<KnHistoryApprove> tempApps = result.get(createTime);
                if( !Utils.isEmpityCollection(tempApps) ){
                    tempApps.add(app);
                }else{
                    tempApps = Lists.newArrayList();
                    tempApps.add(app);
                    result.put(createTime,tempApps);
                }
            }
            if(result.size()>0){
                List<KnHistoryApproveDTO> dtos = Lists.newArrayList();
                String nowDay = new DateTime(System.currentTimeMillis()).toString("yyyy-MM-dd");
                List<KnHistoryApprove> approves1 = Lists.newArrayList();
                for( String key : result.keySet() ){
                    KnDynamicDTO dto = new KnDynamicDTO();
                    dto.setDate(key.equals(nowDay)?"今天":key);
                    List<KnHistoryApproveDTO> dtoList = Lists.newArrayList();
                    approves1 = (List<KnHistoryApprove>)result.get(key);
                    if(!Utils.isEmpityCollection(approves1)){
                        for(KnHistoryApprove approve : approves1){
                            KnHistoryApproveDTO dto1 = BeanMapper.map(approve,KnHistoryApproveDTO.class);
                            dto1.setApproveTime(approve.getSubmitTime()!=null?new DateTime(approve.getSubmitTime()).toString("yyyy-MM-dd HH:mm"):"");
                            dto1.setCreateTime(new DateTime(approve.getCreateTime()).toString("yyyy-MM-dd HH:mm"));
                            KnEmployee emp = empDao.findOne(approve.getApproveId());
                            dto1.setApproveName(emp!=null?emp.getUserName():null);
                            KnHistoryApprove starter = historyApproveDao.queryStarter(approve.getApplyId()).get(0);
                            KnEmployee empStart = empDao.findOne(starter.getApproveId());
                            dto1.setStarterName(empStart!=null?empStart.getUserName():null);
                            dto1.setStarerId(empStart!=null?empStart.getId():null);
                            KnTemplate template = templateDao.findOne(approve.getTempId());
                            dto1.setTemplateName(template.getTitle());
                            dtoList.add(dto1);
                        }
                    }
                    dto.setHists(dtoList);
                    dynamicDTOs.add(dto);
                }
            }
        }
        return dynamicDTOs;
    }
    /**
     * 审批，包括同意并结束，同意并制定下一审批人，拒绝
     * @return
     */
    @Transactional(readOnly=false)
    public int Approve(String detail){
//    public int Approve(KnHistoryApproveDTO dto){
        KnHistoryApproveDTO dto=JsonMapper.nonDefaultMapper().fromJson(detail,KnHistoryApproveDTO.class);
        if(dto==null){
            //返回错误信息
            return 1;
        }
        if( dto.getType().equals(WorkflowConfig.AGREE_AND_OVER) || dto.getType().equals(WorkflowConfig.AGREE_AND_NEXT)){
            //同意
            Push(dto.getApplyId(),dto.getApproveId(),dto.getType(),dto.getApproveOpinion());
        }else{
            //拒绝
            Fallback(dto.getApplyId(),dto.getApproveOpinion());
        }

        //处理附件
        if(dto.getFiles()!=null&&!dto.getFiles().equals("[]")){
            KnWorkflowAttach[] files=JsonMapper.nonDefaultMapper().fromJson(dto.getFiles(),KnWorkflowAttach[].class);
            for(KnWorkflowAttach file : files){
                file.setBusType(KnWorkflowAttach.BusType.APPROVE);
                file.settId(dto.getId());
                SaveFile(file);
            }
        }
        return 0;
    }
    //==============陈超2==============

    /**
     * 根据用户ID获取该用户提交对应模板申请的次数
     * @param tempId 模板ID
     * @param userId 用户ID
     * @return Integer 无数据返回0
     */
    public Integer CountKnApply(final Long tempId,final Long userId){
        return (int)applyDao.count(new Specification<KnApply>(){
            @Override public Predicate toPredicate(Root<KnApply> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                long tid=tempId==null?0:tempId;
                long uid=userId==null?0:userId;
                expressions.add(cb.equal(root.<Integer>get("removeTag"),0));
                expressions.add(cb.equal(root.<Long>get("tempId"),tid));
                expressions.add(cb.equal(root.<Long>get("userId"),uid));
                return predicate;
            }
        });
    }
    /**
     * 根据ID获取申请对象
     * @param id ID
     * @return KnApply
     */
    public KnApply ReadKnApply(Long id){
        return applyDao.findOne(id);
    }
    /**
     * 删除工作流附件
     * @param tid 关联表ID
     * @param type 关联表类型(apply,表示申请表)
     */
    public void RemoveWorkflowAttach(Long tid,KnWorkflowAttach.BusType type){
        List<KnWorkflowAttach> attaches=ListKnWorkflowAttach(tid,type);
        workflowAttachDao.delete(attaches);
    }
    /**
     * 根据关联表ID,关联表类型获取工作流附件列表
     * @param tid 关联表ID
     * @param type 关联表类型(apply,表示申请表)
     * @return List<KnWorkflowAttach>
     */
    public List<KnWorkflowAttach> ListKnWorkflowAttach(final Long tid,final KnWorkflowAttach.BusType type){
        return workflowAttachDao.findAll(new Specification<KnWorkflowAttach>(){
            @Override public Predicate toPredicate(Root<KnWorkflowAttach> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<Long>get("tId"),tid));
                expressions.add(cb.equal(root.<KnWorkflowAttach.BusType>get("busType"),type));
                return predicate;
            }
        });
    }
    /**
     * 保存申请对象
     * @param apply 申请对象
     */
    @Transactional(readOnly=false)
    public void SaveKnApply(KnApply apply){
        applyDao.save(apply);
    }
    /**
     * 保存工作流附件
     * @param attaches 附件集合
     */
    @Transactional(readOnly=false)
    public void SaveWorkflowAttach(List<KnWorkflowAttach> attaches){
        workflowAttachDao.save(attaches);
    }
    /**
     * 根据节点对象获取该节点对应审批用户对象
     * @param node 审批节点
     * @return KnEmployee
     */
    public KnEmployee QueryApproveEmployee(KnNode node) throws Exception{
        if(node==null||node.getType()==null||node.getApproveId()==null){
            throw new Exception("节点异常!");
        }
        if("USER".equals(node.getType().toString())){
            return empDao.findOne(node.getApproveId());
        }
        if("DEPARTMENT".equals(node.getType().toString())){
            return QueryApproveByOrg(node.getApproveId());
        }
        if("SUPERIOR".equals(node.getType().toString())){
            return QuerySuperiorEmp(Users.id());
        }

        return null;
    }
    /**
     * 根据组织ID获取主负责人
     * @param orgId 主负责人
     * @return KnEmployee
     */
    public KnEmployee QueryApproveByOrg(final Long orgId){
        List<KnEmployeeOrganization> employeeOrganizations=employeeOrganizationDao.findAll(new Specification<KnEmployeeOrganization>(){
            @Override public Predicate toPredicate(Root<KnEmployeeOrganization> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                expressions.add(cb.equal(root.<KnEmployeeOrganizationId>get("id").<KnOrganization>get("org").<Long>get("id"),orgId));
                expressions.add(cb.equal(root.<Integer>get("charge"),1));//主负责人
                return predicate;
            }
        },new Sort(Sort.Direction.ASC,"id.emp"));
        if(employeeOrganizations!=null&&employeeOrganizations.size()>0){
            return employeeOrganizations.get(0).getId().getEmp();
        }
        return null;
    }
    /**
     * 获取申请记录列表
     * @param pageNo 页面
     * @param pageSize 数量
     * @param type 类型(apply--申请列表,approve--待审批列表,history--已审批列表)
     * @param search 查询条件
     * @return List<KnApply> ListKnApply
     */
    public List<KnApply> ListKnApply(final Integer pageNo,final Integer pageSize,final String type,final String search){
        List<KnApply> list=Lists.newArrayList();
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.DESC,"createTime"));
        Page<KnApply> page=applyDao.findAll(new Specification<KnApply>(){
            @Override public Predicate toPredicate(Root<KnApply> root,CriteriaQuery<?> query,CriteriaBuilder cb){
                Predicate predicate=cb.conjunction();
                List<Expression<Boolean>> expressions=predicate.getExpressions();
                if("apply".equals(type)){
                    expressions.add(cb.equal(root.<Long>get("userId"),Users.id()));
                }else{
                    List<Long> applyIds=QueryApplyIdsByType(type,pageNo,pageSize);
                    if(applyIds.size()<=0){
                        applyIds.add(0l);
                    }
                    expressions.add(cb.and(root.<Long>get("id").in(applyIds)));
                }
                if(StringUtils.isNotBlank(search)){//查询条件
                    List<Long> tempIds=templateDao.listTemplateIdsByName("%"+search+"%");
                    if(tempIds.size()<=0){
                        tempIds.add(0l);
                    }
                    Predicate p=cb.or(cb.like(root.<String>get("description"),"%"+search+"%"),root.<Long>get("tempId").in(tempIds));
                    //TODO:时间
                    expressions.add(cb.and(p));
                }
                return predicate;
            }
        },pageRequest);
        if(page!=null&&page.getContent()!=null){
            list=page.getContent();
        }
        return list;
    }
    private List<Long> QueryApplyIdsByType(String type,Integer pageNo,Integer pageSize){
        List<Long> list=Lists.newArrayList();
        if("approve".equals(type)){
            PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.ASC,"createTime"));
            list=currentApproveDao.pageCurrentApproveIds(Users.id(),pageRequest);
        }
        if("history".equals(type)){
            PageRequest pageRequest=new PageRequest(pageNo,pageSize,new Sort(Sort.Direction.DESC,"applyId"));
            list=historyApproveDao.pageHistoryApproveIds(Users.id(),pageRequest);
        }
        return list;
    }
    /**
     * 批量修改待审批列表为已阅
     * @param list 待审批申请列表
     */
    @Transactional(readOnly=false)
    public void ChangeApproveHasView(List<KnApply> list){
        if(list!=null&&list.size()>0){
            List<Long> applyIds=Lists.newArrayList();
            for(KnApply apply:list){
                applyIds.add(apply.getId());
            }
            List<KnCurrentApprove> currentApproves=currentApproveDao.listCurrentApproveByNoView(applyIds);
            if(currentApproves!=null&&currentApproves.size()>0){
                for(KnCurrentApprove currentApprove:currentApproves){
                    currentApprove.setIsHasView(IdEntity.ActiveType.ENABLE);
                }
                currentApproveDao.save(currentApproves);
            }
        }
    }
    /**
     * 获取用户对象
     * @param userId 用户ID
     * @return KnEmployee
     */
    public KnEmployee ReadKnEmployee(Long userId){
        return empDao.findOne(userId);
    }
    /**
     * 根据申请Id获取待审批对象
     * @param applyId 申请ID
     * @return KnCurrentApprove
     */
    public KnCurrentApprove ReadCurrentApprove(Long applyId){
        return currentApproveDao.findOneByApplyId(applyId);
    }
    /**
     * 根据申请ID获取历史审批记录
     * @param applyId 申请记录
     * @return List<KnHistoryApprove>
     */
    public List<KnHistoryApprove> ListKnHistoryApprove(Long applyId){
        return historyApproveDao.queryHistoryApprove(applyId);
    }
    /**
     * 保存申请对象并发起工作流
     * @param applyDTO 申请对象
     */
    @Transactional(readOnly=false)
    public void SaveKnApplyAndPush(KnApplyDTO applyDTO){
        KnApply apply=new KnApply();
        if(applyDTO.getId()!=null&&applyDTO.getId()>0){
            apply=ReadKnApply(applyDTO.getId());
            //删除附件
            RemoveWorkflowAttach(applyDTO.getId(),KnWorkflowAttach.BusType.APPLY);
        }
        DateTimeFormatter format=DateTimeFormat.forPattern("yyyy-MM-dd");
        //申请人
        apply.setUserId(Users.id());
        apply.setTempId(applyDTO.getTempId());
        //开始时间
        if(StringUtils.isNotBlank(applyDTO.getStartTime())){
            apply.setStartTime(DateTime.parse(applyDTO.getStartTime(),format).getMillis());
        }
        //结束时间
        if(StringUtils.isNotBlank(applyDTO.getEndTime())){
            apply.setEndTime(DateTime.parse(applyDTO.getEndTime(),format).getMillis());
        }
        //是否半天
        if(StringUtils.isNotBlank(applyDTO.getIsHalfDay())&&"true".equals(applyDTO.getIsHalfDay().trim().toLowerCase())){
            apply.setIsHalfDay(IdEntity.ActiveType.ENABLE);
        }
        //描述
        if(StringUtils.isNotBlank(applyDTO.getDescription())){
            apply.setDescription(applyDTO.getDescription());
        }
        //其他
        if(StringUtils.isNotBlank(applyDTO.getOther())){
            apply.setOther(applyDTO.getOther());
        }
        SaveKnApply(apply);
        //附件
        if(StringUtils.isNotBlank(applyDTO.getFiles())){
            List<KnWorkflowAttach> attaches=JsonMapper.nonDefaultMapper().fromJson(applyDTO.getFiles(),new TypeReference<List<KnWorkflowAttach>>(){
            });
            if(attaches!=null&&attaches.size()>0){
                for(KnWorkflowAttach attach : attaches){
                    attach.settId(apply.getId());
                    attach.setBusType(KnWorkflowAttach.BusType.APPLY);
                }
                SaveWorkflowAttach(attaches);
            }
        }
        //发起工作流
        PushFirst(apply.getTempId(),apply.getId(),applyDTO.getCurrApproveId());
    }


    public static void main(String[] args){
        //"files":"[{sha:../image/abc.jpg,name:zz,fileType:image/png},{sha:../image/abc.jpg,name:zz,fileType:image/png}]"
        String str = "[{\"name\":\"zz\"}]";
        KnWorkflowAttach[] files=JsonMapper.nonDefaultMapper().fromJson(str,KnWorkflowAttach[].class);

    }
}
