package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.service.QuoteService;
import java.util.Arrays;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * For learning purpose, manually configure DataSource and JdbcTemplate
 */
@SpringBootApplication(exclude = {JdbcTemplateAutoConfiguration.class,
    DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class Application implements CommandLineRunner {

  private Logger logger = LoggerFactory.getLogger(Application.class);

  @Autowired
  private DataSource dataSource;

  @Value("${app.init.dailyList}")
  private String[] initDailyList;

  @Autowired
  private QuoteService quoteService;

  public static void main(String[] args) throws Exception {

    SpringApplication app = new SpringApplication(Application.class);

    //Turn off web
    //app.setWebApplicationType(WebApplicationType.NONE);
    app.run(args);
  }

  @Override
  public void run(String... args) throws Exception {
    //ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("schema.sql"));

    quoteService.initQuotes(Arrays.asList(initDailyList));
    quoteService.updateMarketData();
  }

}
