package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class QuoteService {

  private QuoteDao quoteDao;
  private MarketDataDao marketDataDao;

  @Autowired
  public QuoteService(QuoteDao quoteDao, MarketDataDao marketDataDao) {
    this.quoteDao = quoteDao;
    this.marketDataDao = marketDataDao;
  }

  /**
   * Helper method. Map a IexQuote to a Quote entity.
   * Note: `iexQuote.getLatestPrice() == null` if the stock market is closed.
   * Make sure set a default value for number field(s).
   */
  public static Quote buildQuoteFromIexQuote(IexQuote iexQuote) {
    Quote quote = new Quote();
    quote.setTicker(iexQuote.getSymbol());
    quote.setLastPrice(
        Double.parseDouble(Optional.ofNullable(iexQuote.getLatestPrice()).orElse("0")));
    quote.setAskPrice(
        Double.parseDouble(Optional.ofNullable(iexQuote.getIexAskPrice()).orElse("0")));
    quote.setBidPrice(
        Double.parseDouble(Optional.ofNullable(iexQuote.getIexBidPrice()).orElse("0")));
    quote.setBidSize(Integer.parseInt(Optional.ofNullable(iexQuote.getIexBidSize()).orElse("0")));
    quote.setAskSize(Integer.parseInt(Optional.ofNullable(iexQuote.getIexAskSize()).orElse("0")));
    return quote;
  }

  /**
   * Add a list of new tickers to the quote table. Skip existing ticker(s).
   *  - Get iexQuote(s)
   *  - convert each iexQuote to Quote entity
   *  - persist the quote to db
   *
   * @param tickers a list of tickers/symbols
   * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public void initQuotes(List<String> tickers) {
    List<IexQuote> securityList = tickers.stream().map(marketDataDao::findIexQuoteByTicker)
        .collect(Collectors.toList());
    List<Quote> quotes = securityList.stream().map(QuoteService::buildQuoteFromIexQuote)
        .collect(Collectors.toList());

    quotes.forEach(quote -> {
      if (!quoteDao.existsById(quote.getId())) {
        quoteDao.save(quote);
      }
    });
  }

  /**
   * Add a new ticker to the quote table. Skip existing ticker.
   *
   * @param ticker ticker/symbol
   * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public void initQuote(String ticker) {
    initQuotes(Collections.singletonList(ticker));
  }


  /**
   * Update quote table against IEX source
   *  - get all quotes from the db
   *  - foreach ticker get iexQuote
   *  - convert iexQuote to quote entity
   *  - persist quote to db
   *
   * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public void updateMarketData() {
    List<Quote> quotes = quoteDao.findAll();
    List<String> tickers = quotes.stream().map(Quote::getTicker).collect(Collectors.toList());
    List<IexQuote> iexQuotes = marketDataDao.findIexQuoteByTicker(tickers);
    List<Quote> updateQuotes = iexQuotes.stream().map(QuoteService::buildQuoteFromIexQuote)
        .collect(Collectors.toList());
    quoteDao.update(updateQuotes);
  }

}
