# Prometheus Training

Collecting metrics and viewing your server's performance over time is an essential tool for any running software.
Without collecting metrics, your server can run haywire or perform in a degraded state without you even noticing.
Prometheus makes it easy for you to gather information about your running apps and view the state of your system over time.

This guide is meant to be a hands-on and interactive training session on all things Prometheus where you can follow along and work with the tool to better understand it.

This guide is broken up into multiple sections, however, it is recommended to go through the guide in order.

## Table of Contents

1. [Introduction and Preparation](https://gitlab.com/calianat/software/training/prometheus-training)
2. [Understanding and Starting Prometheus](./lessons/1-architecture.md)
3. [Exposing Metrics with the Client Api](./lessons/2-client-api.md)
4. [PromQL](./lessons/3-promql.md)
5. [Additional Exporters](./lessons/4-exporters.md)
6. [AlertManager](./lessons/5-alerts.md)

# What is Prometheus?

Prometheus is an extremely useful tool to collect, gather, and report on metrics and alarms from software systems in an industry standard way. Prometheus is the most used metrics collection tool in the open source community and using Prometheus is so easy that you are going to start using it in every project.

Prometheus is an all-in-one tool that lets you collect and store metrics from running applications. Prometheus stores metrics in a time-series format, giving you a historic view of your data over time. Metrics are collected using a pull/scrape model and you must configure prometheus to know what applications it should poll. Prometheus also has a custom query language, PromQL, which is exteremly easy and intuitive to use unlike other custom querying languages.

Additional components can be installed for other use cases, for example, the Push Gateway, which allows pushing metrics into Prometheus (though this is not recommended), as well as the AlertManager, which allows Prometheus to generate system level alerts based on configurable thersholds for the system. Prometheus also easily plugs into grafana as a data source allowing you to easily make monitoring and reporting dashboards in Grafana.

# Prerequisites

In order to follow along in this training guide, we will be using docker to start up a local instance of Prometheus on your machine to play around and interact with. If you do not have docker installed or access to a machine with docker, install docker first.

Follow the steps below depending on your OS:

- [Windows](https://docs.docker.com/desktop/install/windows-install/)
- [MacOS](https://docs.docker.com/desktop/install/mac-install/)
- [Linux](https://docs.docker.com/engine/install/#server)
  - Select your distribution from the "Server" section

Once you have a machine with Docker installed, [you are ready to begin!](./lessons/1-architecture.md)
