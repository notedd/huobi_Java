package com.huobi.money;

import com.huobi.Constants;
import com.huobi.client.AlgoClient;
import com.huobi.client.TradeClient;
import com.huobi.client.req.algo.CreateAlgoOrderRequest;
import com.huobi.client.req.trade.OrdersRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.OrderStateEnum;
import com.huobi.constant.enums.OrderTypeEnum;
import com.huobi.constant.enums.algo.AlgoOrderSideEnum;
import com.huobi.constant.enums.algo.AlgoOrderTimeInForceEnum;
import com.huobi.constant.enums.algo.AlgoOrderTypeEnum;
import com.huobi.model.algo.CreateAlgoOrderResult;
import com.huobi.model.trade.Order;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SellStrategyService {

    private static final Logger log = LoggerFactory.getLogger(SellStrategyService.class);

    public void DoStrategy() throws Exception {

        log.info("DoStrategy start");

        List<OrderStateEnum> stateList = new ArrayList<>();
        stateList.add(OrderStateEnum.FILLED);

        List<OrderTypeEnum> typeList = new ArrayList<>();
        typeList.add(OrderTypeEnum.BUY_LIMIT);

        TradeClient tradeService = TradeClient.create(HuobiOptions.builder()
                .apiKey(Constants.API_KEY)
                .secretKey(Constants.SECRET_KEY)
                .build());

        //查完成的单子
        List<Order> ordersList = tradeService.getOrders(OrdersRequest.builder()
                .symbol(Configs.symbol)
                .states(stateList).types(typeList)
                .build());

        ordersList.forEach(order -> {
            System.out.println(new Date(order.getCreatedAt()) + ":" + order.toString());
        });



    }

    public static String getRandomClientOrderId() {
        return "d_" + System.nanoTime() + "_" + RandomStringUtils.randomAlphanumeric(4);
    }

}
