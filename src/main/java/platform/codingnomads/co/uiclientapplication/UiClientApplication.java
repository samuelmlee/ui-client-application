package platform.codingnomads.co.uiclientapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UiClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiClientApplication.class, args);
    }

}
