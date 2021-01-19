package com.luxinx.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
public class DruidConfiguration {
    private static Logger log = LoggerFactory.getLogger(DruidConfiguration.class);
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource druid() {
        return new DruidDataSource();
    }
    /**
     * 配置druid管理页面的访问控制
     * 访问网址: http://127.0.0.1:8080/druid
     * @return
     */
    @Bean
    public ServletRegistrationBean<Servlet> druidServlet() {
        log.info("init Druid Servlet Configuration");
        ServletRegistrationBean<Servlet> servletRegistrationBean = new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(new StatViewServlet());  //配置一个拦截器
        servletRegistrationBean.addUrlMappings("/druid/*");    //指定拦截器只拦截druid管理页面的请求
        HashMap<String, String> initParam = new HashMap<String,String>();
        initParam.put("loginUsername", "admin");    //登录druid管理页面的用户名
        initParam.put("loginPassword", "G9ru4Ss1u3S0iz!u");    //登录druid管理页面的密码
        initParam.put("resetEnable", "true");       //是否允许重置druid的统计信息
        initParam.put("allow", "");         //ip白名单，如果没有设置或为空，则表示允许所有访问
        servletRegistrationBean.setInitParameters(initParam);
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<WebStatFilter>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}