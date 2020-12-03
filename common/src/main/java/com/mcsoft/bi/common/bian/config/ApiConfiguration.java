package com.mcsoft.bi.common.bian.config;

import com.binance.client.impl.RestApiInvoker;
import com.mcsoft.bi.common.bian.api.ApiCollector;
import com.mcsoft.bi.common.bian.api.ApiCollectorImpl;
import com.mcsoft.bi.common.bian.api.TradeApi;
import com.mcsoft.bi.common.bian.api.TradeApiImpl;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

/**
 * API各种配置
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Configuration
public class ApiConfiguration {

    @Autowired
    private ProxyConfig proxyConfig;
    @Value("${bi.api.key}")
    private String apiKey;
    @Value("${bi.api.secret}")
    private String apiSecret;

    @Bean
    public ApiCollector apiCollector() {
        return new ApiCollectorImpl(apiKey, apiSecret);
    }

    @Bean
    public TradeApi tradeApi() {
        return new TradeApiImpl(apiKey, apiSecret);
    }

    @PostConstruct
    public void setRestApiInvoker() {
        if (StringUtils.isNotBlank(proxyConfig.getHost())) {
            SocketAddress proxyAddress = new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort());
            Proxy proxy = new Proxy(proxyConfig.getType(), proxyAddress);
            final OkHttpClient client = new OkHttpClient.Builder().proxy(proxy).build();
            RestApiInvoker.setClient(client);
        }
    }

}
