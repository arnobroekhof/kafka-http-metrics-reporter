package nl.techop.kafka;

import com.yammer.metrics.core.MetricPredicate;
import kafka.metrics.KafkaMetricsConfig;
import kafka.metrics.KafkaMetricsReporter;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;

/**
 * KafkaHttpMetricsReporter
 * nl.techop.kafka
 * User: arnobroekhof
 * Date: 31-12-13
 * Time: 14:18
 */
public class KafkaHttpMetricsReporter implements KafkaMetricsReporter, KafkaHttpMetricsReporterMBean {

  private static Logger LOG = Logger.getLogger(KafkaHttpMetricsReporter.class);
  private boolean initialized = false;
  private boolean running = false;
  private boolean enabled = false;

  private KafkaHttpMetricsServer metricsServer = null;

  private static final int DEFAULT_PORT = 8080;
  private static final String DEFAULT_BIND_ADDRESS = "localhost";

  private String bindAddress = DEFAULT_BIND_ADDRESS;
  private int port = DEFAULT_PORT;


  @Override
  public void init(VerifiableProperties verifiableProperties) {
    if (! initialized) {
      KafkaMetricsConfig metricsConfig = new KafkaMetricsConfig(verifiableProperties);

      bindAddress = verifiableProperties.getProperty("kafka.http.metrics.host");
      port = Integer.parseInt(verifiableProperties.getProperty("kafka.http.metrics.port"));
      enabled = Boolean.parseBoolean(verifiableProperties.getProperty("kafka.http.metrics.reporter.enabled"));

      metricsServer = new KafkaHttpMetricsServer(bindAddress,port);
      initialized = true;

      startReporter(metricsConfig.pollingIntervalSecs());
    } else {
      LOG.error("Kafka Http Metrics Reporter already initialized");
    }
  }

  @Override
  public synchronized void startReporter(long pollingPeriodSecs) {
    if (initialized && !running && enabled) {
      metricsServer.start();
      running = true;
    } else {
      if ( ! enabled ) {
        LOG.info("Kafka Http Metrics Reporter disabled");
      } else if ( running ) {
        LOG.error("Kafka Http Metrics Reporter already running");
      }
    }
  }

  @Override
  public synchronized void stopReporter() {
    if ( initialized && running) {
      metricsServer.stop();
    }
  }

  @Override
  public String getMBeanName() {
    return "kafka:type=nl.techop.kafka.KafkaHttpMetricsReporter";
  }
}
