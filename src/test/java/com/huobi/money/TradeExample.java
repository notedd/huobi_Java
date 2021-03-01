package com.huobi.money;

import com.huobi.Constants;
import com.huobi.client.AlgoClient;
import com.huobi.client.TradeClient;
import com.huobi.client.req.algo.CreateAlgoOrderRequest;
import com.huobi.client.req.algo.GetHistoryAlgoOrdersRequest;
import com.huobi.client.req.algo.GetOpenAlgoOrdersRequest;
import com.huobi.client.req.trade.OrdersRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.OrderStateEnum;
import com.huobi.constant.enums.OrderTypeEnum;
import com.huobi.constant.enums.algo.AlgoOrderSideEnum;
import com.huobi.constant.enums.algo.AlgoOrderStatusEnum;
import com.huobi.constant.enums.algo.AlgoOrderTimeInForceEnum;
import com.huobi.constant.enums.algo.AlgoOrderTypeEnum;
import com.huobi.model.algo.CreateAlgoOrderResult;
import com.huobi.model.algo.GetHistoryAlgoOrdersResult;
import com.huobi.model.algo.GetOpenAlgoOrdersResult;
import com.huobi.model.trade.Order;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeExample {

    private static final Logger log = LoggerFactory.getLogger(SellStrategyService.class);

    public static void main(String[] args) {

        TradeClient tradeService = TradeClient.create(HuobiOptions.builder()
                .apiKey(Constants.API_KEY)
                .secretKey(Constants.SECRET_KEY)
                .build());

        //查当天交易完成的buy_limit单子
        List<OrderStateEnum> stateList = new ArrayList<>();
        stateList.add(OrderStateEnum.FILLED);
        List<OrderTypeEnum> typeList = new ArrayList<>();
        typeList.add(OrderTypeEnum.BUY_LIMIT);
        List<Order> ordersList = tradeService.getOrders(OrdersRequest.builder()
                .symbol(Configs.symbol)
                .states(stateList).types(typeList)
                .build());

        Map<Long, Order> byLimitOrderMap = new HashMap<Long, Order>();
        ordersList.forEach(order -> {
            log.info("buylimitorder {}", order.toString());
            byLimitOrderMap.put(order.getId(), order);
        });
        if (byLimitOrderMap.entrySet().isEmpty()) {
            log.info("当天没有需要止盈止损的买单");
            return;
        }

        AlgoClient algoClient = AlgoClient.create(HuobiOptions.builder().apiKey(Constants.API_KEY).secretKey(Constants.SECRET_KEY).build());

        //查询当天open的委托单
        GetOpenAlgoOrdersResult getOpenAlgoOrdersResult = algoClient.getOpenAlgoOrders(GetOpenAlgoOrdersRequest.builder().build());
        Map<String, String> openAlgoOrderMap = new HashMap<String, String>();
        getOpenAlgoOrdersResult.getList().forEach(order -> {
            log.info("openalogorder {}", order.toString());
            openAlgoOrderMap.put(order.getClientOrderId(), null);
        });

        //查询历史的委托单
        GetHistoryAlgoOrdersRequest getHistoryAlgoOrdersRequest = GetHistoryAlgoOrdersRequest.builder()
                .orderStatus(AlgoOrderStatusEnum.TRIGGERED)
                .orderSide(AlgoOrderSideEnum.SELL)
                .orderType(AlgoOrderTypeEnum.LIMIT)
                .symbol(Configs.symbol)
                .build();
        GetHistoryAlgoOrdersResult getHistoryAlgoOrdersResult = algoClient.getHistoryAlgoOrders(getHistoryAlgoOrdersRequest);
        Map<String, String> hisAlgoOrderMap = new HashMap<String, String>();
        getHistoryAlgoOrdersResult.getList().forEach(order -> {
            log.info("hisalogorder {}", order.toString());
            hisAlgoOrderMap.put(order.getClientOrderId(), null);
        });

        log.info("byLimitOrderMap={}", byLimitOrderMap);
        log.info("openAlgoOrderMap={}", openAlgoOrderMap);
        log.info("hisAlgoOrderMap={}", hisAlgoOrderMap);

        //找出需要补的止赢止损订单号
        Map<String, Order> zhiYingMap = new HashMap<>();
        Map<String, Order> zhiSunMap = new HashMap<>();

        byLimitOrderMap.keySet().forEach(orderid -> {
            String zhiYingOrder = orderid + "0001";
            String zhiSunOrder = orderid + "0002";
            if (openAlgoOrderMap.keySet().contains(zhiYingOrder) || hisAlgoOrderMap.keySet().contains(zhiYingOrder)) {

            } else {
                zhiYingMap.put(zhiYingOrder, byLimitOrderMap.get(orderid));
            }
            if (openAlgoOrderMap.keySet().contains(zhiSunOrder) || hisAlgoOrderMap.keySet().contains(zhiSunOrder)) {

            } else {
                zhiSunMap.put(zhiSunOrder, byLimitOrderMap.get(orderid));
            }
        });

        log.info("zhiYingMap={}", zhiYingMap);
        log.info("zhiSunMap={}", zhiSunMap);

        for (Map.Entry<String, Order> entry : zhiYingMap.entrySet()) {
            try {
                createZhiYingOrder(entry.getKey(), entry.getValue(), algoClient);
            } catch (Exception e) {
                log.error("", e);
            }
        }

        for (Map.Entry<String, Order> entry : zhiSunMap.entrySet()) {
            try {
                createZhiSunOrder(entry.getKey(), entry.getValue(), algoClient);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public static void createZhiYingOrder(String orderid, Order order, AlgoClient algoClient) {

        BigDecimal orderPrice = order.getPrice().multiply(new BigDecimal(1).add(Configs.perFeeScale).add(Configs.perFeeScale).add(Configs.zhiYingScale)).setScale(4, RoundingMode.DOWN);
        BigDecimal stopPrice = order.getPrice().multiply(new BigDecimal(1).add(Configs.perFeeScale).add(Configs.perFeeScale).add(Configs.zhiYingScale)).setScale(4, RoundingMode.DOWN);

        CreateAlgoOrderRequest createAlgoOrderRequest = CreateAlgoOrderRequest.builder()
                .clientOrderId(orderid)
                .accountId(Configs.spotAccountId)
                .symbol(Configs.symbol)
                .orderPrice(orderPrice)
                .orderSize(order.getFilledAmount().subtract(order.getFilledFees()).setScale(2, RoundingMode.DOWN))
                .orderSide(AlgoOrderSideEnum.SELL)
                .orderType(AlgoOrderTypeEnum.LIMIT)
                .timeInForce(AlgoOrderTimeInForceEnum.GTC)
                .stopPrice(stopPrice)
                .build();

        log.info("create order request:" + createAlgoOrderRequest);
        CreateAlgoOrderResult createAlgoOrderResult = algoClient.createAlgoOrder(createAlgoOrderRequest);
        log.info("create order result:" + createAlgoOrderResult);

    }

    public static void createZhiSunOrder(String orderid, Order order, AlgoClient algoClient) {

        BigDecimal orderPrice = order.getPrice().multiply(new BigDecimal(1).add(Configs.perFeeScale).add(Configs.perFeeScale).subtract(Configs.zhiSunScale)).setScale(4, RoundingMode.DOWN);
        BigDecimal stopPrice = order.getPrice().multiply(new BigDecimal(1).add(Configs.perFeeScale).add(Configs.perFeeScale).subtract(Configs.zhiSunScale)).setScale(4, RoundingMode.DOWN);

        CreateAlgoOrderRequest createAlgoOrderRequest = CreateAlgoOrderRequest.builder()
                .clientOrderId(orderid)
                .accountId(Configs.spotAccountId)
                .symbol(Configs.symbol)
                .orderPrice(orderPrice)
                .orderSize(order.getFilledAmount().subtract(order.getFilledFees()).setScale(2, RoundingMode.DOWN))
                .orderSide(AlgoOrderSideEnum.SELL)
                .orderType(AlgoOrderTypeEnum.LIMIT)
                .timeInForce(AlgoOrderTimeInForceEnum.GTC)
                .stopPrice(stopPrice)
                .build();

        log.info("create order request:" + createAlgoOrderRequest);
        CreateAlgoOrderResult createAlgoOrderResult = algoClient.createAlgoOrder(createAlgoOrderRequest);
        log.info("create order result:" + createAlgoOrderResult);

    }

    public static String getRandomClientOrderId() {
        return "d_" + System.nanoTime() + "_" + RandomStringUtils.randomAlphanumeric(4);
    }
}
