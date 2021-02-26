package com.huobi.money;

import java.math.BigDecimal;

public class Configs {

    public static String symbol = "htusdt";
    public static Long spotAccountId = 15703083L;
    public static Long period = 30000L;
    public static Long initdelay = 1000L;

    //策略是否打开
    public static volatile boolean buyStragegyOpen = true;
    public static volatile boolean sellStragegyOpen = false;
    public static volatile boolean riskStragegyOpen = false;
    public static volatile boolean canelStragegyOpen = false;

    //策略是否在测试阶段 测试阶段不真实下单操作
    public static volatile boolean isBuyStragegyTest = true;
    public static volatile boolean isSellStragegyTest = true;
    public static volatile boolean isCanelStragegyTest = true;
    public static volatile boolean isRiskStragegyTest = true;

    //每次购买最大的价格 ustd 100美金
    public static BigDecimal maxPrice = new BigDecimal("100");


}
