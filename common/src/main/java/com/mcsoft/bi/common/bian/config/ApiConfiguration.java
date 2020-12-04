package com.mcsoft.bi.common.bian.config;

import com.binance.client.impl.RestApiInvoker;
import com.mcsoft.bi.common.bian.future.api.FutureCollectorApi;
import com.mcsoft.bi.common.bian.future.api.FutureCollectorApiImpl;
import com.mcsoft.bi.common.bian.future.api.FutureInformationApi;
import com.mcsoft.bi.common.bian.future.api.FutureInformationApiImpl;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
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
    public FutureCollectorApi apiCollector() {
        return new FutureCollectorApiImpl(apiKey, apiSecret);
    }

    @Bean
    public FutureInformationApi tradeApi() {
        return new FutureInformationApiImpl(apiKey, apiSecret);
    }

    @Bean
    public Exchange binanceExchange(){
        final ExchangeSpecification defaultExchangeSpecification = new BinanceExchange().getDefaultExchangeSpecification();
        defaultExchangeSpecification.setApiKey(apiKey);
        defaultExchangeSpecification.setSecretKey(apiSecret);
        setProxy(defaultExchangeSpecification);
        return ExchangeFactory.INSTANCE.createExchange(defaultExchangeSpecification);
    }

    private void setProxy(ExchangeSpecification defaultExchangeSpecification) {
        if (StringUtils.isNotBlank(proxyConfig.getHost())) {
            defaultExchangeSpecification.setProxyHost(proxyConfig.getHost());
            defaultExchangeSpecification.setProxyPort(proxyConfig.getPort());
        }
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
