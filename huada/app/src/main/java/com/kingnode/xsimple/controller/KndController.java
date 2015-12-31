package com.kingnode.xsimple.controller;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.kingnode.diva.mapper.JsonMapper;
import com.kingnode.diva.qrcode.TwoDimensionCode;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.rest.RestStatus;
import com.kingnode.xsimple.service.system.ResourceService;
import com.kingnode.xsimple.util.key.UuidMaker;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
/**
 * LoginController负责打开登录页面(GET请求)和登录出错页面(POST请求)，
 * <p/>
 * 真正登录的POST请求由Filter完成,
 *
 * @author Chirs Chou(chirs@zhoujin.com)
 */
@Controller public class KndController{
    private static Logger logger=LoggerFactory.getLogger(KndController.class);
    @Autowired private ResourceService res;
    @RequestMapping(value="login", method=RequestMethod.GET) public String login(){
        Subject s=SecurityUtils.getSubject();
        return s.isRemembered()||s.isAuthenticated()?"redirect:main":"login";
    }
    @RequestMapping(value="login", method=RequestMethod.POST) public String fail(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String userName,Model model){
        model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM,userName);
        return "login";
    }
    @RequestMapping(value="/main", method=RequestMethod.GET) public String main(){
        return "Main";
    }
    @RequestMapping(value={"/upload-file"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public void upload(@RequestParam("files") MultipartFile file,HttpServletResponse response,@RequestParam(value="path", defaultValue="") String path){
        Map<String,String> hm=new HashMap<>();
        String uuid=UuidMaker.getInstance().getUuid(true);
        hm.put("uuid",uuid);
        WebApplicationContext webApplicationContext=ContextLoader.getCurrentWebApplicationContext();
        String fileExt=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1).toLowerCase();//扩展名
        StringBuilder sb=new StringBuilder(Setting.BASEADDRESS);
        if(!Strings.isNullOrEmpty(path)){
            sb.append("/").append(path);
        }
        sb.append("/").append(Calendar.getInstance().get(Calendar.YEAR));
        sb.append("/").append(Calendar.getInstance().get(Calendar.MONTH));
        sb.append("/").append(uuid).append(".").append(fileExt);
        String fileName=sb.toString();
        File localFile=new File(webApplicationContext.getServletContext().getRealPath(fileName));
        if(!localFile.getParentFile().exists()){
            localFile.getParentFile().mkdirs();
        }
        hm.put("size",Long.valueOf(file.getSize()).toString());
        try{
            file.transferTo(localFile);
            if(getImageFormat(fileExt)){
                //生成缩略图
                StringBuilder sp=new StringBuilder(Setting.BASEADDRESS);
                if(!Strings.isNullOrEmpty(path)){
                    sp.append("/").append(path);
                }
                sp.append("/").append(Calendar.getInstance().get(Calendar.YEAR));
                sp.append("/").append(Calendar.getInstance().get(Calendar.MONTH));
                sp.append("/").append(uuid).append("_s").append(".").append(fileExt);
                try{
                    Thumbnails.of(localFile).size(256,256).outputQuality(1.0f).toFile(webApplicationContext.getServletContext().getRealPath(sp.toString()));
                }catch(IOException e){
                    logger.info("生成缩略图出错!");
                }
            }                          //触发图像处理
            hm.put("path",Setting.BASEADDRESS);
            hm.put("ext",fileExt);
            hm.put("url",fileName);
            response.setContentType("text/html; charset=UTF-8");
            JsonMapper.nonEmptyMapper().getMapper().writeValue(response.getWriter(),hm);
        }catch(IOException e){
            logger.info("上传文件出错!");
        }
    }
    private static Boolean getImageFormat(String suffix){
        return "TFF".equalsIgnoreCase(suffix)||"TIFF".equalsIgnoreCase(suffix)||"PNG".equalsIgnoreCase(suffix)||"GIF".equalsIgnoreCase(suffix)||"JPG".equalsIgnoreCase(suffix)||"JPEG".equalsIgnoreCase(suffix);
    }
    @RequestMapping(value="/api/login", method=RequestMethod.POST) @ResponseBody
    public RestStatus login(@RequestParam("user") String userName,@RequestParam("password") String password,@RequestParam("remember") boolean remember){
        RestStatus rs=new RestStatus(true);
        if(StringUtils.isEmpty(userName)||StringUtils.isEmpty(password)){
            rs.setStatus(false);
            rs.setErrorCode("empty");
            rs.setErrorMessage("The username and password cannot be the empty!");
            return rs;
        }
        UsernamePasswordToken token=new UsernamePasswordToken(userName,password,remember);
        try{
            Subject subject=SecurityUtils.getSubject();
            subject.login(token);
            return rs;
        }catch(UnknownAccountException e){
            rs.setStatus(false);
            rs.setErrorCode("unknown");
            rs.setErrorMessage("unknown account");
            return rs;
        }catch(DisabledAccountException e){
            rs.setStatus(false);
            rs.setErrorCode("disabled");
            rs.setErrorMessage("disabled account");
            return rs;
        }catch(AuthenticationException e){
            rs.setStatus(false);
            rs.setErrorCode("authentication");
            rs.setErrorMessage("authentication account error");
            return rs;
        }
    }
    @RequestMapping("/api/access") public HttpEntity token(HttpServletRequest request) throws URISyntaxException, OAuthSystemException{
        try{
            //构建OAuth请求
            OAuthTokenRequest oauthRequest=new OAuthTokenRequest(request);
            //检查提交的客户端id是否正确
            if(!res.checkClientId(oauthRequest.getClientId())){
                OAuthResponse response=OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).setError(OAuthError.TokenResponse.INVALID_CLIENT).setErrorDescription(Setting.INVALID_CLIENT_DESCRIPTION).buildJSONMessage();
                return new ResponseEntity<>(response.getBody(),HttpStatus.valueOf(response.getResponseStatus()));
            }
            // 检查客户端安全KEY是否正确
            if(!res.checkClientSecret(oauthRequest.getClientSecret())){
                OAuthResponse response=OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED).setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT).setErrorDescription(Setting.INVALID_CLIENT_DESCRIPTION).buildJSONMessage();
                return new ResponseEntity<>(response.getBody(),HttpStatus.valueOf(response.getResponseStatus()));
            }
            String authCode=oauthRequest.getParam(OAuth.OAUTH_CODE);
            // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
            if(oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())){
                if(!res.checkAuthCode(authCode)){
                    OAuthResponse response=OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).setError(OAuthError.TokenResponse.INVALID_GRANT).setErrorDescription("错误的授权码").buildJSONMessage();
                    return new ResponseEntity<>(response.getBody(),HttpStatus.valueOf(response.getResponseStatus()));
                }
            }
            //生成Access Token
            OAuthIssuer oauthIssuerImpl=new OAuthIssuerImpl(new MD5Generator());
            final String accessToken=oauthIssuerImpl.accessToken();
            res.addAccessToken(accessToken,res.getUsernameByAuthCode(authCode));
            //生成OAuth响应
            OAuthResponse response=OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK).setAccessToken(accessToken).setExpiresIn(String.valueOf(res.getExpireIn())).buildJSONMessage();
            //根据OAuthResponse生成ResponseEntity
            return new ResponseEntity<>(response.getBody(),HttpStatus.valueOf(response.getResponseStatus()));
        }catch(OAuthProblemException e){
            //构建错误响应
            OAuthResponse res=OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e).buildJSONMessage();
            return new ResponseEntity<>(res.getBody(),HttpStatus.valueOf(res.getResponseStatus()));
        }
    }
    @RequestMapping("/api/authorize") public Object authorize(Model model,HttpServletRequest request) throws URISyntaxException, OAuthSystemException{
        try{
            //构建OAuth 授权请求
            OAuthAuthzRequest oauthRequest=new OAuthAuthzRequest(request);
            //检查传入的客户端id是否正确
            if(!res.checkClientId(oauthRequest.getClientId())){
                OAuthResponse response=OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).setError(OAuthError.TokenResponse.INVALID_CLIENT).setErrorDescription(Setting.INVALID_CLIENT_DESCRIPTION).buildJSONMessage();
                return new ResponseEntity<>(response.getBody(),HttpStatus.valueOf(response.getResponseStatus()));
            }
            Subject subject=SecurityUtils.getSubject();
            //如果用户没有登录，跳转到登陆页面
            if(!subject.isAuthenticated()){
                if(!login(subject,request)){//登录失败时跳转到登陆页面
                    model.addAttribute("client",res.findByClientId(oauthRequest.getClientId()));
                    return "login";
                }
            }
            String username=(String)subject.getPrincipal();
            //生成授权码
            String authorizationCode=null;
            //responseType目前仅支持CODE，另外还有TOKEN
            String responseType=oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            if(responseType.equals(ResponseType.CODE.toString())){
                OAuthIssuerImpl oauthIssuerImpl=new OAuthIssuerImpl(new MD5Generator());
                authorizationCode=oauthIssuerImpl.authorizationCode();
                res.addAuthCode(authorizationCode,username);
            }
            //进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder=OAuthASResponse.authorizationResponse(request,HttpServletResponse.SC_FOUND);
            //设置授权码
            builder.setCode(authorizationCode);
            //得到到客户端重定向地址
            String redirectURI=oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
            //构建响应
            final OAuthResponse response=builder.location(redirectURI).buildQueryMessage();
            //根据OAuthResponse返回ResponseEntity响应
            HttpHeaders headers=new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity(headers,HttpStatus.valueOf(response.getResponseStatus()));
        }catch(OAuthProblemException e){
            //出错处理
            String redirectUri=e.getRedirectUri();
            if(OAuthUtils.isEmpty(redirectUri)){
                //告诉客户端没有传入redirectUri直接报错
                return new ResponseEntity<>("OAuth callback url needs to be provided by client!!!",HttpStatus.NOT_FOUND);
            }
            //返回错误消息（如?error=）
            final OAuthResponse response=OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND).error(e).location(redirectUri).buildQueryMessage();
            HttpHeaders headers=new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity(headers,HttpStatus.valueOf(response.getResponseStatus()));
        }
    }
    @RequestMapping("/api/code") public void TwoDimensionCode(HttpServletResponse response,HttpServletRequest request){
        String code="/Pcq3FqoXb25qQaAgc5ek2ru2GjcXFj/HHjmfVjft2R4vG8F2heaGZrZCALVJWegrTc/vyZmevyBVCfw6auH/dON9OyHge5PZ2thc/FPJFGIdQ+6Ape77w==";
        try{
            new TwoDimensionCode().encoderQRCode(code,response.getOutputStream());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private boolean login(Subject subject,HttpServletRequest request){
        if("get".equalsIgnoreCase(request.getMethod())){
            return false;
        }
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        if(StringUtils.isEmpty(username)||StringUtils.isEmpty(password)){
            return false;
        }
        UsernamePasswordToken token=new UsernamePasswordToken(username,password);
        try{
            subject.login(token);
            return true;
        }catch(Exception e){
            request.setAttribute("error","登录失败:"+e.getClass().getName());
            return false;
        }
    }
}