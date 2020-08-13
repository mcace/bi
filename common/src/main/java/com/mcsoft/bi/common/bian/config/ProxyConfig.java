package com.mcsoft.bi.common.bian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.Proxy;

/**
 * 代理配置，如果需要代理，则要配置一下
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Configuration
@ConfigurationProperties(prefix = "bi.proxy")
@Data
public class ProxyConfig {

    private Proxy.Type type;
    private String host;
    private Integer port;

}
