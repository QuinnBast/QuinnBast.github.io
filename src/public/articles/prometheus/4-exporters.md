---
title: "Enhancing Observability using Prometheus Exporters"
date: 2025-09-06 19:00
description: "Get more visibility into your systems by understanding how to setup Prometheus Exporters for third party applications."
path: "prometheus"
tags:
- Prometheus
- Observability
---
# Prometheus Exporters

Because Prometheus is so popular, many third party technologies and tools will already be instrumented with Prometheus; we simply need to tell the Prometheus server to go and collect the metrics.
Many third party tools have created what are called "Exporters" for their app, which export metrics to Prometheus.

A list of the most common prometheus exporters for third party tools [can be found here](https://prometheus.io/docs/instrumenting/exporters/) (though this list is not comprehensive).

Some of the common ones you might know are:

Virtually Any Database
- ElasticSearch
- MongoDB
- MySQL
- OpenTSDB
- InfluxDB
- PostgreSQL
- Redis

Various Hardware monitoring tools
- IPMI Exporter
- Netgear exporters
- Node/System Exporters
- NVIDIA GPU Exporter
- Windows Exporter

As well as many other third party tools like:
- Jira
- Confluence
- Kafka
- HAProxy
- Apache Server
- Minecraft
- OpenStack
- Kubernetes
- Etcd

If you are using any of these technologies, consider adding the prometheus exporters for these tools and configure Prometheus server to report the metrics for them.

Showing off these tools is a bit hard to do in a local environment just using docker, so unfortunately we won't be able to see these tools in action.
However, know that they exist and can provide a lot of value for your system.

For example, being able to monitor kafka and see the topic lags, message throughput on each topic, and more has been extremely useful.

In the next and final section, we will learn [how to create alerts from prometheus metrics](./5-alerts.md).
