package com.luxinx.config;

import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
//@Configuration
public class UserFilter implements Filter {

    @Value("${account.username}")
    private String username;
    @Value("${account.password}")
    private String password;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String token = request.getHeader("token");
        String md5key = MD5Encoder.encode((username + password).getBytes());
        if(StringUtils.isEmpty(token)||md5key.equals(token)){
            response.getWriter().print("invilid request");
        }else {
            chain.doFilter(request,response);
        }
    }
}
