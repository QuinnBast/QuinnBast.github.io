# Intro to Observability - Deploy a local Prometheus and Grafana stack with Helm

## Introduction

In the previous section, we learned how to set resource limits and requests for our applications.  
But wouldn't it be nice if there was an easy way to see our app's CPU, and memory usage over time to know what to set
the limits to?

We mentioned that this was possible, but that you needed to have
the [metrics server](https://github.com/kubernetes-sigs/metrics-server) deployed in your cluster.  
How do we do that?

Helm of course!

In this section, we will deploy the metrics server, as well as a local Prometheus and Grafana stack with Helm.

## Deploying an observability stack

While we could install the metrics server directly, and then Prometheus and Grafana, there is an easier way.  
[Kube-prometheus-stack](https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack) is a
Helm chart that deploys the metrics server, Prometheus, and Grafana in one go.

To get started, let's add the helm repository for the kube-prometheus-stack:

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
```

Now, let's check out the chart'
s [values.yaml for version 45.21.0](https://github.com/prometheus-community/helm-charts/blob/kube-prometheus-stack-45.21.0/charts/kube-prometheus-stack/values.yaml)
and see if there is anything we need to change.  
On initial investigation, the chart should include everything we need to deploy a local stack without needing any
changes.

So, we can install the chart. Let's make a namespace for it first, and then deploy it to the namespace:

```bash
kubectl create namespace monitoring
helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack -n monitoring --version 45.21.0
```

And wait for the magic to happen.

```bash
kubectl get pods -n monitoring
```

You should see a bunch of pods starting, including:

- Prometheus Alertmanager
- Prometheus Kube State Metrics
- Prometheus Node Exporter
- Prometheus Server
- Grafana

## Accessing Grafana and Prometheus

Since we didn't customize any of the `values.yaml`, we didn't add a Service to expose these apps.  
To make this lesson shorter, we will just port-forward the apps to our local machine for now.  
In a production scenario though, you would want to expose these apps with a LoadBalancer or Ingress to be able to access
them consistently.

Let's port-forward Grafana to our local machine:

```bash
kubectl port-forward -n monitoring svc/grafana-kube-prometheus-stack 3000:3000
```

Now, we can access Grafana in our browser at `http://localhost:3000`.  
The default username and password are `admin` and `prom-operator`, respectively (somehow ChatGPT knew this).

We can now browse around Grafana and see that a bunch of dashboards have been pre-installed for us.  
For example, we can see the CPU and Memory usage of our pods, namespaces, and nodes, as well as the network traffic.

These metrics are essential to monitoring our cluster.  
In addition to just dashboards, the stack comes configured with some pre-built alerts that can be configured to send
notifications to Slack, PagerDuty, etc.  
These alerting rules can let us know when the cluster is under heavy load, or when a pod is using too much CPU or
memory, or a disk is being overused.

This stack is essential for any production cluster, and is a great way to get started with monitoring your cluster.  
For more in-depth monitoring, you can also instrument custom prometheus metrics into your applications, and create
custom dashboards in Grafana.  
In order to do this, take a look at
my [Prometheus Training](https://gitlab.com/calianat/software/training/prometheus-trainingp) repository.

## Navigation

Now that we can see our metrics, what about logs?  
In order to see our logs, we need to also deploy a logging stack.

[<< Home](../README.md)

Next: [Intro to Logging](./17-loggingStack.md)