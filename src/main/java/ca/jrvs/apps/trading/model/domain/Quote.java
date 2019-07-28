package ca.jrvs.apps.trading.model.domain;

public class Quote implements Entity<String> {

  private String ticker;
  private Double lastPrice;
  private Double bidPrice;
  private Integer bidSize;
  private Double askPrice;
  private Integer askSize;

  @Override
  public String getId() {
    return ticker;
  }

  @Override
  public void setId(String id) {
    this.ticker = id;
  }

  public String getTicker() {
    return ticker == null ? null : ticker.toUpperCase();
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public Double getLastPrice() {
    return lastPrice;
  }

  public void setLastPrice(Double lastPrice) {
    this.lastPrice = lastPrice;
  }

  public Double getBidPrice() {
    return bidPrice;
  }

  public void setBidPrice(Double bidPrice) {
    this.bidPrice = bidPrice;
  }

  public Integer getBidSize() {
    return bidSize;
  }

  public void setBidSize(Integer bidSize) {
    this.bidSize = bidSize;
  }

  public Double getAskPrice() {
    return askPrice;
  }

  public void setAskPrice(Double askPrice) {
    this.askPrice = askPrice;
  }

  public Integer getAskSize() {
    return askSize;
  }

  public void setAskSize(Integer askSize) {
    this.askSize = askSize;
  }
}
