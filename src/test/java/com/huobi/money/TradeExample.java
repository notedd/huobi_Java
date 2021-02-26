package com.huobi.money;

import com.huobi.Constants;
import com.huobi.client.MarketClient;
import com.huobi.client.TradeClient;
import com.huobi.client.req.market.MarketDetailMergedRequest;
import com.huobi.client.req.trade.*;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.*;
import com.huobi.model.market.MarketDetailMerged;
import com.huobi.model.market.MbpIncrementalUpdateEvent;
import com.huobi.model.market.PriceLevel;
import com.huobi.model.trade.*;
import org.apache.commons.lang.RandomStringUtils;

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


        MarketClient marketClient = MarketClient.create(HuobiOptions.builder()
                .apiKey(Constants.API_KEY)
                .secretKey(Constants.SECRET_KEY)
                .build());
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


        List<Order> ordersList = tradeService.getOrders(OrdersRequest.builder()
                .symbol(symbol)
                .states(stateList).types(typeList)
                .build());

        ordersList.forEach(order -> {
            System.out.println(new Date(order.getCreatedAt()) + ":" + order.toString());
        });


    }
}
