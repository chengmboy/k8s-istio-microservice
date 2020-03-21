package io.github.chengmboy.kim.uc;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableHystrix
@SpringBootApplication(scanBasePackages = {"io.github.chengmboy.kim.uc","io.github.chengmboy.kim.common.bean"})
@EnableDiscoveryClient
@EnableApolloConfig
public class UcServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UcServiceApplication.class, args);
	}
}
