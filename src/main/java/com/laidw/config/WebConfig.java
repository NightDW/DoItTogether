package com.laidw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;

/**
 * 和Web相关的配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${dit.upload-img-relative-dir-no-jar}")
    private String imgDirNoJar;

    @Value("${dit.upload-img-relative-dir-is-jar}")
    private String imgDirIsJar;

    //让项目支持PUT等提交方式
    @Bean
    public HiddenHttpMethodFilter supportPutDelRequest(){
        return new HiddenHttpMethodFilter();
    }

    //配置静态资源路径映射
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //注意，磁盘路径需要以"file:/"开头，同时末尾需要有"/"
        registry.addResourceHandler("/img/**").addResourceLocations("file:/" + getImgDir() + "/");
    }

    @Bean("imgDir")
    public String getImgDir(){
        ApplicationHome home = new ApplicationHome(getClass());

        //home.getSource().getPath()的返回值：
        //不打包成Jar时为 C:\Users\Acer\Desktop\IdeaPros\DoItTogether\target\classes
        //打包成Jar包时为 {jar包所在目录}\do_it_together-0.0.1-SNAPSHOT.jar
        File tem = home.getSource();
        boolean isJar = tem.getPath().endsWith(".jar");

        File dir = tem.getParentFile();

        //如果不是打包成Jar包，则需要再往前退两级目录
        if(!isJar){
            dir = dir.getParentFile().getParentFile();
            return new File(dir, imgDirNoJar).getPath();
        }
        //如果是打包成Jar包，则要注意手动创建目录，并把默认图片复制进去
        return new File(dir, imgDirIsJar).getPath();
    }
}
