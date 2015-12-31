package com.kingnode.workflow.filter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class RequestParamFilter extends OncePerRequestFilter{
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException{
        Logger log =LoggerFactory.getLogger(RequestParamFilter.class);
        String requestUrl = request.getRequestURI();
        String ctxPath = request.getContextPath();
        String param = request.getQueryString();
        String method = request.getMethod();
        log.info("请求地址url:"+requestUrl);
        log.info("请求参数param:"+param);
        log.info("请求方式method:"+method);
        if(method.equals("POST")){
//            StringBuffer sb = new StringBuffer();
//            InputStream is = request.getInputStream();
//            byte[] bytes = new byte[1024];
//            int iRead;
//            while((iRead=is.read(bytes))!=-1){
//                sb.append(new String(bytes,0,iRead,"UTF-8"));
//            }
//            log.info("请求参数param:"+sb.toString());
        }else{
            log.info("请求参数param:"+param);
        }
        filterChain.doFilter(request, response);
    }
}
