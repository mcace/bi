package com.mcsoft.bi.sniper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by MC on 2020/8/18.
 *
 * @author MC
 */
@ComponentScan(basePackages = "com.mcsoft")
@SpringBootApplication
public class SniperApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SniperApplication.class, args);
    }

}
