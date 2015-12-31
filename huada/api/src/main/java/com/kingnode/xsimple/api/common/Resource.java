package com.kingnode.xsimple.api.common;
import java.util.List;

import com.google.common.collect.Lists;
import com.kingnode.xsimple.entity.system.KnResource;
/**
 * 菜单树型类
 *
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class Resource extends KnResource{
    private static final long serialVersionUID=-367546314286602922L;
    public List<Resource> children=Lists.newArrayList();
    public List<Resource> getChildren(){
        return children;
    }
    public void setChildren(List<Resource> children){
        this.children=children;
    }
}