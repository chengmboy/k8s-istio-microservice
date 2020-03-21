package io.github.chengmboy.kim.gateway;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableZuulProxy
@EnableFeignClients(basePackages = {"io.github.chengmboy.kim"})
@EnableHystrix
@SpringBootApplication(scanBasePackages = {"io.github.chengmboy.kim"})
@EnableApolloConfig
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
}
