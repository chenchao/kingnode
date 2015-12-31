<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>参会人员管理</title>
    <link rel="stylesheet" type="text/css" href="${ctx}/assets/global/plugins/select2/select2.css"/>
    <link rel="stylesheet" href="${ctx}/assets/global/plugins/data-tables/DT_bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/assets/global/plugins/bootstrap-datepicker/css/datepicker.css"/>
</head>
<body>
<div class="row">
    <div class="col-md-12">
        <ul class="page-breadcrumb breadcrumb">
            <li>
                <i class="fa fa-home"></i>
                <a href="${ctx}/">会议签到管理</a>
                <i class="fa fa-angle-right"></i>
            </li>
            <li>
                <a href="#">参会人员管理</a>
                <i class="fa fa-angle-right"></i>
            </li>
        </ul>
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        <div class="portlet box green-haze">
            <div class="portlet-title">
                <div class="caption"><i class="fa fa-cogs"></i>内容列表</div>
                <div class="actions">
                    <div class="btn-group">
                        <a href="${ctx}/meet/createForm" class="btn btn-sm btn-default">
                            <i class="fa fa-plus"></i>
                            <span class="hidden-480">新增人员</span>
                        </a>
                        <a href="${ctx}/news/article/create" class="btn btn-sm btn-default">
                            <i class="fa fa-plus"></i>
                            <span class="hidden-480">删除</span>
                        </a>
                        <a href="${ctx}/news/article/create" class="btn btn-sm btn-default">
                            <i class="fa fa-plus"></i>
                            <span class="hidden-480">导入会议</span>
                        </a>
                        <a class="btn btn-sm btn-default" href="#" data-toggle="dropdown">
                            <i class="fa fa-share"></i>
                            <span class="hidden-480">导出人员</span>
                            <i class="fa fa-angle-down"></i>
                        </a>
                        <ul class="dropdown-menu pull-right">
                            <li><a href="#">全部人员</a></li>
                            <li><a href="#">按会议</a></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="portlet-body">
                <c:if test="${not empty message}">
                    <div id="message" class="alert alert-success">
                        <button data-dismiss="alert" class="close">×</button>
                            ${message}
                    </div>
                </c:if>
                <div class="table-container">
                    <div class="table-actions-wrapper">
                        <select name="theme" id="theme" class="table-group-action-input form-control input-inline input-small input-sm">
                            <tags:meet listData="${listData}" choose="2"/>
                        </select>
                    </div>
                    <table class="table table-striped table-bordered table-hover" id="attendees_data_table">
                        <thead>
                        <tr role="row" class="heading">
                            <th width="5%"><input type="checkbox" class="group-checkable"></th>
                            <th width="10%">姓名</th>
                            <th width="5%">性别</th>
                            <th width="10%">电话</th>
                            <th width="15%">Email</th>
                            <th width="10%">来自公司</th>
                            <th width="10%">客户经理</th>
                            <th width="15%">操作</th>
                        </tr>
                        <tr role="row" class="filter">
                            <td>
                            </td>
                            <td><input type="text" class="form-control form-filter input-sm" name="search_LIKE_name"></td>
                            <td>
                                <select name="search_EQ_sex" class=" form-filter input-inline input-sm">
                                    <option value="">请选择</option>
                                    <option value="男">男</option>
                                    <option value="女">女</option>
                                </select>
                            </td>
                            <td><input type="text" class="form-control form-filter input-sm" name="search_LIKE_phone"></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td>
                                <button class="btn btn-sm yellow filter-submit margin-bottom"><i class="fa fa-search"></i> 搜索</button>
                                <button class="btn btn-sm red filter-cancel"><i class="fa fa-times"></i> 重置</button>
                            </td>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<content tag="script">
    <script type="text/javascript" src="${ctx}/assets/global/plugins/select2/select2.min.js"></script>
    <script type="text/javascript" src="${ctx}/assets/global/plugins/data-tables/jquery.dataTables.js"></script>
    <script type="text/javascript" src="${ctx}/assets/global/plugins/data-tables/DT_bootstrap.js"></script>
    <script type="text/javascript" src="${ctx}/assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
    <script src="${ctx}/assets/global/scripts/datatable.js"></script>
    <script type="text/javascript">
        function alertClose(){
            $(".alert").alert('close');
        }
        $(document).ready(function(){
            setInterval(alertClose,6000);
        });
        var grid=new Datatable();
        var $attendees_data_table=$("#attendees_data_table");
        grid.init({
            src:$attendees_data_table,
            onSuccess:function(grid){
                console.log(grid);
            },
            onError:function(grid){
            },
            dataTable:{
                "bServerSide":true,
                "sAjaxSource":"${ctx}/meet/attendees",
                "aaSorting":[
                    [ 1,"asc" ]
                ],
                "aoColumns":[
                    { "sTitle":"#","mData":"id","mRender":function(data,type){
                        return'<input type="checkbox" class="group-checkable" value="'+data+'">';
                    }},
                    {  "sTitle":"姓名","mData":"name"},
                    {  "sTitle":"性别","mData":"sex"},
                    {  "sTitle":"电话","mData":"phone"},
                    {  "sTitle":"Email","mData":"email"},
                    {  "sTitle":"来自公司","mData":"company"},
                    {  "sTitle":"客户经理","mData":"cusmger"},
                    {  "sTitle":"操作","mData":"id","sDefaultContent":"","mRender":function(data){
                        return'<a href="${ctx}/meet/updateForm/'+data+'" class="btn default btn-xs purple"><i class="fa fa-edit"></i>修改</a><a href="${ctx}/meet/delete/'+data+'" class="btn default btn-xs black"><i class="fa fa-trash-o"></i>删除</a>';
                    }}
                ]
            }
        });
        $('#theme').change(function(){
            selectAjax($("#theme").val());
        });
        function selectAjax(theme){
            grid.setAjaxParam("theme",theme);
            grid.getDataTable().fnDraw();
        }
    </script>
</content>
</html>
