<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!-- BEGIN HEADER -->
<div class="page-header navbar navbar-fixed-top">
    <!-- BEGIN HEADER INNER -->
    <div class="page-header-inner">
        <!-- BEGIN LOGO -->
        <div class="page-logo">
            <a href="${ctx}/main">
                <img src="${ctx}/assets/admin/layout/img/logo.png" alt="logo" class="logo-default"/>
            </a>

            <div class="menu-toggler sidebar-toggler hide"></div>
        </div>
        <!-- END LOGO -->
        <!-- BEGIN RESPONSIVE MENU TOGGLER -->
        <div class="menu-toggler responsive-toggler" data-toggle="collapse" data-target=".navbar-collapse">
        </div>
        <!-- END RESPONSIVE MENU TOGGLER -->
        <!-- BEGIN TOP NAVIGATION MENU -->
        <div class="top-menu">
            <ul class="nav navbar-nav pull-right">
<%--                <!-- BEGIN NOTIFICATION DROPDOWN -->
                <li class="dropdown dropdown-extended dropdown-notification" id="header_notification_bar">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true"><i class="fa fa-warning"></i></a>
                    <ul class="dropdown-menu">
                        <li><p>你有0条新通知</p></li>
                        <li>
                            <ul class="dropdown-menu-list scroller" style="height: 250px;">
                            </ul>
                        </li>
                        <li class="external">
                            <a href="#">查看所有通知<i class="m-icon-swapright"></i></a>
                        </li>
                    </ul>
                </li>
                <!-- END NOTIFICATION DROPDOWN -->
                <!-- BEGIN INBOX DROPDOWN -->
                <li class="dropdown dropdown-extended dropdown-inbox" id="header_inbox_bar">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true"><i class="fa fa-envelope"></i></a>
                    <ul class="dropdown-menu">
                        <li><p>你有0条新信息</p></li>
                        <li>
                            <ul class="dropdown-menu-list scroller" style="height: 250px;">
                            </ul>
                        </li>
                        <li class="external"><a href="#">查阅所有信息<i class="m-icon-swapright"></i></a></li>
                    </ul>
                </li>
                <!-- END INBOX DROPDOWN -->
                <!-- BEGIN TODO DROPDOWN -->
                <li class="dropdown dropdown-extended dropdown-tasks" id="header_task_bar">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true"><i class="fa fa-tasks"></i></a>
                    <ul class="dropdown-menu extended tasks">
                        <li>
                            <p>你有0条待处理任务</p>
                        </li>
                        <li>
                            <ul class="dropdown-menu-list scroller" style="height: 250px;">
                            </ul>
                        </li>
                        <li class="external"><a href="#">查收所有任务<i class="m-icon-swapright"></i></a></li>
                    </ul>
                </li>
                <!-- END TODO DROPDOWN -->--%>
                <!-- BEGIN USER LOGIN DROPDOWN -->
                <!-- BEGIN USER LOGIN DROPDOWN -->
                <li class="dropdown dropdown-user">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                        <img alt="" class="img-circle" src="${ctx}/assets/admin/layout/img/avatar3_small.jpg"/>
                        <span class="username"><shiro:principal property="name"/></span>
                        <i class="fa fa-angle-down"></i>
                    </a>
                    <ul class="dropdown-menu">
                        <%--<li><a href="#"><i class="fa fa-user"></i>我的资料</a></li>
                        <li><a href="#"><i class="fa fa-calendar"></i>我的日程 </a></li>
                        <li><a href="#"><i class="fa fa-envelope"></i>我的信箱 </a></li>
                        <li><a href="#"><i class="fa fa-tasks"></i>我的任务 </a></li>
                        <li class="divider"></li>
                        <li><a href="#"><i class="fa fa-lock"></i>我要锁屏</a></li>--%>
                        <li><a href="${ctx}/logout"><i class="fa fa-key"></i>我要退出</a></li>
                    </ul>
                </li>
                <!-- END USER LOGIN DROPDOWN -->
                <!-- END USER LOGIN DROPDOWN -->
            </ul>
        </div>
        <!-- END TOP NAVIGATION MENU -->
    </div>
    <!-- END HEADER INNER -->
</div>
<!-- END HEADER -->