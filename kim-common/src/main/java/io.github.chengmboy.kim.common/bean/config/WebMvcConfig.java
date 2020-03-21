package io.github.chengmboy.kim.common.bean.config;

import feign.RequestInterceptor;
import io.github.chengmboy.kim.common.bean.interceptor.FeignBasicAuthRequestInterceptor;
import io.github.chengmboy.kim.common.bean.interceptor.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns("/**");
    }

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new FeignBasicAuthRequestInterceptor();
    }
}