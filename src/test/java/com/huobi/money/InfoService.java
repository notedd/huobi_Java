package com.huobi.money;

import com.huobi.Constants;
import com.huobi.client.AccountClient;
import com.huobi.client.MarketClient;
import com.huobi.client.TradeClient;
import com.huobi.constant.HuobiOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoService {

    private static final Logger log = LoggerFactory.getLogger(InfoService.class);

    MarketClient marketClient = MarketClient.create(new HuobiOptions());

    AccountClient accountService = AccountClient.create(HuobiOptions.builder()
            .apiKey(Constants.API_KEY)
            .secretKey(Constants.SECRET_KEY)
            .build());

    TradeClient tradeService = TradeClient.create(HuobiOptions.builder()
            .apiKey(Constants.API_KEY)
            .secretKey(Constants.SECRET_KEY)
            .build());


}
