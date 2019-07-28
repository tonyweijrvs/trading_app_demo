package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class QuoteDao extends JdbcCrudDao<Quote, String> {

  private final static String TABLE_NAME = "quote";
  private final static String ID_NAME = "ticker";
  private JdbcTemplate jdbcTemplate;

  private SimpleJdbcInsert simpleJdbcInsert;

  @Autowired
  public QuoteDao(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
    simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME);
  }

  @Override
  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  @Override
  public SimpleJdbcInsert getSimpleJdbcInsert() {
    return simpleJdbcInsert;
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public String getIdName() {
    return ID_NAME;
  }

  @Override
  Class getEntityClass() {
    return Quote.class;
  }


  @Override
  public Quote save(Quote quote) {
    SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(quote);
    int row = getSimpleJdbcInsert().execute(parameterSource);
    if (row != 1) {
      throw new IncorrectResultSizeDataAccessException("Failed to insert", 1, row);
    }
    return quote;
  }

  public List<Quote> findAll() {
    String selectSql = "SELECT * FROM " + TABLE_NAME;
    return jdbcTemplate
        .query(selectSql, BeanPropertyRowMapper.newInstance(Quote.class));
  }

  /**
   * https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#jdbc-batch-list
   */
  public void update(List<Quote> quotes) {
    String updateSql = "UPDATE quote SET last_price=?, bid_price=?, bid_size=?, ask_price=?, ask_size=? WHERE ticker=?";

    //Prepare batch update values (order must match updateSql question mark)
    List<Object[]> batch = new ArrayList<>();
    quotes.forEach(quote -> {
      if (!existsById(quote.getTicker())) {
        throw new ResourceNotFoundException("Ticker not found:" + quote.getTicker());
      }
      Object[] values = new Object[]{quote.getLastPrice(), quote.getBidPrice(), quote.getBidSize(),
          quote.getAskPrice(), quote.getAskSize(), quote.getTicker()};
      batch.add(values);
    });

    int[] rows = jdbcTemplate.batchUpdate(updateSql, batch);
    int totalRow = Arrays.stream(rows).sum();
    if (totalRow != quotes.size()) {
      throw new IncorrectResultSizeDataAccessException("Number of rows ", quotes.size(), totalRow);
    }
  }
}
