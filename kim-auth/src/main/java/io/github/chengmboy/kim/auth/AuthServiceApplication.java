package io.github.chengmboy.kim.auth;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableFeignClients(basePackages = "io.github.chengmboy.kim.*.api")
@SpringBootApplication(scanBasePackages = {"io.github.chengmboy.kim.auth","io.github.chengmboy.kim.*.api",
		"io.github.chengmboy.kim.common.bean"})
@EnableHystrix
@EnableSwagger2
@EnableApolloConfig
public class AuthServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
}
