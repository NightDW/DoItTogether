package com.laidw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 和Web层相关的配置
 * 这里主要是在配置文件上传功能
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 工程不打包成Jar包时图片目录的相对路径，此时的参考路径为工程所在目录
     */
    @Value("${dit.img.upload-relative-dir.no-jar}")
    private String imgDirNoJar;

    /**
     * 工程打包成Jar包时图片目录的相对路径，此时的参考路径为Jar包所在目录
     */
    @Value("${dit.img.upload-relative-dir.is-jar}")
    private String imgDirIsJar;

    @Bean
    public HiddenHttpMethodFilter supportMoreRequests(){

        /*
         * HiddenHttpMethodFilter可以让项目支持PUT和DELETE等提交方式
         * 提交PUT等请求时，表单的提交方式必须是POST，然后用_method请求参数来指定真正的提交方式
         */
        return new HiddenHttpMethodFilter();
    }

    /**
     * 配置静态资源路径映射，用于解决图片上传后头像无法及时更新的问题
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //注意，磁盘路径需要以"file:/"开头，同时末尾需要有"/"
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:/" + getImgDir() + "/");
    }

    @Bean("imgDir")
    public String getImgDir(){
        ApplicationHome home = new ApplicationHome(getClass());

        /*
         * home.getSource().getPath()的返回值：
         * 不打包成Jar时为 C:\Users\Acer\Desktop\IdeaPros\DoItTogether\target\classes
         * 打包成Jar包时为 ${jar包所在目录}\do_it_together-xxx.jar
         * 因此可通过后缀名来确定工程是否被打包成了Jar包
         */
        File tem = home.getSource();

        //进行单元测试时，tem可能为空，此时随便返回一个字符串即可
        if(tem == null){
            System.out.println("ApplicationHome not found!");
            return "C:\\Users\\Acer\\Desktop\\";
        }

        boolean isJar = tem.getPath().endsWith(".jar");
        File dir = tem.getParentFile();

        //如果不是打包成Jar包，则需要再往前退两级目录
        if(!isJar){
            dir = dir.getParentFile().getParentFile();
            return new File(dir, imgDirNoJar).getPath();
        }

        //如果是打包成Jar包，则要注意，应该手动创建目录，并把默认图片复制进去
        return new File(dir, imgDirIsJar).getPath();
    }
}
