package application;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.params.CancelOrderByCurrencyPair;
import org.knowm.xchange.service.trade.params.CancelOrderByIdParams;

public class CancelOrderIDAndPair implements CancelOrderByIdParams, CancelOrderByCurrencyPair {
  private final String orderId;
  private final CurrencyPair pair;

  public CancelOrderIDAndPair(CurrencyPair pair, String orderId) {
    this.pair = pair;
    this.orderId = orderId;
  }

  @Override
  public CurrencyPair getCurrencyPair() {
    return pair;
  }

  @Override
  public String getOrderId() {
    return orderId;
  }
}