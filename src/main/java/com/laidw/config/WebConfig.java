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
     * 工程不打包成Jar包时图片目录的相对路径；此时的参考路径为工程根目录的上一级目录
     */
    @Value("${dit.img.upload-relative-dir.no-jar}")
    private String imgDirNoJar;

    /**
     * 工程打包成Jar包时图片目录的相对路径；此时的参考路径为Jar包所在目录
     */
    @Value("${dit.img.upload-relative-dir.is-jar}")
    private String imgDirIsJar;

    /**
     * 往IOC容器添加HiddenHttpMethodFilter组件，以让项目支持PUT和DELETE等提交方式
     * 在提交PUT等请求时，表单的提交方式必须是POST，然后通过_method请求参数来指定真正的提交方式
     * @return HiddenHttpMethodFilter组件
     */
    @Bean
    public HiddenHttpMethodFilter supportMoreRequests(){
        return new HiddenHttpMethodFilter();
    }

    /**
     * 配置静态资源路径映射，用于解决图片上传后头像无法及时更新的问题
     * @param registry 通过调用该对象的方法来进行配置
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //注意，磁盘路径需要以"file:/"开头，同时末尾需要有"/"
        registry.addResourceHandler("/img/**").addResourceLocations("file:/" + getImgDir() + "/");
    }

    /**
     * 获取图片目录的完整路径，同时将这个完整路径放到IOC容器中
     * @return 图片目录的完整路径
     */
    @Bean("imgDir")
    public String getImgDir(){
        ApplicationHome home = new ApplicationHome(getClass());

        /*
         * home.getSource().getPath()的返回值：
         * 不打包成Jar时为工程的classes目录，如C:\Users\Acer\Desktop\IdeaPros\DoItTogether\target\classes
         * 打包成Jar包时为Jar包的完整路径，即${jar包所在目录}\do_it_together-xxx.jar
         * 因此可通过后缀名来确定工程是否被打包成了Jar包
         */
        File source = home.getSource();

        //在进行单元测试时，source为空，此时随便返回一个字符串即可，因为此时并不需要图片目录
        if(source == null){
            System.out.println("ApplicationHome source not found!");
            return "C:\\Users\\Acer\\Desktop\\";
        }

        //根据source.getPath()来判断工程是否被打包成Jar包
        boolean isJar = source.getPath().endsWith(".jar");

        //获取home.getSource()的上一级目录
        File dir = source.getParentFile();

        //如果是打包成Jar包，则此时的dir就是图片目录的参考路径
        //注意，如果此时dir目录中没有图片文件夹，则应该手动创建图片文件夹，并把默认图片放进去
        if(isJar)
            return new File(dir, imgDirIsJar).getPath();

        //如果不是打包成Jar包，则需要再退两级目录才能得到参考路径
        dir = dir.getParentFile().getParentFile();
        return new File(dir, imgDirNoJar).getPath();
    }
}
