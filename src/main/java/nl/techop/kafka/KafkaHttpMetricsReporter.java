package nl.techop.kafka;

import com.yammer.metrics.core.MetricPredicate;
import kafka.metrics.KafkaMetricsReporter;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;

/**
 * KafkaMonitoringRestApi
 * nl.techop.kafka.monitoring
 * User: arnobroekhof
 * Date: 31-12-13
 * Time: 14:18
 */
public class KafkaHttpMetricsReporter implements KafkaMetricsReporter, KafkaHttpMetricsReporterMBean {

  static Logger LOG = Logger.getLogger(KafkaHttpMetricsReporter.class);
  boolean initialized = false;
  boolean running = false;
  private KafkaHttpMetricsServer uiServer = null;
  private long defaultPort = 8080;


  MetricPredicate predicate = MetricPredicate.ALL;

  @Override
  public void init(VerifiableProperties verifiableProperties) {
    if (! initialized) {
      uiServer = new KafkaHttpMetricsServer(defaultPort);
      LOG.info("Initializing Kafka Rest API");


      initialized = true;
      startReporter(8080);
    } else {
      LOG.error("Kafka RestAPI already initialized");
    }
  }

  @Override
  public synchronized void startReporter(long port) {
    if (initialized && !running) {
      LOG.info("Starting Kafka Rest API on port: " + Long.toString(port));
      uiServer.start();
      running = true;
    } else {
      LOG.error("Kafka Rest API already running");
    }
  }

  @Override
  public synchronized void stopReporter() {
    if ( initialized && running) {
      LOG.info("Stopping Kafka REST API");
      uiServer.stop();
    } else {
      LOG.info("Unable to stop Kafka REST API");
    }
  }

  @Override
  public String getMBeanName() {
    return "kafka:type=nl.techop.kafka.KafkaHttpMetricsReporter";
  }
}
