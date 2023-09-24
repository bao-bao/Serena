package SerenaSimulation.strategy;

import SerenaSimulation.DataServiceManagerTest;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.MAEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.network.HistoryDownloader;
import com.regrx.serena.common.utils.Calculator;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.HistoryData;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.statistic.MovingAverage;
import com.regrx.serena.strategy.StrategyOption;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;


public class Bollinger extends AbstractStrategy {
    private boolean active; // need log
    private boolean isUp;   // need log
    private boolean belowLowerBound;
    private boolean aboveUpperBound;
    private boolean enableProfitLimit;  // need log
    private double tradeInPrice;    // need log
    private double peekProfitPrice; // need log
    private boolean NST;    // need log
    private final ArrayList<Integer> options;

    public Bollinger(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_PRIORITY);  // TODO: refine priority
        super.setName("Bollinger");

        options = new ArrayList<>();
        this.NST = false;
    }

    @Override
    public Decision execute(ExPrice price) {
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_BOLLINGER, interval);
        MovingAverage currentMA = DataServiceManagerTest.getInstance().queryData(interval).getNewMAvg();
        LinkedList<ExPrice> prices = DataServiceManagerTest.getInstance().queryData(interval).getPrices();
        if (prices.size() == 0) {
            return decision;
        }

        double midLine = currentMA.getMAByAggrCount(Setting.BOLLINGER_AGGREGATE_COUNT);
        ArrayList<Double> CloseSequence = new ArrayList<>();
        ListIterator<ExPrice> iterator = prices.listIterator(0);
        for (int i = 0; i < Setting.BOLLINGER_AGGREGATE_COUNT; i++) {
            if (iterator.hasNext()) {
                ExPrice historyPrice = iterator.next();
                CloseSequence.add(historyPrice.getPrice());
            } else {
                return decision;
            }
        }

        double stdDeviation = Calculator.standardDeviation(CloseSequence);
        double upperBound = midLine + (stdDeviation * Setting.BOLLINGER_DEVIATION_MULTIPLIER);
        double lowerBound = midLine - (stdDeviation * Setting.BOLLINGER_DEVIATION_MULTIPLIER);

        int count = 0;
        HistoryData lastMinuteInfo = new HistoryData();
        lastMinuteInfo.setClosePrice(price.getPrice());
        lastMinuteInfo.setHighestPrice(price.getHighest());
        lastMinuteInfo.setLowestPrice(price.getLowest());

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
                    bollingerLongCoverByFallback(decision, price, lastMinuteInfo);
                    break;
                case StrategyOption.BollingerLongCoverByLose:
                    bollingerLongCoverByLose(decision, price);
                    break;
                case StrategyOption.BollingerShortCoverByFallback:
                    bollingerShortCoverByFallback(decision, price, lastMinuteInfo);
                    break;
                case StrategyOption.BollingerShortCoverByLose:
                    bollingerShortCoverByLose(decision, price);
                    break;
                default:
                    break;
            }
            if (decision.isExecute()) {
                break;
            }
        }
        if (options.contains(StrategyOption.DefaultNST)) {
            NST(decision);
        }

        if (decision.getTradingType() == TradingType.NO_ACTION) {
            belowLowerBound = price.getPrice() < lowerBound;
            aboveUpperBound = price.getPrice() > upperBound;
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
        if (!active && belowLowerBound && price.getPrice() > lower) {
            belowLowerBound = false;
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
        if (!active && aboveUpperBound && price.getPrice() < upper) {
            aboveUpperBound = false;
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
//        if (!active &&
//                price.getPrice() < lower &&
//                tail / total > Setting.BOLLINGER_B_PRICE_RATIO &&
//                total > Setting.BOLLINGER_B_PRICE_REFERENCE) {
        if (!active &&
                price.getPrice() < lower &&
                tail >= Setting.BOLLINGER_B_PRICE_REFERENCE) {
            decision.make(TradingType.PUT_BUYING, "B2");
            active = true;
            isUp = true;
            NST = false;
            tradeInPrice = price.getPrice();
        }
    }

    private void shortByTail(Decision decision, ExPrice price, HistoryData lastMinuteInfo, double upper) {
        if (NST && !isUp) {
            return;
        }
        double tail = Math.abs(lastMinuteInfo.getHighestPrice() - lastMinuteInfo.getClosePrice());
        double total = Math.abs(lastMinuteInfo.getLowestPrice() - lastMinuteInfo.getHighestPrice());
//        if (!active &&
//                price.getPrice() > upper &&
//                tail / total > Setting.BOLLINGER_S_PRICE_RATIO &&
//                total > Setting.BOLLINGER_S_PRICE_REFERENCE) {
        if (!active &&
                price.getPrice() > upper &&
                tail > Setting.BOLLINGER_S_PRICE_REFERENCE) {
            decision.make(TradingType.SHORT_SELLING, "S2");
            active = true;
            isUp = false;
            NST = false;
            tradeInPrice = price.getPrice();
        }
    }

    private void bollingerLongCoverByFallback(Decision decision, ExPrice price, HistoryData lastMinuteInfo) {
        if (active && isUp) {
            if (!enableProfitLimit) {
                enableProfitLimit = true;
                peekProfitPrice = lastMinuteInfo.getHighestPrice();
            }
            if (enableProfitLimit) {
                peekProfitPrice = Math.max(peekProfitPrice, lastMinuteInfo.getHighestPrice());
                double peekProfit = peekProfitPrice - tradeInPrice;
                double profit = price.getPrice() - tradeInPrice;
                if (peekProfit > Setting.BOLLINGER_B_PROFIT_TREAT &&
                        profit / peekProfit < Setting.BOLLINGER_B_FALLBACK) {
                    decision.make(TradingType.EMPTY, "LC1");
                    bollingerReset();
                }
            }
        }
    }


    private void bollingerShortCoverByFallback(Decision decision, ExPrice price, HistoryData lastMinuteInfo) {
        if (active && !isUp) {
            if (!enableProfitLimit) {
                enableProfitLimit = true;
                peekProfitPrice = lastMinuteInfo.getLowestPrice();
            }
            if (enableProfitLimit) {
                peekProfitPrice = Math.min(peekProfitPrice, lastMinuteInfo.getLowestPrice());
                double peekProfit = tradeInPrice - peekProfitPrice;
                double profit = tradeInPrice - price.getPrice();
                if (peekProfit > Setting.BOLLINGER_S_PROFIT_TREAT &&
                        profit / peekProfit < Setting.BOLLINGER_S_FALLBACK) {
                    decision.make(TradingType.EMPTY, "SC1");
                    bollingerReset();
                }
            }
        }
    }

    private void bollingerLongCoverByLose(Decision decision, ExPrice price) {
        if (active && isUp) {
            double profit = price.getPrice() - tradeInPrice;
            if (profit < 0 - Setting.BOLLINGER_B_LOSE_LIMIT) {
                decision.make(TradingType.EMPTY, "LC2");
                bollingerReset();
            }
        }
    }

    private void bollingerShortCoverByLose(Decision decision, ExPrice price) {
        if (active && !isUp) {
            double profit = tradeInPrice - price.getPrice();
            if (profit < 0 - Setting.BOLLINGER_S_LOSE_LIMIT) {
                decision.make(TradingType.EMPTY, "SC2");
                bollingerReset();
            }
        }
    }

    private void NST(Decision decision) {
        if (!NST && decision.getTradingType() == TradingType.EMPTY) {
            NST = true;
        }
    }

    private void bollingerReset() {
        active = false;
        enableProfitLimit = false;
        peekProfitPrice = 0.0;
        tradeInPrice = 0.0;
    }
}
