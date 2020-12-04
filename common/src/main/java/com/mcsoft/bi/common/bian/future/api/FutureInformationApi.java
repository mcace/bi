package com.mcsoft.bi.common.bian.future.api;

import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 合约账户信息
 * Created by MC on 2020/11/27.
 *
 * @author MC
 */
public interface FutureInformationApi {

    /**
     * 获取账户信息
     *
     * @return 账户信息
     */
    AccountInformation getAccountInformation();

    /**
     * 获取所有合约交易记录
     *  @param symbol    交易对（e.g ALGOUSDT），必传
     * @param orderId   起始订单id，类似游标，选传
     * @param startTime 查询起始时间，选传
     * @param endTime   查询结束时间，选传
     * @param limit     查询数量，默认500，最大1000，选传
     * @return
     */
    List<Order> getAllOrders(String symbol, Long orderId, LocalDateTime startTime, LocalDateTime endTime, Integer limit);

}
