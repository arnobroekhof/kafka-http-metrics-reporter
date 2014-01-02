package nl.techop.kafka;


import com.yammer.metrics.reporting.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.InetSocketAddress;

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
  private int port;
  private String bindAddress;

  public KafkaHttpMetricsServer(String bindAddress, int port) {

    this.port = port;
    this.bindAddress = bindAddress;
    init();

  }

  private void init() {
    LOG.info("Initializing Kafka Http Metrics Reporter");
    InetSocketAddress inetSocketAddress = new InetSocketAddress(bindAddress,port);
    server = new Server(inetSocketAddress);

    ServletContextHandler servletContextHandler = new ServletContextHandler();

    servletContextHandler.setContextPath("/");
    servletContextHandler.addServlet(new ServletHolder(new AdminServlet()), "/api");
    servletContextHandler.addServlet(new ServletHolder(new MetricsServlet()), "/api/metrics");
    servletContextHandler.addServlet(new ServletHolder(new ThreadDumpServlet()), "/api/threads");
    servletContextHandler.addServlet(new ServletHolder(new HealthCheckServlet()), "/api/healthcheck");
    servletContextHandler.addServlet(new ServletHolder(new PingServlet()), "/api/ping");

    server.setHandler(servletContextHandler);
    LOG.info("Finished initializing Kafka Http Metrics Reporter");
  }

  public void start() {
    try {
      LOG.info("Starting Kafka Http Metrics Reporter");
      server.start();
      LOG.info("Started Kafka Http Metrics Reporter on: " + bindAddress +":"+ port);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    try {
      LOG.info("Stopping Kafka Http Metrics Reporter");
      server.stop();
      LOG.info("Kafka Http Metrics Reporter stopped");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}

