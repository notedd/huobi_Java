package com.huobi.money;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InitMain {

    private static final Logger log = LoggerFactory.getLogger(InitMain.class);

    public static void main(String[] args) {

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
        BuyStrategyService buyStrategyService = new BuyStrategyService();
        CanelStrategyService canelStrategyService = new CanelStrategyService();
        RiskStrategyService riskStrategyService = new RiskStrategyService();
        SellStrategyService sellStrategyService = new SellStrategyService();

//        try {
//            buyStrategyService.DoStrategy();
//        } catch (Exception e) {
//            log.error("error", e);
//        }

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                log.info("买单策略---------------------------");
                buyStrategyService.DoStrategy();
            } catch (Exception e) {
                log.info("买单策略异常退出", e);
            }
        }, Configs.initdelay, Configs.period, TimeUnit.MILLISECONDS);
//
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            try {
//                log.info("卖单策略---------------------------");
//                sellStrategyService.DoStrategy();
//            } catch (Exception e) {
//                log.info("卖单策略", e);
//            }
//
//        }, Configs.initdelay, Configs.period, TimeUnit.MILLISECONDS);
//
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            try {
//                log.info("撤单策略---------------------------");
//                canelStrategyService.DoStrategy();
//            } catch (Exception e) {
//                log.info("撤单策略", e);
//            }
//
//        }, Configs.initdelay, Configs.period, TimeUnit.MILLISECONDS);
//
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            try {
//                log.info("风控策略---------------------------");
//                riskStrategyService.DoStrategy();
//            } catch (Exception e) {
//                log.info("风控策略", e);
//            }
//
//        }, Configs.initdelay, Configs.period, TimeUnit.MILLISECONDS);

    }
}
