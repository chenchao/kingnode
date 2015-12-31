package com.kingnode.regulation.reset;
import com.kingnode.regulation.dto.ReturnListDTO;
import com.kingnode.regulation.entity.KnClassification;
import com.kingnode.regulation.entity.KnRegulationFile;
import com.kingnode.regulation.service.RegulationService;
import com.kingnode.xsimple.rest.DetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
@RestController @RequestMapping({"/api/v1/reguation","/api/secure/v1/library"})
public class KnRegulationRestController{
    @Autowired
    private RegulationService rs;
    /**
     * 获取文件分类列表
     *
     * @param pageNo         分页页码
     * @param pageSize       分页数量大小
     * @param classification 分类名称纸质模糊搜索
     *
     * @return
     */
    @RequestMapping(value="list-classification")
    public ReturnListDTO<KnClassification> listKnClassification(@RequestParam(value="p") Integer pageNo,@RequestParam(value="s") Integer pageSize,@RequestParam(value="classification") String classification){
        return rs.PageListClassification(pageNo,pageSize,classification);
    }
    /**
     * 获取文件列表
     *
     * @param pageNo      分页页码
     * @param pageSize    分页数量大小
     * @param id
     * @param profileName 资料名称纸质模糊搜索
     *
     * @return
     */
    @RequestMapping(value="list-profile")
    public ReturnListDTO<KnRegulationFile> listKnRegulationFile(@RequestParam(value="p") Integer pageNo,@RequestParam(value="s") Integer pageSize,@RequestParam(value="id") Long id,@RequestParam(value="name") String profileName){
        return rs.PageListFile(pageNo,pageSize,id,profileName);
    }
    /**
     * 获取文件列表，全局查询
     * @param pageNo      分页页码
     * @param pageSize    分页数量大小
     * @param profileName  资料名称纸质模糊搜索
     */
    @RequestMapping(value="list")
    public ReturnListDTO<KnRegulationFile> ReadKnRegulationFile(@RequestParam(value="p") Integer pageNo,@RequestParam(value="s") Integer pageSize,@RequestParam(value="name") String profileName){
        return rs.PageListFile(pageNo,pageSize,profileName);
    }
    /**
     * 增加资料的阅读次数
     *
     * @param id 资料id
     *
     * @return 返回资料的详情
     */
    @RequestMapping(value="add-count")
    public DetailDTO<KnRegulationFile> addReadCount(@RequestParam(value="id") Long id){
        return rs.AddReadCount(id);
    }
}
