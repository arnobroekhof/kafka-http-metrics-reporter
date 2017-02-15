/*
 * *
 *  * Copyright 2014, arnobroekhof@gmail.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package nl.techop.kafka;

import kafka.metrics.KafkaMetricsConfig;
import kafka.metrics.KafkaMetricsReporter;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;

/**
 * Class KafkaHttpMetricsReporter
 * &lt;p/&gt;
 * Author: arnobroekhof
 * &lt;p/&gt;
 * Purpose: Main class that is being called by Kafka on startup. This Class is also repsonsible for looking up the
 * metric settings as configured in the kafka server.properties file en based on those settings it starts the
 * embedded Jetty Server with the CodaStale servlets attached to it.
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


  /*
   * {@inheritDoc}
   */
  @Override
  public void init(VerifiableProperties verifiableProperties) {

    if (!initialized) {
      // get configured metrics from kafka
      KafkaMetricsConfig metricsConfig = new KafkaMetricsConfig(verifiableProperties);

      // get the configured properties from kafka to set the bindAddress and port.
      bindAddress = verifiableProperties.getProperty("kafka.http.metrics.host");
      port = Integer.parseInt(verifiableProperties.getProperty("kafka.http.metrics.port"));
      enabled = Boolean.parseBoolean(verifiableProperties.getProperty("kafka.http.metrics.reporter.enabled"));

      // construct the Metrics Server
      metricsServer = new KafkaHttpMetricsServer(bindAddress, port);
      initialized = true;

      // call the method startReporter
      startReporter(metricsConfig.pollingIntervalSecs());
    } else {
      LOG.error("Kafka Http Metrics Reporter already initialized");
    }
  }

  /*
   * {@inheritDoc}
   */
  @Override
  public synchronized void startReporter(long pollingPeriodSecs) {
    if (initialized && !running && enabled) {
      // start the metrics server
      metricsServer.start();
      running = true;
    } else {
      if (!enabled) {
        LOG.info("Kafka Http Metrics Reporter disabled");
      } else if (running) {
        LOG.error("Kafka Http Metrics Reporter already running");
      }
    }
  }

  /*
   * {@inheritDoc}
   */
  @Override
  public synchronized void stopReporter() {
    if (initialized && running) {
      // stop the metrics server
      metricsServer.stop();
    }
  }

  /*
   * {@inheritDoc}
   */
  @Override
  public String getMBeanName() {
    return "kafka:type=nl.techop.kafka.KafkaHttpMetricsReporter";
  }
}
