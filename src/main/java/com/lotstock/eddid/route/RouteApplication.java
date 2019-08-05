package com.lotstock.eddid.route;

import com.lotstock.eddid.ibmp.config.ZuulAutoConfigure;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@ImportAutoConfiguration({ZuulAutoConfigure.class})
@EnableSwagger2Doc
public class RouteApplication extends SpringBootServletInitializer{
	
	protected static final Logger log = Logger.getLogger(RouteApplication.class);
	

    @Bean
	@LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
	public static void main(String[] args) {
		SpringApplication.run(RouteApplication.class, args);
		log.info("route start success！");
	}
	
}
