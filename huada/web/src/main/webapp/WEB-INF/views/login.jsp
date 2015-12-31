<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en" class="no-js">
<!--<![endif]-->
<head>
    <meta charset="utf-8"/>
    <title>登录页</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <meta content="" name="description"/>
    <meta content="" name="author"/>
    <link href="${ctx}/assets/global/css/openSans.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/plugins/select2/select2.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/css/components.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/global/css/plugins.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/admin/layout/css/layout.css" rel="stylesheet" type="text/css"/>
    <link id="style_color" href="${ctx}/assets/admin/layout/css/themes/default.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/assets/admin/layout/css/custom.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        .login{
            background-color: #666 !important;
        }

        .login .logo{
            margin: 0 auto;
            margin-top: 60px;
            padding: 15px;
            text-align: center;
        }

        .login .content{
            background: url(${ctx}/assets/admin/pages/img/bg-white-lock.png) repeat;
            width: 360px;
            margin: 0 auto;
            margin-bottom: 0px;
            padding: 30px;
            padding-top: 20px;
            padding-bottom: 15px;
        }

        .login .content h3{
            color: #eee;
        }

        .login .content h4{
            color: #eee;
        }

        .login .content p,
        .login .content label{
            color: #fff;
        }

        .login .content .login-form{
            padding: 0px;
            margin: 0px;
        }

        .login .content .form-control{
            background-color: #fff;
        }

        .login .content .form-title{
            font-weight: 300;
            margin-bottom: 25px;
        }

        .login .content .form-actions{
            background-color: transparent;
            clear: both;
            border: 0px;
            padding: 0px 30px 25px 30px;
            margin-left: -30px;
            margin-right: -30px;
        }

        .login .content .form-actions .checkbox{
            margin-left: 0;
            padding-left: 0;
        }

        .login .content .forget-form .form-actions{
            border: 0;
            margin-bottom: 0;
            padding-bottom: 20px;
        }

        .login .content .form-actions .checkbox{
            margin-top: 8px;
            display: inline-block;
        }

        .login .content .form-actions .btn{
            margin-top: 1px;
        }

        /* select2 dropdowns */
        .login .content .select2-container i{
            display: inline-block;
            position: relative;
            color: #ccc;
            z-index: 1;
            top: 1px;
            margin: 4px 4px 0px -1px;
            width: 16px;
            height: 16px;
            font-size: 16px;
            text-align: center;
        }

        .login .content .has-error .select2-container i{
            color: #b94a48;
        }

        .login .content .select2-container a span{
            font-size: 13px;
        }

        .login .content .select2-container a span img{
            margin-left: 4px;
        }

        /* footer copyright */
        .login .copyright{
            text-align: center;
            margin: 0 auto;
            padding: 10px;
            color: #eee;
            font-size: 13px;
        }

        @media (max-width: 480px){
            .login .logo{
                margin-top: 10px;
            }

            .login .content{
                padding: 30px;
                width: 222px;
            }

            .login .content h3{
                font-size: 22px;
            }

            .login .checkbox{
                font-size: 13px;
            }
        }

    </style>
    <link rel="shortcut icon" href="${ctx}/favicon.ico"/>
    <script>var ctx="${ctx}";</script>
</head>
<body class="login">
<div class="logo">
    <a href="${ctx}/">
        <img src="${ctx}/assets/admin/layout/img/logo-big.png" alt=""/>
    </a>
</div>
<div class="menu-toggler sidebar-toggler">
</div>
<div class="content">
    <form class="login-form" action="${ctx}/login" method="post">
        <h3 class="form-title">登录系统账号</h3>
        <%
            String error=(String)request.getAttribute(org.apache.shiro.web.filter.authc.FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
            if(error!=null){
        %>
        <div class="alert alert-danger">
            <button class="close" data-close="alert"></button>
            <span><%
                if(error.contains("DisabledAccountException")){
                    out.print("用户已被屏蔽,请登录其他用户.");
                }else if(error.contains("UnknownAccountException")){
                    out.print("用户不存在,请检查后重试!");
                }else{
                    out.print("登录失败，请重试.");
                }
            %></span>
        </div>
        <%}%>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">用户名</label>

            <div class="input-icon">
                <i class="fa fa-user"></i>
                <input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="用户名" name="username" value="${username}"/>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">密&nbsp;&nbsp;码</label>

            <div class="input-icon">
                <i class="fa fa-lock"></i>
                <input class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="密码" name="password"/>
            </div>
        </div>
        <div class="form-actions">
            <label class="checkbox"><input type="checkbox" name="rememberMe" id="rememberMe" value="true"/> 下次自动登录 </label>
            <button type="submit" class="btn blue pull-right">登陆 <i class="m-icon-swapright m-icon-white"></i></button>
        </div>
    </form>
</div>
<div class="copyright">@2014 &copy; 深圳盈诺德信息技术有限公司.</div>
<!--[if lt IE 9]>
<script src="${ctx}/assets/global/plugins/respond.min.js"></script>
<script src="${ctx}/assets/global/plugins/excanvas.min.js"></script>
<![endif]-->
<script src="${ctx}/assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="${ctx}/assets/global/plugins/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
<script src="${ctx}/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctx}/assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js"
        type="text/javascript"></script>
<script src="${ctx}/assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js" type="text/javascript"></script>
<script src="${ctx}/assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>
<script src="${ctx}/assets/global/plugins/jquery.cokie.min.js" type="text/javascript"></script>
<script src="${ctx}/assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script>
<script src="${ctx}/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>
<script src="${ctx}/assets/global/plugins/backstretch/jquery.backstretch.min.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/assets/global/plugins/select2/select2.min.js"></script>
<script src="${ctx}/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${ctx}/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>
<script>
    jQuery(document).ready(function(){
        Metronic.init();
        Layout.init();
    });
    $('.login-form').validate({
        errorElement:'span',
        errorClass:'help-block',
        focusInvalid:false,
        rules:{
            username:{
                required:true
            },
            password:{
                required:true
            },
            remember:{
                required:false
            }
        },
        messages:{
            username:{
                required:"用户名不能为空."
            },
            password:{
                required:"密码不能为空."
            }
        },
        invalidHandler:function(event,validator){
            $('.alert-danger',$('.login-form')).show();
        },
        highlight:function(element){
            $(element).closest('.form-group').addClass('has-error');
        },
        success:function(label){
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement:function(error,element){
            error.insertAfter(element.closest('.input-icon'));
        },
        submitHandler:function(form){
            form.submit();
        }
    });
    $('.login-form input').keypress(function(e){
        if(e.which==13){
            if($('.login-form').validate().form()){
                $('.login-form').submit();
            }
            return false;
        }
    });
    $.backstretch([ctx+"/assets/admin/pages/media/bg/2.jpg"],{
        fade:1000,
        duration:8000
    });
</script>
</body>
</html>
