package com.kingnode.xsimple.rest;
import java.util.List;

import com.google.common.collect.Lists;
import com.kingnode.diva.mapper.BeanMapper;
import com.kingnode.xsimple.dto.FullEmployeeDTO;
import com.kingnode.xsimple.dto.OrganizationDTO;
import com.kingnode.xsimple.dto.SimpleEmployeeDTO;
import com.kingnode.xsimple.entity.IdEntity;
import com.kingnode.xsimple.entity.system.KnEmployee;
import com.kingnode.xsimple.entity.system.KnOrganization;
import com.kingnode.xsimple.service.system.OrganizationService;
import com.kingnode.xsimple.util.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 组织-员工rest接口
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/org"})
public class OrganizationRestController{
    @Autowired
    private OrganizationService os;
    /**
     * 获取当前登录用户所在组织的员工信息-所有员工
     * 根据员工姓名获取员工列表
     */
    @RequestMapping(value="employee",method=RequestMethod.GET)
    public ListDTO<SimpleEmployeeDTO> ListEmployee(){
        //TODO cici 华大--获取登录者同部门人员,去除离职的人员,只获取在职人员的信息,20150427
//        return new ListDTO<>(true,os.ListEmployeeOrganization(Users.id()));
        return ListEmployeeEnable();
    }
    /**
     * 获取当前登录用户所在组织的员工信息-仅在职员工
     * 根据员工姓名获取员工列表
     */
    @RequestMapping(value="employee-on",method=RequestMethod.GET)
    public ListDTO<SimpleEmployeeDTO> ListEmployeeEnable(){
        return new ListDTO<>(true,os.ListEmployeeEnableOrganization(Users.id()));
    }
    /**
     * 根据员工姓名获取员工列表-非模糊查询
     *
     * @param name 查询的员工姓名
     *
     * @return 员工集合
     */
    @RequestMapping(value="employee",method=RequestMethod.POST)
    public ListDTO<SimpleEmployeeDTO> ListEmployeeByUserName(@RequestParam(value="name") String name){
        return new ListDTO<>(true,os.ListEmployee(name));
    }
    /**
     * 根据员工姓名获取员工列表-模糊查询-所有员工
     *
     * @param name 查询的员工姓名
     *
     * @return 员工集合
     */
    @RequestMapping(value="employee/like",method=RequestMethod.POST)
    public ListDTO<FullEmployeeDTO> ListEmployeeLikeUserName(@RequestParam(value="name") String name){
        List<KnEmployee> emps=os.ListKnEmployeeLike(name);
        List<FullEmployeeDTO> list=Lists.newArrayList();
        for(KnEmployee e : emps){
            FullEmployeeDTO dto=BeanMapper.map(e,FullEmployeeDTO.class);
            list.add(dto);
        }
        ListDTO<FullEmployeeDTO> listDTO=new ListDTO<>(true,list);
        if(list==null||list.size()<=0){
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
    /**
     * 根据员工姓名获取员工列表-模糊查询-仅在职员工
     *
     * @param name 查询的员工姓名
     *
     * @return 在职员工集合
     */
    @RequestMapping(value="employee-on/like",method=RequestMethod.POST)
    public ListDTO<FullEmployeeDTO> ListEmployeeEnableLikeUserName(@RequestParam(value="name") String name){
        List<KnEmployee> emps=os.ListKnEmployeeEnableLike(name);
        List<FullEmployeeDTO> list=Lists.newArrayList();
        for(KnEmployee e : emps){
            FullEmployeeDTO dto=BeanMapper.map(e,FullEmployeeDTO.class);
            list.add(dto);
        }
        ListDTO<FullEmployeeDTO> listDTO=new ListDTO<>(true,list);
        if(list==null||list.size()<=0){
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
    /**
     * 根据员工的主键id获取员工的详情
     */
    @RequestMapping(value="employee/{empId}",method=RequestMethod.GET)
    public DetailDTO<FullEmployeeDTO> ReadEmployee(@PathVariable(value="empId") Long empId){
        return new DetailDTO<>(true,os.ReadEmployee(empId));
    }
    /**
     * 根据组织的主键id获取该组织的下级组织信息,如获取第一级supId传入0
     *
     * @param supId 组织主键id
     *
     * @return 组织集合列表
     */
    @RequestMapping(value="org/{supId}",method=RequestMethod.GET)
    public ListDTO<OrganizationDTO> ListOrganization(@PathVariable(value="supId") Long supId){
        //TODO cici 华大修改成只是显示可用状态的部门信息 20150427
        return new ListDTO<>(true,os.ListKnOrganization(supId,IdEntity.ActiveType.ENABLE));
    }
    /**
     * 根据组织的主键id获取组织底下的人员信息列表-所有人员
     *
     * @param orgId 组织的主键id
     *
     * @return 员工集合列表
     */
    @RequestMapping(value="org/emp/{orgId}",method=RequestMethod.GET)
    public ListDTO<SimpleEmployeeDTO> ListEmployeeByOrgId(@PathVariable(value="orgId") Long orgId){
        //TODO cici 修改成获取在职人员的列表信息
        //        return new ListDTO<>(true,os.ListEmployeeByOrgId(orgId));
        return ListEmployeeEnableByOrgId(orgId);
    }
    /**
     * 根据组织的主键id获取组织底下的人员信息列表-仅在职人员
     *
     * @param orgId 组织的主键id
     *
     * @return 在职员工集合列表
     */
    @RequestMapping(value="org/emp-on/{orgId}",method=RequestMethod.GET)
    public ListDTO<SimpleEmployeeDTO> ListEmployeeEnableByOrgId(@PathVariable(value="orgId") Long orgId){
        return new ListDTO<>(true,os.ListEmployeeEnableByOrgId(orgId));
    }
    /**
     * 根据组织的主键id获取组织底下的人员和组织底下的子组织的员工信息列表
     *
     * @param orgId 组织的主键id
     *
     * @return 员工集合列表
     */
    @RequestMapping(value="org/emp-all/{orgId}",method=RequestMethod.GET)
    public ListDTO<FullEmployeeDTO> ListAllEmployeeByOrgId(@PathVariable(value="orgId") Long orgId){
        List<KnEmployee> emps=os.ListAllEmployeeByOrgId(orgId);
        List<FullEmployeeDTO> list=Lists.newArrayList();
        for(KnEmployee e : emps){
            FullEmployeeDTO dto=BeanMapper.map(e,FullEmployeeDTO.class);
            list.add(dto);
        }
        ListDTO<FullEmployeeDTO> listDTO=new ListDTO<>(true,list);
        if(list==null||list.size()<=0){
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
    /**
     * 根据组织的主键id获取组织底下的人员和组织底下的子组织的员工信息列表-仅在职员工
     *
     * @param orgId 组织的主键id
     *
     * @return 在职员工集合列表
     */
    @RequestMapping(value="org/emp-all-on/{orgId}",method=RequestMethod.GET)
    public ListDTO<FullEmployeeDTO> ListAllEmployeeEnableByOrgId(@PathVariable(value="orgId") Long orgId){
        List<KnEmployee> emps=os.ListAllEmployeeByOrgId(orgId);
        List<FullEmployeeDTO> list=Lists.newArrayList();
        for(KnEmployee e : emps){
            //在职员工才加入
            if(IdEntity.ActiveType.ENABLE.equals(e.getJob())){
                FullEmployeeDTO dto=BeanMapper.map(e,FullEmployeeDTO.class);
                list.add(dto);
            }
        }
        ListDTO<FullEmployeeDTO> listDTO=new ListDTO<>(true,list);
        if(list==null||list.size()<=0){
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
    /**
     * 获取除登录人员得所有员工
     *
     * @param pageNo   页码
     * @param pageSize 每页显示数
     */
    @RequestMapping(value="/employee-list",method={RequestMethod.GET})
    public ListDTO<FullEmployeeDTO> ReadEmployeeNot(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="5") Integer pageSize){
        List<KnEmployee> emps=os.PageEmployeeNot(Users.id(),pageNo,pageSize);
        List<FullEmployeeDTO> list=Lists.newArrayList();
        for(KnEmployee e : emps){
            FullEmployeeDTO dto=BeanMapper.map(e,FullEmployeeDTO.class);
            list.add(dto);
        }
        ListDTO<FullEmployeeDTO> listDTO=new ListDTO<>(true,list);
        if(list==null||list.size()<=0){
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
    /**
     * 获取所有部门
     *
     * @param pageNo   页码
     * @param pageSize 每页显示数
     */
    @RequestMapping(value="/org-list",method={RequestMethod.GET})
    public ListDTO<OrganizationDTO> ReadOrg(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="100") Integer pageSize){
        List<KnOrganization> orgs=os.PageKnOrganization(pageNo,pageSize);
        List<OrganizationDTO> list=Lists.newArrayList();
        for(KnOrganization o : orgs){
            OrganizationDTO dto=BeanMapper.map(o,OrganizationDTO.class);
            list.add(dto);
        }
        ListDTO<OrganizationDTO> listDTO=new ListDTO<>(true,list);
        if(list==null||list.size()<=0){
            listDTO.setErrorMessage("无数据");
        }
        return listDTO;
    }
}
