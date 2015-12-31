<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>参会人员新增/编辑
    </title>
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
        <div class="portlet box green">
            <div class="portlet-title">
                <div class="caption">
                    <i class="fa fa-gift"></i>人员信息
                </div>
            </div>
            <div class="portlet-body form">
                <form action="${ctx}/meet/create" class="form-horizontal form-bordered" method="POST">
                    <input type="hidden" name="id" value="${registerInfo.id}"/>

                    <div class="form-body">

                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3">姓名
															<span class="required">
																 *
															</span>
                                    </label>

                                    <div class="col-md-9">
                                        <input type="text" class="form-control required" placeholder="姓名" name="name"
                                               value="${registerInfo.name}" check-type="required">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3">性别
                                    </label>

                                    <div class="col-md-9">
                                        <div class="radio-list">
                                            <label class="radio-inline">
                                                <span class="checked"><input type="radio" name="sex" checked="checked"
                                                                             value="男"></span>
                                                男</label>
                                            <label class="radio-inline">
                                                <span class=""><input type="radio" name="sex" value="女"></span>
                                                女</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3">电话<span class="required">
																			 *
																		</span></label>

                                    <div class="col-md-9">
                                        <input type="text" class="form-control required" name="phone"
                                               value="${registerInfo.phone}">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3">Email</label>

                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="email"
                                               value="${registerInfo.email}">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3">所属企业
                                    </label>

                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="company"
                                               value="${registerInfo.company}">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3">所属客户经理
                                    </label>

                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="cusmger"
                                               value="${registerInfo.cusmger}">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3">扩展字段1
                                    </label>

                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="ext1"
                                               value="${registerInfo.ext1}">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3">扩展字段2
                                    </label>

                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="ext2"
                                               value="${registerInfo.ext2}">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3">扩展字段3
                                    </label>

                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="ext3"
                                               value="${registerInfo.ext3}">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-actions fluid">
                            <div class="col-md-offset-3 col-md-9">
                                <button type="submit" class="btn green">保存</button>
                                <button type="button" class="btn default"
                                        onclick="window.location.href='${ctx}/meet/list'">取消
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
<content tag="script">
    <script type="text/javascript">
        <c:if test="${not empty registerInfo.sex}">
        var sex = "${registerInfo.sex}";
        if (sex == "男")
            $("input:radio[name='sex']").eq(0).attr("checked", 'checked');
        if (sex == "女")
            $("input:radio[name='sex']").eq(1).attr("checked", 'checked');
        </c:if>
    </script>
</content>
</html>
