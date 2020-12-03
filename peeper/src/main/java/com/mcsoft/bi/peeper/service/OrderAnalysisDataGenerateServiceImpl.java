package com.mcsoft.bi.peeper.service;

import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Asset;
import com.binance.client.model.trade.Order;
import com.mcsoft.bi.common.bian.api.TradeApi;
import com.mcsoft.bi.peeper.constant.OrderConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by MC on 2020/11/27.
 *
 * @author MC
 */
@Service
public class OrderAnalysisDataGenerateServiceImpl implements OrderAnalysisDataGenerateService {

    @Autowired
    private TradeApi tradeApi;

    @Override
    public void generateOrderAnalysisData() {
        // 拉取账户信息
        final AccountInformation accountInformation = tradeApi.getAccountInformation();
        // 拉取所有币交易记录
        final List<Asset> assets = accountInformation.getAssets();
        if (CollectionUtils.isEmpty(assets)) {
            return;
        }
        for (Asset asset : assets) {
            final String symbol = asset.getAsset();
            final List<Order> allOrders = tradeApi.getAllOrders(symbol + OrderConstants.DEFAULT_TRADE_COIN, null,
                    OrderConstants.QUERY_START_TIME, null, 1000);
            // TODO 2020/12/3 : 待完成 -by MC
            if (CollectionUtils.isEmpty(allOrders)) {
                continue;
            }

        }


    }
}
