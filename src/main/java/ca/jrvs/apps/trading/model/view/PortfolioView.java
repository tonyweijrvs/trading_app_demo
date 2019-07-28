package ca.jrvs.apps.trading.model.view;

import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.List;

public class PortfolioView {

  private List<SecurityRow> SecurityRows;

  public List<SecurityRow> getSecurityRows() {
    return SecurityRows;
  }

  public void setSecurityRows(
      List<SecurityRow> securityRows) {
    SecurityRows = securityRows;
  }

  public static class SecurityRow {

    private String ticker;
    private Position position;
    private Quote quote;

    public String getTicker() {
      return ticker;
    }

    public void setTicker(String ticker) {
      this.ticker = ticker;
    }

    public Position getPosition() {
      return position;
    }

    public void setPosition(Position position) {
      this.position = position;
    }

    public Quote getQuote() {
      return quote;
    }

    public void setQuote(Quote quote) {
      this.quote = quote;
    }
  }

}
