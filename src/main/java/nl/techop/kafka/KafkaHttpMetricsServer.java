package nl.techop.kafka;


import com.yammer.metrics.reporting.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * KafkaHttpMetricsServer
 * nl.techop.kafka
 * User: arnobroekhof
 * Date: 31-12-13
 * Time: 15:31
 */
public class KafkaHttpMetricsServer {

  private static final Logger LOG = Logger.getLogger(KafkaHttpMetricsServer.class);
  private Server server;

  public KafkaHttpMetricsServer(long port) {

    LOG.info("Initializing Kafka Http Metrics Reporter on port: " + port);
    server = new Server((int)port);

    ServletContextHandler servletContextHandler = new ServletContextHandler();

    servletContextHandler.setContextPath("/");
    servletContextHandler.addServlet(new ServletHolder(new AdminServlet()), "/api");
    servletContextHandler.addServlet(new ServletHolder(new MetricsServlet()), "/api/metrics");
    servletContextHandler.addServlet(new ServletHolder(new ThreadDumpServlet()), "/api/threads");
    servletContextHandler.addServlet(new ServletHolder(new HealthCheckServlet()), "/api/healthcheck");
    servletContextHandler.addServlet(new ServletHolder(new PingServlet()), "/api/ping");

    server.setHandler(servletContextHandler);

  }

  public void start() {
    try {
      LOG.info("Starting Kafka Http Metrics Reporter");
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    try {
      LOG.info("Stopping Kafka Http Metrics Reporter");
      server.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}

