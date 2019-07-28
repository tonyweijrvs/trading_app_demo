package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.util.StringUtil;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class AppConfig {

  private Logger logger = LoggerFactory.getLogger(AppConfig.class);

  @Value("${iex.host}")
  private String iex_host;

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public MarketDataConfig marketDataConfig() {
    if (StringUtil.isEmpty(System.getenv("IEX_PUB_TOKEN")) || StringUtil.isEmpty(iex_host)) {
      throw new IllegalArgumentException("ENV:IEX_PUB_TOKEN or property:iex_host is not set");
    }
    MarketDataConfig marketDataConfig = new MarketDataConfig();
    marketDataConfig.setToken(System.getenv("IEX_PUB_TOKEN"));
    marketDataConfig.setHost(iex_host);
    return marketDataConfig;
  }

  @Bean
  public DataSource dataSource() {

    String jdbcUrl;
    String user;
    String password;

    if (!StringUtil.isEmpty(System.getenv("RDS_HOSTNAME"))) {
      // DO NOT log env var in real development. Very serious security issue.
      logger.info("RDS_HOSTNAME:" + System.getenv("RDS_HOSTNAME"));
      logger.info("RDS_USERNAME:" + System.getenv("RDS_USERNAME"));
      logger.info("RDS_PASSWORD:" + System.getenv("RDS_PASSWORD"));
      jdbcUrl = "jdbc:postgresql://" + System.getenv("RDS_HOSTNAME") + ":" + System.getenv("RDS_PORT") + "/jrvstrading";
      user = System.getenv("RDS_USERNAME");
      password = System.getenv("RDS_PASSWORD");
    } else {
      jdbcUrl = System.getenv("PSQL_URL");
      user = System.getenv("PSQL_USER");
      password = System.getenv("PSQL_PASSWORD");
    }

    logger.error("JDBC:" + jdbcUrl);

    if (StringUtil.isEmpty(jdbcUrl, user, password)) {
      throw new IllegalArgumentException("Missing data source config env vars");
    }

    BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setDriverClassName("org.postgresql.Driver");
    basicDataSource.setUrl(jdbcUrl);
    basicDataSource.setUsername(user);
    basicDataSource.setPassword(password);
    return basicDataSource;
  }

  // http://bit.ly/2tWTmzQ connectionPool
  @Bean
  public HttpClientConnectionManager httpClientConnectionManager() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(50);
    cm.setDefaultMaxPerRoute(50);
    return cm;
  }
}
