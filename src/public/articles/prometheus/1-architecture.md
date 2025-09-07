---
title: "Understanding Prometheus"
date: 2025-09-06 19:00
description: "Learn the basics of the Prometheus Architecture and how Prometheus collects application metrics."
path: "prometheus"
tags:
- Prometheus
- Observability
---
# Prometheus Architecture

To understand how Prometheus works, it is important to understand the architecture of Prometheus.
Prometheus works by using a "pull based" model.

![Prometheus Architecture](./images/prometheus/PrometheusArchitecture.png)

[A full architecture diagram can be found here](https://prometheus.io/docs/introduction/overview/#architecture), but the diagram above simplifies and makes the software a bit easier to understand for someone jumping in.

## Understanding Scraping

In order for Prometheus to "scrape" or poll your application, Prometheus lets you configure a list of all of the targets you want to consume metrics from.
Once configured, Prometheus will regularly poll each target on a configurable interval (default every 1 minute) to grab metrics from the source.

Prometheus sends an HTTP request to an exposed REST endpoint on configured targets. Prometheus expects the data that it collects from each source to be in a standardized format (which the client does for you).
By instrumenting your code with a Prometheus client library, or using a system that has Prometheus monitoring built-in, Prometheus will be able to read the data that is available by your software.

Upon scraping a target, prometheus add some "labels" by default (labels are indexed fields).
The `job`, which is the name of the configuration job telling prometheus to scrape the particular target, and the `instance` which is the `host:port` that was scraped.

For every scrape target, prometheus also exposes metrics about the scrape process itself. Some are more advanced but two interesting ones are:

- `up` - is a metric that monitors scrape targets. `1` if the target was successfully scraped, `0` if not. This is useful to see if a server is, well, "up" or not.
- `scrape_duration_seconds` - How long the scrape took to complete

Prometheus provides a web GUI out of the box which supports making API queries and provides a visual representation of the time series data from the web.

## Starting a Local Prometheus

To start playing around, let's start up a local instance of a Prometheus server.
We are going to do this using docker, but there are a few other ways this can be done.

To start prometheus locally, run the following command:

```shell
docker run \
    --network host \
    -v ${PWD}/config:/etc/prometheus \
    prom/prometheus
```

The `-v` is a volume mount that mounts the following file:

```yaml
global:
  # How frequently to scrape targets by default.
  scrape_interval: 1m

  # How long until a scrape request times out.
  scrape_timeout: 10s

  # How frequently to evaluate rules.
  evaluation_interval: 1m

scrape_configs:
  - job_name: sampleApp
    metrics_path: /
    static_configs:
      - targets:
          - localhost:1234

### Used to configure a connection to the prometheus alertmanager instance.
#alerting:
#  alertmanagers:
#    - scheme: http
#      static_configs:
#        - targets: [ 'localhost:9093' ]

rule_files:
  - alerting_rules.yml
```

Running this command will start up a local prometheus server on port 9090.

Once the container has started, open up a web browser to port 9090 and you should see the Prometheus GUI:

![Prometheus GUI](./images/prometheus/PrometheusGui.png)

The main landing page to prometheus allows you to perform queries on prometheus metrics.
By default, Prometheus will have no metrics available to query.
However, there are some other interesting pages on this GUI.

The most interesting ones are under the `Status` menu:

- `Configuration` - shows you the current YAML configuration.
- `Targets` - shows you all current targets that Prometheus is scraping as well as if they are online, the time they were last polled, etc.

One thing to note, is that some configuration settings have been already configured.
This file configures a `scrape_configs` section which tells prometheus to attempt to scrape `localhost:1234`.

![Failing Target](./images/prometheus/PrometheusFailingTarget.png)

Currently we have no metrics being reported at this location so the target appears as being DOWN on the `Targets` page.
However, this will soon change.

[In the next lesson](./2-client-api.md) we will make our software export prometheus metrics so that we can configure it as a target for prometheus to scrape and view our metrics in Prometheus.
