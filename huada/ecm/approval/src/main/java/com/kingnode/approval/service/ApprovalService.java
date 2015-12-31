package com.kingnode.approval.service;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kingnode.approval.dao.KnApprovalDao;
import com.kingnode.approval.dao.KnApprovalFileDao;
import com.kingnode.approval.dao.KnApprovalProcessDao;
import com.kingnode.approval.dao.KnApprovalVariablesDao;
import com.kingnode.approval.dao.KnFlowDao;
import com.kingnode.approval.dao.KnFlowFormDao;
import com.kingnode.approval.entity.KnApproval;
import com.kingnode.approval.entity.KnApprovalFile;
import com.kingnode.approval.entity.KnApprovalProcess;
import com.kingnode.approval.entity.KnApprovalVariables;
import com.kingnode.approval.entity.KnFlow;
import com.kingnode.approval.entity.KnFlowForm;
import com.kingnode.xsimple.dao.system.KnEmployeeOrganizationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@Component @Transactional(readOnly=true) public class ApprovalService{
    private KnApprovalDao approvalDao;
    private KnFlowDao flowDao;
    private KnFlowFormDao flowFormDao;
    private KnApprovalProcessDao processDao;
    private KnApprovalVariablesDao variablesDao;
    private KnApprovalFileDao fileDao;
    private KnEmployeeOrganizationDao empOrgDao;
    public List<Object[]> ReadApprovalCount(Long userId){
        return approvalDao.findApprovalCount(userId);
    }
    public KnApproval ReadKnApproval(Long id){
        return approvalDao.findOne(id);
    }
    public KnFlowForm ReadKnFlowForm(Long id){
        return flowFormDao.findOne(id);
    }
    public List<KnApproval> ReadKnApprovalByUser(Long userId,Integer pageNo,Integer pageSize){
        return approvalDao.findByUserId(userId,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnApproval> ReadKnApprovalByUserSearch(Long userId,String content,Integer pageNo,Integer pageSize){
        return approvalDao.findByUserIdAndContentLike(userId,content,new PageRequest(pageNo,pageSize)).getContent();
    }
    public List<KnApprovalFile> ReadUserFile(Long userId,List<KnApprovalFile.FileType> fileTypes){
        return fileDao.findKnApprovalFile(userId,fileTypes);
    }
    @Transactional(readOnly=false) public int UpdateKnApprovalProcessForNodeType(Long id){
        return processDao.setResultType(id);
    }
    @Transactional(readOnly=false) public int UpdateKnApprovalForNodeType(Long id,KnApproval.NodeType nodeType){
        return approvalDao.setNodeType(id,nodeType);
    }
    @Transactional(readOnly=false) public KnApproval SaveKnApproval(KnApproval approval){
        return approvalDao.save(approval);
    }
    @Transactional(readOnly=false) public KnApprovalVariables SaveKnApprovalVariables(KnApprovalVariables approvalVariables){
        return variablesDao.save(approvalVariables);
    }
    @Transactional(readOnly=false) public KnApprovalFile SaveKnApprovalFile(KnApprovalFile file){
        return fileDao.save(file);
    }
    @Transactional(readOnly=false) public KnFlow SaveKnFlow(KnFlow flow){
        return flowDao.save(flow);
    }
    @Transactional(readOnly=false) public KnApprovalProcess SaveKnApprovalProcess(KnApprovalProcess process){
        return processDao.save(process);
    }
    @Autowired public void setApprovalDao(KnApprovalDao approvalDao){
        this.approvalDao=approvalDao;
    }
    @Autowired public void setFlowFormDao(KnFlowFormDao flowFormDao){
        this.flowFormDao=flowFormDao;
    }
    @Autowired public void setProcessDao(KnApprovalProcessDao processDao){
        this.processDao=processDao;
    }
    @Autowired public void setVariablesDao(KnApprovalVariablesDao variablesDao){
        this.variablesDao=variablesDao;
    }
    @Autowired public void setFileDao(KnApprovalFileDao fileDao){
        this.fileDao=fileDao;
    }
    @Autowired public void setFlowDao(KnFlowDao flowDao){
        this.flowDao=flowDao;
    }
    @Autowired public void setEmpOrgDao(KnEmployeeOrganizationDao empOrgDao){
        this.empOrgDao=empOrgDao;
    }
    /**
     * 解析字符串拼装文件类型List集合
     *
     * @param type 文件类型 例如 EXCEL 如果多个文件类型，使用逗号隔开(EXCEL,WORD)
     *
     * @return 文件类型集合List<KnApprovalFile.FileType>
     */
    public List<KnApprovalFile.FileType> getFileTypes(String type){
        List<KnApprovalFile.FileType> fileTypes=Lists.newArrayList();
        if(!Strings.isNullOrEmpty(type)){
            String[] fileTypesStr=type.split(",");
            for(String fileType : fileTypesStr){
                switch(fileType){
                case "TEXT":
                    fileTypes.add(KnApprovalFile.FileType.TEXT);
                    break;
                case "WORD":
                    fileTypes.add(KnApprovalFile.FileType.WORD);
                    break;
                case "EXCEL":
                    fileTypes.add(KnApprovalFile.FileType.EXCEL);
                    break;
                case "PPT":
                    fileTypes.add(KnApprovalFile.FileType.PPT);
                    break;
                case "PNG":
                    fileTypes.add(KnApprovalFile.FileType.PNG);
                    break;
                case "JPG":
                    fileTypes.add(KnApprovalFile.FileType.JPG);
                    break;
                case "AUDIO":
                    fileTypes.add(KnApprovalFile.FileType.AUDIO);
                    break;
                case "VIDEO":
                    fileTypes.add(KnApprovalFile.FileType.VIDEO);
                    break;
                }
            }
        }
        return fileTypes;
    }
}
