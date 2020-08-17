package com.mcsoft.bi.bark;

import com.mcsoft.bi.bark.service.NoticeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@EnableFeignClients(basePackages = "com.mcsoft.bi")
@ComponentScan(basePackages = "com.mcsoft.bi")
@SpringBootApplication
public class BarkApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(BarkApplication.class, args);
        final NoticeService service = context.getBean(NoticeService.class);
        service.start();
    }

}
