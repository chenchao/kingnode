package com.kingnode.xsimple.api.common;

import com.kingnode.xsimple.entity.system.KnTeam;
import com.kingnode.xsimple.entity.system.KnUser;

/**
 * Created by xushuangyong on 14-7-29.
 */
public class TeamBean {


    private long id;
    private String code = "";
    private String name = "";
    private String description = "";
    private String active;
    private Long empId = null;
    private String empName = "";
    private String loginName = "";

    public TeamBean(KnTeam team, KnUser user) {
        setId(team.getId());
        setCode(team.getCode());
        setName(team.getName());
        setDescription(team.getDescription());
        setActive(team.getActive().name());
        if (user != null) {
            setEmpId(user.getId());
            setEmpName(user.getName());
            setLoginName(user.getLoginName());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }


}
