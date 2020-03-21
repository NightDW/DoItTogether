package com.laidw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

/**
 * 主要是为了让项目支持PUT等提交方式
 */
@Configuration
public class IocConfig {

    @Bean
    public HiddenHttpMethodFilter supportPutDelRequest(){
        return new HiddenHttpMethodFilter();
    }

}
