<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>测试页面</title>
    <!-- BEGIN PAGE LEVEL STYLES -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/assets/global/plugins/select2/select2.css"/>
    <link rel="stylesheet" href="${ctx}/assets/global/plugins/data-tables/DT_bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/assets/global/plugins/bootstrap-datepicker/css/datepicker.css"/>
    <link href="${ctx}/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="${ctx}/assets/global/plugins/select2/select2-metronic.css"/>
    <link href="${ctx}/assets/global/css/common/style-metronic.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/css/common/style.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/css/common/style-responsive.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/css/common/plugins.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/css/common/themes/default.css" rel="stylesheet" type="text/css" id="style_color"/>
    <link href="${ctx}/assets/global/css/common/custom.css" rel="stylesheet" type="text/css"/>

</head>
<body>
<div class="page-container">
    <div class="page-sidebar-wrapper">
        <div class="page-sidebar navbar-collapse collapse">
            <ul id="menuTree" class="page-sidebar-menu" data-auto-scroll="true" data-slide-speed="200">
                <li class="sidebar-toggler-wrapper">
                    <div class="sidebar-toggler hidden-phone">
                    </div>
                </li>
                <li class="active">
                    <a href="javascript:;">
                        <i class="fa fa-home"></i>
						<span class="title">
							接口测试
						</span>
						<span class="arrow ">
						</span>
                    </a>
                    <ul class="sub-menu">
                        <li class="active">
                            <a href="${ctx}/testInfo">
                                <i class="fa fa-bullhorn"></i>
                                前端接口
                            </a>
                        </li>
                    </ul>
                    <ul class="sub-menu">
                        <li class="active">
                            <a href="${ctx}/testInfo/dto">
                                <i class="fa fa-bullhorn"></i>
                                前端DTO接口
                            </a>
                        </li>
                    </ul>
                    <ul class="sub-menu">
                        <li>
                            <a href="${ctx}/testInfo/upload">
                                <i class="fa fa-bullhorn"></i>
                                附件接口
                            </a>
                        </li>
                    </ul>
                </li>

            </ul>
        </div>
    </div>
    <div class="page-content-wrapper">
        <div class="page-content">
            <div class="row">
                <div class="col-md-12">
                    <h3 class="page-title">
                        接口测试
                    </h3>
                </div>
            </div>
            <div class="row" id="editDataDiv">
                <div class="col-md-12">
                    <div class="tabbable tabbable-custom boxless tabbable-reversed">
                        <div class="tab-content">
                            <div class="portlet box green">
                                <div class="portlet-title">
                                    <div class="caption">
                                        <i class="fa fa-reorder"></i>接口
                                    </div>
                                    <div class="tools">
                                        <a href="javascript:void(0);" class="remove" onclick="closeForm()">
                                        </a>
                                    </div>
                                </div>
                                <div class="portlet-body form">
                                    <form enctype="multipart/form-data" class="form-horizontal" id="formData" type="post">
                                        <input type="hidden" id="fileName" name="fileName" value=""/>

                                        <div class="tab-content no-space">
                                            <div class="tab-pane active" id="tab_general">
                                                <div class="form-body">
                                                    <div class="form-group">
                                                        <label class="col-md-2 control-label">访问地址:
															<span class="required">
																 *
															</span>
                                                        </label>

                                                        <div class="col-md-10">
                                                            <input type="text" class="form-control" id="path" name="path" placeholder="要访问的action,例如：/client/ebs_updateEbsUserResponsiblity.action">
                                                            <span style="color:red;">如：/api/v1/application/info</span>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="col-md-2 control-label">请求参数:
																<span class="required">
																	 *
																</span>
                                                        </label>

                                                        <div class="col-md-10">
                                                            <textarea rows="3" class="form-control" id="jsonparm" name="jsonparm" placeholder="输入要传入参数,例如：{'pageNum':'1','pageSize':'20'}"></textarea>
                                                            <span style="color:red;">如：{"pageNum":"1","pageSize":"20"}</span>
                                                        </div>
                                                    </div>

                                                    <div class="form-group">
                                                        <label class="col-md-2 control-label">请求方式:
																<span class="required">
																	 *
																</span>
                                                        </label>

                                                        <div class="col-md-10">
                                                            <div class="radio-list">
                                                                <label class="radio-inline"><input type="radio" name="optionsRadios" value="POST" checked>POST</label>
                                                                <label class="radio-inline"><input type="radio" name="optionsRadios" value="GET"> GET</label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="form-group" style="margin-left: 0px; margin-right: 0px; margin-bottom: 1px; border-bottom-width: 0px;">
                                                        <button id="d_clip_button" type="button" class="btn default pull-right" id="copyResult" data-clipboard-target="resultJson">
                                                            <span>复制</span>
                                                        </button>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="col-md-2 control-label">返回结果:
															<span class="required">
																 *
															</span>
                                                        </label>

                                                        <div class="col-md-10">
                                                            <textarea id="resultJson" rows="23" class="form-control" data-always-visible="1" readonly></textarea>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <div class="col-md-offset-5 col-md-10">
                                                            <div class="height-auto">
                                                                <button class="btn green" type="button" onclick="sumitData();">提交</button>
                                                                <label style="padding-left: 20px;"></label>
                                                                <button class="btn default" type="button" onclick="resetdata()">重置</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<content tag="script">
    <script src="${ctx}/assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>
    <script src="${ctx}/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
    <script>
        var webUrl="${ctx}", bool=false;
        /**
         * 提交数据contentType:"application/json",
         **/
        function sumitData(){
            var path=$('#path').val(), jsonparm=$('#jsonparm').val(), type=$('input[type="radio"]:checked').val();
            if(''==$.trim(path)||''==$.trim(type)){
                bootbox.alert('信息输入不完整,请从新输入');
                return;
            }
            var data=JSON.parse(jsonparm);
            console.log(data);
            $.ajax({
                url:webUrl+path,
                type:type,
                dataType:"json",
                data:data,
                success:function(result){
                    result=JSON.stringify(result);
                    $("#resultJson").text(result.toString());
                }
            });

        }
        /**
         * 重置信息
         */
        function resetdata(){
            $('#filebutton').remove();
            $("input[type='file']").remove();
            $("#toUploadFile").attr("disabled",false);
            $("#resultJson").text('');
            $('#path').val('');
            $('#jsonparm').val('');
        }

    </script>
    <script type="text/javascript" src="${ctx}/assets/global/plugins/zeroclip/ZeroClipboard.js"></script>
    <script type="text/javascript">
        // 定义一个新的复制对象
        var clip=new ZeroClipboard(document.getElementById("d_clip_button"),{
            moviePath:"${ctx}/assets/global/plugins/zeroclip/ZeroClipboard.swf"
        });
        // 复制内容到剪贴板成功后的操作
        clip.on('complete',function(client,args){
            alert("复制成功，复制内容为："+args.text.substring(1,20)+"....");
        });

    </script>
</content>
</html>
