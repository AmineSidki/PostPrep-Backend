package org.aminesidki.postprep;

import org.aminesidki.postprep.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class PostPrepApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostPrepApplication.class, args);
    }

}
