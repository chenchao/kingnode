package com.kingnode.affairs.reset;
import com.kingnode.affairs.dto.ReturnListDTO;
import com.kingnode.affairs.entity.KnAffairsPerson;
import com.kingnode.affairs.entity.KnDepartment;
import com.kingnode.affairs.service.AffairsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/affairs","/api/secure/v1/affairs"})
public class KnAffairsRestController{
    @Autowired
    private AffairsService as;
    /**
     * 获取接口人的部门
     *
     * @param pageNo   分页页码
     * @param pageSize 分页数量大小
     * @param dname    部门名称,纸质模糊搜索
     *
     * @return
     */
    @RequestMapping(value="list-department")
    public ReturnListDTO<KnDepartment> listKnClassification(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize,@RequestParam(value="dname") String dname){
        return as.PageKnDepartment(pageNo,pageSize,dname);
    }
    /**
     * 获取文件列表
     *
     * @param pageNo   分页页码
     * @param pageSize 分页数量大小
     * @param id       部门id
     * @param name     对应员工的用户或者登陆名,支持模糊搜索
     *
     * @return
     */
    @RequestMapping(value="list-affairs-person")
    public ReturnListDTO<KnAffairsPerson> listKnRegulationFile(@RequestParam(value="p",defaultValue="0") Integer pageNo,@RequestParam(value="s",defaultValue="10") Integer pageSize,@RequestParam(value="id") Long id,@RequestParam(value="name",defaultValue="") String name){
        ReturnListDTO<KnAffairsPerson> dt=as.PageKnAffairsPerson(pageNo,pageSize,id,name);
        dt.setList(dt.getList());
        return dt;
    }
}
