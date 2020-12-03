package com.mcsoft.bi.peeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by MC on 2020/8/18.
 *
 * @author MC
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.mcsoft.bi")
public class PeeperApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(PeeperApplication.class, args);
    }

}
