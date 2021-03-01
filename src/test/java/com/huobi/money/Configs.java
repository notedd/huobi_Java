package com.huobi.money;

import java.math.BigDecimal;

public class Configs {

    public static String symbol = "htusdt";
    public static String usdt = "usdt";
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

    //风控是否关闭下单
    public static volatile boolean isBuyStragegyRisk = true;

    //每次购买最大的价格 usdt 100美金
    public static BigDecimal perMaxPrice = new BigDecimal("100");

    //账户余额最低保留金额 usdt 1000美金
    public static BigDecimal minHoldPrice = new BigDecimal("1400");

    //交易手续费比例 0.2%
    public static BigDecimal perFeeScale = new BigDecimal("0.002");

    //每次卖出止赢比例 2%
    public static BigDecimal zhiYingScale = new BigDecimal("0.02");

    //每次卖出止损比例 5%
    public static BigDecimal zhiSunScale = new BigDecimal("0.05");



}
