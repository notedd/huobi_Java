package com.huobi.money;

import com.alibaba.fastjson.JSON;
import com.huobi.Constants;
import com.huobi.client.AccountClient;
import com.huobi.client.MarketClient;
import com.huobi.client.TradeClient;
import com.huobi.client.req.account.AccountBalanceRequest;
import com.huobi.client.req.market.CandlestickRequest;
import com.huobi.client.req.market.MarketDetailMergedRequest;
import com.huobi.client.req.trade.CreateOrderRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.CandlestickIntervalEnum;
import com.huobi.model.account.AccountBalance;
import com.huobi.model.market.Candlestick;
import com.huobi.model.market.MarketDetailMerged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BuyStrategyService {

    private static final Logger log = LoggerFactory.getLogger(BuyStrategyService.class);

    AccountClient accountService = AccountClient.create(HuobiOptions.builder()
            .apiKey(Constants.API_KEY)
            .secretKey(Constants.SECRET_KEY)
            .build());

    MarketClient marketClient = MarketClient.create(new HuobiOptions());

    TradeClient tradeService = TradeClient.create(HuobiOptions.builder()
            .apiKey(Constants.API_KEY)
            .secretKey(Constants.SECRET_KEY)
            .build());


    public void DoStrategy() throws Exception {

        log.info("BuyStrategyService start");

        // 策略是否打开
        if (!Configs.buyStragegyOpen) {
            log.info("buyStragegyOpen false");
            return;
        }

        // 获取指定账号可用金额
        AccountBalance accountBalance = accountService.getAccountBalance(AccountBalanceRequest.builder()
                .accountId(Configs.spotAccountId)
                .build());

        AtomicReference<BigDecimal> balanceAmout = new AtomicReference<>();
        accountBalance.getList().forEach(balance -> {
            if (balance.getCurrency().equals(Configs.usdt) && balance.getType().equals("trade")) {
                balanceAmout.set(balance.getBalance());
                return;
            }
        });

        log.info("账号余额:{}", balanceAmout);
        if (balanceAmout.get().compareTo(Configs.minHoldPrice) < 0) {
            log.info("minHoldPrice return");
            return;
        }

        // 获取指定交易对行情数据
        List<Candlestick> list = marketClient.getCandlestick(CandlestickRequest.builder()
                .symbol(Configs.symbol)
                .interval(CandlestickIntervalEnum.MIN15)
                .size(4)
                .build());

        log.info("交易信息:{}", JSON.toJSONString(list));
        log.info("价格信息:{},{},{},{}", list.get(3).getClose(), list.get(2).getClose(), list.get(1).getClose(), list.get(0).getClose());
        // 判断行情是否可以买入
        if (list.get(3).getClose().compareTo(list.get(2).getClose()) < 0 &&
                list.get(2).getClose().compareTo(list.get(1).getClose()) < 0 &&
                list.get(1).getClose().compareTo(list.get(0).getClose()) < 0) {
            log.info("可以买入");
        } else {
            log.info("不可买入");
            return;
        }

        // 构建买入参数
        MarketDetailMerged marketDetailMerged = marketClient.getMarketDetailMerged(MarketDetailMergedRequest.builder().symbol(Configs.symbol).build());
        BigDecimal askPrice = marketDetailMerged.getAsk().getPrice();

        //买入数量 向下取整
        BigDecimal amount = Configs.perMaxPrice.divide(askPrice, 0, RoundingMode.DOWN);
        CreateOrderRequest sellLimitRequest = CreateOrderRequest.spotSellLimit(Configs.spotAccountId, Configs.symbol, askPrice, amount);
        log.info("下单数据:{}", JSON.toJSONString(sellLimitRequest));


        // 下单买入 回测阶段不实际下单 记录下单数据到日志 后面分析
        if (Configs.isBuyStragegyTest) {
            log.info("isBuyStragegyTest true");
            return;
        } else {
            Long sellLimitId = tradeService.createOrder(sellLimitRequest);
            log.info("下单成功id = {}", sellLimitId);
        }


    }
}
