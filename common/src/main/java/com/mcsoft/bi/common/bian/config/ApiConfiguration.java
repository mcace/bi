package com.mcsoft.bi.common.bian.config;

import com.binance.client.impl.RestApiInvoker;
import com.mcsoft.bi.common.bian.future.api.FutureCollectorApi;
import com.mcsoft.bi.common.bian.future.api.FutureCollectorApiImpl;
import com.mcsoft.bi.common.bian.future.api.FutureInformationApi;
import com.mcsoft.bi.common.bian.future.api.FutureInformationApiImpl;
import com.mcsoft.xchange.binance.MyBinanceExchange;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceResilience;
import org.knowm.xchange.client.ResilienceRegistries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.time.Duration;

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
    public Exchange binanceExchange() {
        final ExchangeSpecification defaultExchangeSpecification = new MyBinanceExchange().getDefaultExchangeSpecification();
        defaultExchangeSpecification.setApiKey(apiKey);
        defaultExchangeSpecification.setSecretKey(apiSecret);
        // 设置限速
        setResilience(defaultExchangeSpecification);
        // 设置代理
        setProxy(defaultExchangeSpecification);
        Exchange exchange = ExchangeFactory.INSTANCE.createExchange(defaultExchangeSpecification);
        setResilienceRegistries(exchange.getResilienceRegistries());
        return exchange;
    }

    private void setResilience(ExchangeSpecification defaultExchangeSpecification) {
        ExchangeSpecification.ResilienceSpecification resilienceSpecification = new ExchangeSpecification.ResilienceSpecification();
        resilienceSpecification.setRateLimiterEnabled(true);
        resilienceSpecification.setRetryEnabled(true);
        defaultExchangeSpecification.setResilience(resilienceSpecification);
    }

    private void setProxy(ExchangeSpecification defaultExchangeSpecification) {
        if (StringUtils.isNotBlank(proxyConfig.getHost())) {
            defaultExchangeSpecification.setProxyHost(proxyConfig.getHost());
            defaultExchangeSpecification.setProxyPort(proxyConfig.getPort());
        }
    }

    private void setResilienceRegistries(ResilienceRegistries resilienceRegistries) {
        resilienceRegistries.rateLimiters().remove(BinanceResilience.REQUEST_WEIGHT_RATE_LIMITER);
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.from(resilienceRegistries.rateLimiters().getDefaultConfig())
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .limitForPeriod(200)
                .timeoutDuration(Duration.ofSeconds(60))
                .build();
        RateLimiter rateLimiter = resilienceRegistries.rateLimiters().rateLimiter(BinanceResilience.REQUEST_WEIGHT_RATE_LIMITER, rateLimiterConfig);
        System.out.println(rateLimiter);
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
