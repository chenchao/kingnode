<%@ page import="com.kingnode.xsimple.api.common.Resource" %>
<%@ page import="com.kingnode.xsimple.service.system.ResourceService" %>
<%@ page import="com.kingnode.xsimple.util.Users" %>
<%@ page import="java.util.Collections" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!-- BEGIN SIDEBAR -->
<div class="page-sidebar-wrapper">
    <div class="page-sidebar navbar-collapse collapse">
        <!-- BEGIN SIDEBAR MENU -->
        <ul class="page-sidebar-menu" data-auto-scroll="false" data-auto-speed="200">
            <li class="sidebar-toggler-wrapper">
                <!-- BEGIN SIDEBAR TOGGLER BUTTON -->
                <div class="sidebar-toggler"></div>
                <!-- BEGIN SIDEBAR TOGGLER BUTTON -->
            </li>
            <li class="sidebar-search-wrapper hidden-xs">
                <form class="sidebar-search" action="#" method="POST">
                    <a href="javascript:;" class="remove"></a>

                    <div class="input-group">
                       <%-- <input type="text" class="form-control" placeholder="搜索..."><span class="input-group-btn"><input class="btn submit" type="button" type="button" value=" "/></span>--%>
                    </div>
                </form>
            </li>
            <% if(Users.ShiroUser()==null){
             %><script>location.href='${ctx}/login'</script><% return ;
            }%>
            <%
                org.springframework.context.ApplicationContext ctx = org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
                java.util.List<Resource> res=((ResourceService)ctx.getBean("resourceService")).CaseResource(Users.ShiroUser().id);
                String url=request.getServletPath();
                String path=request.getContextPath();
                if(res!=null){
                    Collections.sort(res);
                }
            %>
            <%
                for(Resource r : res){
                    if("/main".startsWith(r.getUrl())){%>
            <li class="start<%=r.getUrl().startsWith(url)?" active":""%>">
                <a href="<%=path+r.getUrl()%>"><i class="fa fa-home"></i><span class="title"><%=r.getName()%></span><span class='selected'></span></a>
            </li>
            <%
            }else{
                boolean t=url.startsWith(r.getUrl());
            %>
            <li<%=t?" class=\"active open\"":""%>>
                <a href="javascript:void(0);"><i class="<%=r.getIcon()%>"></i><span class="title"><%=r.getName()%></span><%if(t){%><span class='selected'></span><%}%><span class="arrow<%=t?" open":""%>"></span></a>
                    <%java.util.List<Resource> ch=r.getChildren();
        if(ch!=null&&ch.size()>0){Collections.sort(ch);%>
                <ul class='sub-menu'>
                    <%for(Resource r1 : ch){%>
                    <li<%=url.contains(r1.getUrl())?" class=\"active\"":""%>><a href='<%=path+r1.getUrl()%>'><i class='<%=r1.getIcon()%>'></i><%=r1.getName()%></a></li>
                    <%}%>
                </ul><%}%>
            </li><%}}%>
        </ul>
    </div>
</div>
<!-- END SIDEBAR -->