package com.mcsoft.bi.common.bian.spot.api;

import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.BinancePrice;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 现货/交易账户信息
 * Created by MC on 2020/12/4.
 *
 * @author MC
 */
public interface SpotInformationApi {

    /**
     * 获取账户信息，主要是各币种的余额
     *
     * @return 账户信息
     */
    AccountInfo getAccountInformation();

    /**
     * 拉取交易记录列表
     *
     * @param base    需要拉取的币
     * @param counter 拉取币的对手币，和baseCurrency组合成交易对，如ETH,USDT组合成ETHUSDT，作为接口参数symbol传出
     * @param limit   限制查询数量，传null时默认为500，最大为1000
     * @param startId 查询起始id，为BinanceOrder中的orderId
     * @return 交易记录列表
     */
    List<BinanceOrder> getTradeRecords(Currency base, Currency counter, Integer limit, Long startId);

    /**
     * 获取标记价格
     *
     * @param base    需要拉取的币
     * @param counter 拉取币的对手币，和baseCurrency组合成交易对，如ETH,USDT组合成ETHUSDT，作为接口参数symbol传出
     * @return 标记价格
     */
    BinancePrice getSymbolPriceTicker(Currency base, Currency counter);

    /**
     * 获取K线
     *
     * @param base          需要拉取的币
     * @param counter       拉取币的对手币，和baseCurrency组合成交易对，如ETH,USDT组合成ETHUSDT，作为接口参数symbol传出
     * @param interval      K线时间间隔
     * @param limit         拉取数据量
     * @param startDateTime 拉取起始时间
     * @param endDateTime   拉取结束时间
     * @return K线数据列表
     */
    List<BinanceKline> getKline(Currency base, Currency counter, KlineInterval interval, Integer limit, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
