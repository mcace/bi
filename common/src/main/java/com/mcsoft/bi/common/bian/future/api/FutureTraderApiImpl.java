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
public class FutureTraderApiImpl implements FutureTraderApi {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(FutureTraderApiImpl.class);

    private final SyncRequestClient syncRequestClient;

    public FutureTraderApiImpl(String apiKey, String apiSecret) {
        this.syncRequestClient = BinanceApiInternalFactory.getInstance().createSyncRequestClient(
                apiKey,
                apiSecret, new RequestOptions()
        );
    }


}
