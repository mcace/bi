package com.mcsoft.bi.common.bian.future.api;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.impl.BinanceApiInternalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by MC on 2020/8/19.
 *
 * @author MC
 */
public class ApiTraderImpl implements ApiTrader {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(ApiTraderImpl.class);

    private final SyncRequestClient syncRequestClient;

    public ApiTraderImpl(String apiKey, String apiSecret) {
        this.syncRequestClient = BinanceApiInternalFactory.getInstance().createSyncRequestClient(
                apiKey,
                apiSecret, new RequestOptions()
        );
    }


}
