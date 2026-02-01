package org.aminesidki.postprep;

import org.aminesidki.postprep.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class PostPrepApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostPrepApplication.class, args);
    }

}
