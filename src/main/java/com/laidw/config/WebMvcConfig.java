package com.laidw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 主要是配置静态文件路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${dit.upload-photo-dir}")
    private String imgDir;

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //注意，磁盘路径需要以"file:/"开头，同时末尾需要有"/"
        registry.addResourceHandler("/img/**").addResourceLocations("file:/" + imgDir);
    }
}
