Kafka Http Metrics Reporter
==============================

This is a http metrics reporter for kafka using
Jetty with the metrics servlets (http://metrics.codahale.com/manual/servlets/kafka ) for exposing the metrics as JSON Objects.
Instead of retrieving metrics through JMX with JConsole it is now possible to retrieve the metrics with curl or some other http / rest client.
Code is tested with Kafka 0.8.0

[![Travis Build Status](https://secure.travis-ci.org/arnobroekhof/kafka-http-metrics-reporter.png)](http://travis-ci.org/arnobroekhof/kafka-http-metrics-reporter)

Install On Broker
------------

1. Build the `kafka-http-metrics-reporter-1.0.0-uber.jar` jar using `mvn package`.
2. Copy the jar kafka-http-metrics-reporter-1.0.0-uber.jar to the libs/
   directory of your kafka broker installation
3. Configure the broker (see the configuration section below)
4. Restart the broker

Configuration
------------

Edit the `server.properties` file of your installation, activate the reporter by setting:

```
    kafka.metrics.reporters=nl.techop.kafka.KafkaHttpMetricsReporter[,kafka.metrics.KafkaCSVMetricsReporter[,....]]
    kafka.http.metrics.reporter.enabled=true
    kafka.http.metrics.host=localhost
    kafka.http.metrics.port=8080
```

URL List
------------

| *url* | *description* |
|:-----:|:-------------:|
| /api  | HTML admin menu with links |
| /api/healthcheck | HealthCheckServlet responds to GET requests by running all the [health checks](#health-checks) and returning 501 Not Implemented if no health checks are registered, 200 OK if all pass, or 500 Internal Service Error if one or more fail. The results are returned as a human-readable text/plain entity. |
| /api/metrics | exposes the state of the metrics in a particular registry as a JSON object. |
| /api/ping | responds to GET requests with a text/plain/200 OK response of pong. This is useful for determining liveness for load balancers, etc. |
| api/threads | responds to GET requests with a text/plain representation of all the live threads in the JVM, their states, their stack traces, and the state of any locks they may be waiting for. |

Usage Examples
------------

### Curl

```bash
   curl -XGET -H "Content-type: application/json" -H "Accept: application/json" "http://localhost:8080/api/metrics"

```
