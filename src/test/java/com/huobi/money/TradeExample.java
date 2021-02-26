package com.huobi.money;

import com.huobi.Constants;
import com.huobi.client.AlgoClient;
import com.huobi.client.MarketClient;
import com.huobi.client.TradeClient;
import com.huobi.client.req.algo.CreateAlgoOrderRequest;
import com.huobi.client.req.market.MarketDetailMergedRequest;
import com.huobi.client.req.trade.*;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.*;
import com.huobi.constant.enums.algo.AlgoOrderSideEnum;
import com.huobi.constant.enums.algo.AlgoOrderTimeInForceEnum;
import com.huobi.constant.enums.algo.AlgoOrderTypeEnum;
import com.huobi.model.algo.CreateAlgoOrderResult;
import com.huobi.model.market.MarketDetailMerged;
import com.huobi.model.market.MbpIncrementalUpdateEvent;
import com.huobi.model.market.PriceLevel;
import com.huobi.model.trade.*;
import com.huobi.service.huobi.utils.DataUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TradeExample {

    public static void main(String[] args) {

        String symbol = "htusdt";
        Long spotAccountId = 15703083L;

        TradeClient tradeService = TradeClient.create(HuobiOptions.builder()
                .apiKey(Constants.API_KEY)
                .secretKey(Constants.SECRET_KEY)
                .build());

        // 构造委托卖单
        AlgoClient algoClient = AlgoClient.create(HuobiOptions.builder().apiKey(Constants.API_KEY).secretKey(Constants.SECRET_KEY).build());

//
//
//        String clientOrderId = "T" + System.nanoTime() + "_" + RandomStringUtils.randomAlphanumeric(4);
//        MarketDetailMerged marketDetailMerged = marketClient.getMarketDetailMerged(MarketDetailMergedRequest.builder().symbol(symbol).build());
//        BigDecimal askPrice = marketDetailMerged.getAsk().getPrice();
//        System.out.println(askPrice);


//
//        CreateOrderRequest sellLimitRequest = CreateOrderRequest.spotSellLimit(spotAccountId, symbol, askPrice, new BigDecimal("2"));
//        Long sellLimitId = tradeService.createOrder(sellLimitRequest);
//        System.out.println("create sell-limit order:" + sellLimitId);
//
//        Order clientOrder = tradeService.getOrder(sellLimitId);
//        System.out.println(clientOrder.toString());

        // 查未完成的单子
//        List<Order> orderList = tradeService.getOpenOrders(OpenOrdersRequest.builder()
//                .accountId(spotAccountId)
//                .symbol(symbol)
//                .build());
//
//        orderList.forEach(order -> {
//            System.out.println(order.toString());
//        });

        List<OrderStateEnum> stateList = new ArrayList<>();
        stateList.add(OrderStateEnum.FILLED);

        List<OrderTypeEnum> typeList = new ArrayList<>();
        typeList.add(OrderTypeEnum.BUY_LIMIT);

        //查完成的单子
        List<Order> ordersList = tradeService.getOrders(OrdersRequest.builder()
                .symbol(symbol)
                .states(stateList).types(typeList)
                .build());

        Order order = ordersList.get(0);
        System.out.println(new Date(order.getCreatedAt()) + ":" + order.toString());
        BigDecimal orderPrice = order.getPrice().multiply(new BigDecimal(1).add(Configs.perFeeScale).add(Configs.perFeeScale).add(Configs.zhiYingScale));
        BigDecimal stopPrice = order.getPrice().multiply(new BigDecimal(1).add(Configs.perFeeScale).add(Configs.perFeeScale).add(Configs.test));

        CreateAlgoOrderRequest createAlgoOrderRequest = CreateAlgoOrderRequest.builder()
                .clientOrderId(getRandomClientOrderId())
                .accountId(Configs.spotAccountId)
                .symbol(Configs.symbol)
                .orderPrice(orderPrice)
                .orderSize(order.getFilledAmount().subtract(order.getFilledFees()))
                .orderSide(AlgoOrderSideEnum.SELL)
                .orderType(AlgoOrderTypeEnum.LIMIT)
                .timeInForce(AlgoOrderTimeInForceEnum.GTC)
                .stopPrice(stopPrice)
                .clientOrderId(order.getId().toString())
                .build();

        System.out.println("create order result:" + createAlgoOrderRequest);

        CreateAlgoOrderResult createAlgoOrderResult = algoClient.createAlgoOrder(createAlgoOrderRequest);
        System.out.println("create order result:" + createAlgoOrderResult);

    }

    public static String getRandomClientOrderId() {
        return "d_" + System.nanoTime() + "_" + RandomStringUtils.randomAlphanumeric(4);
    }
}
