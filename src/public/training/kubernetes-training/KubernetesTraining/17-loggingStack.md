# Intro to Logging - Deploy a local Logging Stack with Helm

## Introduction

In the last lesson, we learnt how to centralize our application's metrics using a metric stack.  
In this lesson, we will deploy a local logging stack using Helm that enables us to centralize all of our application
logs into Grafana.

In this lesson, we will deploy the following components:

- **Promtail** - a log shipper that reads logs from the local filesystem and sends them to Loki
- **Loki** - a log aggregation system that indexes logs and provides a query language for searching logs

By deploying these components, we will be able to centralize all of our application logs into Grafana.  
But how do we ship these components?

[You must be joking right?](https://youtu.be/mqFLXayD6e8?t=147)

## Installing a Logging Stack

With a little browsing around the internet, we can
find [this bad boy](https://artifacthub.io/packages/helm/grafana/loki-stack).  
This helm chart deploys Loki and promtail into your Kubernetes cluster.

To install the logging stack, run the following commands:

```shell
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update
```

Now that we have added the Grafana helm repository, we can install the logging stack.  
But before we install the stack, let's look at
the [values.yaml file for v2.10.2](https://github.com/grafana/helm-charts/blob/loki-stack-2.10.2/charts/loki-stack/values.yaml)
of the helm chart.

Looking at the values, there is one thing we don't want. The value `test_pod.enabled` is true.  
Let's just disable this as we install the chart.

Before, we have used `--values` to pass in a separate values.yaml file, but we can also pass in values directly using
`--set`.

```shell
kubectl create namespace logging
helm install loki-stack grafana/loki-stack --version 2.10.2 --set test_pod.enabled=false -n logging
```

And now, let's see the results:

```shell
$ kubectl get pods -n logging
NAME                        READY   STATUS    RESTARTS   AGE
loki-stack-0                0/1     Running   0          45s
loki-stack-promtail-dzx97   1/1     Running   0          45s
loki-stack-promtail-j8zxk   1/1     Running   0          45s
loki-stack-promtail-l4htt   1/1     Running   0          45s
loki-stack-promtail-s897q   1/1     Running   0          45s
```

## Daemon Sets

You may have noticed that there are 4 instances of promtail running! Why would we need 4 instances of promtail?!

Promtail is a log shipper and reads logs from the local filesystem and sends them to Loki (the log database).  
In our local deployment, we have 4 machines - the control plane, and 3 worker nodes.  
In production, each of these would be separate machines, and therefore, we would need a service deployed on each machine
to be able to read the logs from each host.

In order to get this behavior, we use a `DaemonSet` in Kubernetes.  
DaemonSets ensure that all Nodes run a copy of a Pod. This is useful for things like log aggregators, or monitoring
agents.

## Viewing Logs

Now that we have the logging stack deployed, we can view the logs in Grafana.  
To do this, we need to port-forward the Grafana service to our local machine.

```shell
kubectl port-forward svc/loki-stack-grafana 3000:80 -n logging
```

Now, open your browser and navigate to `http://localhost:3000`.  
You will be prompted to login. The default username is `admin` and the default password is `prom-operator`.

In order to access the logs, we need to add a datasource to Grafana.  
To do this, click on the gear icon on the left-hand side of the screen, and then click on **Data Sources**.

Click on **Add data source**, and then select **Loki** from the list of data sources.  
Next, we need to find Loki's URL.

Kubernetes service URLs are in the form: `<serviceName>.<namespace>.svc.cluster.local`.  
So, to find the service name, let's run the following command:

```shell
$ kubectl get svc -n logging
NAME                    TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
loki-stack              ClusterIP   10.96.181.243   <none>        3100/TCP   7m52s
loki-stack-headless     ClusterIP   None            <none>        3100/TCP   7m52s
loki-stack-memberlist   ClusterIP   None            <none>        7946/TCP   7m52s
```

Therefore, the URL for Loki is: `http://loki-stack.logging.svc.cluster.local:3100`.  
Let's put this into Grafana, and click **Save and Test**.  
You should see a successful connection.

## Querying with Loki

Now that we have the datasource set up, we can start querying logs.  
To do this, click on the **Explore** tab on the left-hand side of the screen.

In the dropdown in the top left of the screen, select "Loki" as the data source to use for our queries.

Now, we can start querying logs.  
While getting started you can use the label browser, or you can start writing queries directly.  
For example, to get all logs from the `kube-system` namespace, you can run the following query:

```shell
{namespace="kube-system"}
```

Or, you could get all logs for a particular pod by running the following query:

```shell
{container="loki"}
```

There is a lot of powerful functionality in Loki, and you can find more information in
the [official documentation](https://grafana.com/docs/loki/latest/).

## Navigation

Now that we have both metrics and logs in one place, we can make some powerful queries and visualizations.

In the final lesson we will look at how to maintain our ever-growing number of helm charts, and how to easily deploy
them using an app called ArgoCD.

[Home](../README.md)

Next: [ArgoCD - Manage your Helm Charts](./18-argocd.md)
