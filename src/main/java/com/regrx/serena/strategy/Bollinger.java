package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.network.HistoryDownloader;
import com.regrx.serena.common.utils.Calculator;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.HistoryData;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.statistic.MovingAverage;

import java.util.*;


public class Bollinger extends AbstractStrategy {
    private final String type;
    private boolean active; // need log
    private boolean isUp;   // need log
    private boolean aboveLowerBound;
    private boolean belowUpperBound;
    private boolean enableProfitLimit;  // need log
    private double tradeInPrice;    // need log
    private double peekProfitPrice; // need log
    private boolean NST;    // need log
    private ArrayList<Integer> options;

    public Bollinger(IntervalEnum interval, String type) {
        super(interval, Setting.DEFAULT_PRIORITY);  // TODO: refine priority
        super.setName("Bollinger");

        this.type = type;
        this.NST = false;
    }

    @Override
    public Decision execute(ExPrice price) {
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_BOLLINGER, interval);
        MovingAverage currentMA = dataSvcMgr.queryData(interval).getNewMAvg();
        LinkedList<MovingAverage> mAvgs = dataSvcMgr.queryData(interval).getMAvgs();
        if (mAvgs.size() == 0) {
            return decision;
        }

        double midLine = currentMA.getMAByIndex(Setting.BOLLINGER_MA_BASE);
        ArrayList<Double> MASequence = new ArrayList<>();
        ListIterator<MovingAverage> iterator = mAvgs.listIterator(0);
        for (int i = 0; i < Setting.BOLLINGER_AGGREGATE_COUNT; i++) {
            if (iterator.hasNext()) {
                MASequence.add(iterator.next().getMAByIndex(Setting.BOLLINGER_MA_BASE));
            } else {
                LogUtil.getInstance().warning("MA Sequence not enough...");
                break;
            }
        }

        double stdDeviation = Calculator.standardDeviation(MASequence);
        double upperBound = midLine + stdDeviation * Setting.BOLLINGER_DEVIATION_MULTIPLIER;
        double lowerBound = midLine - stdDeviation * Setting.BOLLINGER_DEVIATION_MULTIPLIER;

        int count = 0;
        HistoryData lastMinuteInfo = HistoryDownloader.getHistoryDataByTime(type, interval, price.getTime());
        while (lastMinuteInfo == null) {
            if (count > 10) {
                LogUtil.getInstance().severe("Get Highest Price Failed...");
                return decision;
            }
            try {
                Thread.sleep(800);
                count++;
            } catch (Exception ignored) {
            }
            lastMinuteInfo = HistoryDownloader.getHistoryDataByTime(type, interval, price.getTime());
        }

        for (Integer option : options) {
            switch (option) {
                case StrategyOption.BollingerLongByDefault:
                    longByDefault(decision, price, lowerBound);
                    break;
                case StrategyOption.BollingerLongByTail:
                    longByTail(decision, price, lastMinuteInfo, lowerBound);
                    break;
                case StrategyOption.BollingerShortByDefault:
                    shortByDefault(decision, price, upperBound);
                    break;
                case StrategyOption.BollingerShortByTail:
                    shortByTail(decision, price, lastMinuteInfo, upperBound);
                    break;
                case StrategyOption.BollingerLongCoverByFallback:
                    bollingerLongCoverByFallback(decision, price, midLine);
                    break;
                case StrategyOption.BollingerLongCoverByLose:
                    bollingerLongCoverByLose(decision, price);
                    break;
                case StrategyOption.BollingerShortCoverByFallback:
                    bollingerShortCoverByFallback(decision, price, midLine);
                    break;
                case StrategyOption.BollingerShortCoverByLose:
                    bollingerShortCoverByLose(decision, price);
                    break;
                default:
                    break;
            }
        }
        if (options.contains(StrategyOption.DefaultNST)) {
            NST(decision);
        }

        if (decision.getTradingType() == TradingType.NO_ACTION) {
            aboveLowerBound = price.getPrice() > lowerBound;
            belowUpperBound = price.getPrice() < upperBound;
        }

        return decision;
    }

    public Bollinger withOption(Integer option) {
        options.add(option);
        return this;
    }

    private void longByDefault(Decision decision, ExPrice price, double lower) {
        if (NST && isUp) {
            return;
        }
        if (!active && aboveLowerBound && price.getPrice() < lower) {
            aboveLowerBound = false;
            decision.make(TradingType.PUT_BUYING, "B1");
            active = true;
            isUp = true;
            NST = false;
            tradeInPrice = price.getPrice();
        }
    }

    private void shortByDefault(Decision decision, ExPrice price, double upper) {
        if (NST && !isUp) {
            return;
        }
        if (!active && belowUpperBound && price.getPrice() > upper) {
            belowUpperBound = false;
            decision.make(TradingType.SHORT_SELLING, "S1");
            active = true;
            isUp = false;
            NST = false;
            tradeInPrice = price.getPrice();
        }
    }

    private void longByTail(Decision decision, ExPrice price, HistoryData lastMinuteInfo, double lower) {
        if (NST && isUp) {
            return;
        }
        double tail = Math.abs(lastMinuteInfo.getLowestPrice() - lastMinuteInfo.getClosePrice());
        double total = Math.abs(lastMinuteInfo.getLowestPrice() - lastMinuteInfo.getHighestPrice());
        if (!active &&
                price.getPrice() < lower &&
                tail / total > Setting.BOLLINGER_B_PRICE_RATIO &&
                total > Setting.BOLLINGER_B_PRICE_REFERENCE) {
            decision.make(TradingType.PUT_BUYING, "B2");
            active = true;
            isUp = true;
            NST = false;
            tradeInPrice = price.getPrice();
        }
    }

    private void shortByTail(Decision decision, ExPrice price, HistoryData lastMinuteInfo, double upper) {
        if (NST && isUp) {
            return;
        }
        double tail = Math.abs(lastMinuteInfo.getHighestPrice() - lastMinuteInfo.getClosePrice());
        double total = Math.abs(lastMinuteInfo.getLowestPrice() - lastMinuteInfo.getHighestPrice());
        if (!active &&
                price.getPrice() > upper &&
                tail / total > Setting.BOLLINGER_S_PRICE_RATIO &&
                total > Setting.BOLLINGER_S_PRICE_REFERENCE) {
            decision.make(TradingType.SHORT_SELLING, "S2");
            active = true;
            isUp = false;
            NST = false;
            tradeInPrice = price.getPrice();
        }
    }

    private void bollingerLongCoverByFallback(Decision decision, ExPrice price, double mid) {
        if(active && isUp) {
            if(!enableProfitLimit && price.getPrice() > mid) {
                enableProfitLimit = true;
                peekProfitPrice = price.getPrice();
            }
            if (enableProfitLimit) {
                peekProfitPrice = Math.max(peekProfitPrice, price.getPrice());
                if (peekProfitPrice - price.getPrice() > Setting.BOLLINGER_B_FALLBACK) {
                    decision.make(TradingType.EMPTY, "LC1");
                    active = false;
                    enableProfitLimit = false;
                    peekProfitPrice = 0.0;
                    tradeInPrice = 0.0;
                }
            }
        }
    }


    private void bollingerShortCoverByFallback(Decision decision, ExPrice price, double mid) {
        if(active && !isUp) {
            if(!enableProfitLimit && price.getPrice() < mid) {
                enableProfitLimit = true;
                peekProfitPrice = price.getPrice();
            }
            if (enableProfitLimit) {
                peekProfitPrice = Math.min(peekProfitPrice, price.getPrice());
                if (price.getPrice() - peekProfitPrice > Setting.BOLLINGER_S_FALLBACK) {
                    decision.make(TradingType.EMPTY, "SC1");
                    active = false;
                    enableProfitLimit = false;
                    peekProfitPrice = 0.0;
                    tradeInPrice = 0.0;
                }
            }
        }
    }

    private void bollingerLongCoverByLose(Decision decision, ExPrice price) {
        if(active && isUp) {
            double profit = Math.abs(price.getPrice() - tradeInPrice);
            if (profit < Setting.BOLLINGER_B_LOSE_LIMIT) {
                decision.make(TradingType.EMPTY, "LC2");
                active = false;
                tradeInPrice = 0.0;
            }
        }
    }

    private void bollingerShortCoverByLose(Decision decision, ExPrice price) {
        if(active && !isUp) {
            double profit = Math.abs(tradeInPrice - price.getPrice());
            if (profit < Setting.BOLLINGER_S_LOSE_LIMIT) {
                decision.make(TradingType.EMPTY, "SC2");
                active = false;
                tradeInPrice = 0.0;
            }
        }
    }

    private void NST(Decision decision) {
        if (!NST && decision.getTradingType() == TradingType.EMPTY) {
            NST = true;
        }
    }
}
