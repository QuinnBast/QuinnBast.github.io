# Helm

In the previous section, we were able to apply a config file to our application and we've realized that our app needs a
database!  
We could try to manually piece together a bunch of Kubernetes manifests to deploy the database we need (Redis)...
but...  
Redis is such a common database. Surely other people are deploying Redis to Kubernetes! Someone has got to have done
this before...

[Helm](https://helm.sh/) is the Kubernetes Package Manager — You can bet that if you're trying to install a 3rd party
tool, there is likely a Helm chart that exists for it.

In order to start using Helm, you will need to [install it](https://helm.sh/docs/intro/quickstart/), however, I
explained this on the [pre-requisites page](../README.md) so you should already have it installed. You can
check with:

```shell
helm version
```

## Helm Basics

To start using Helm, we first need to find a Helm chart. You can do this by googling for the tool you want to install!  
For example: "Redis Helm chart".

Most (but not all) Helm charts will require you to add their Helm repository so you can pull their charts.  
Luckily, Redis is not one of them, however, this will usually look something like this:

```shell
helm repo add <repoName> <repoUrl>
```

Once you have added the repository, the next step is to install your chart! This is generally done with:

```shell
helm install <releaseName> <UrlToChart>
```

However, 99% of the time, we will want to customize the installation.  
The default installations will make some assumptions about how you want to install things and may provide some default
values that won't work for you.  
In order to customize a Helm chart, we need to find something called the chart's `values.yaml` file.

Let's take a practical look at this and try to install the Redis Helm chart...

### Helm: In practice

A Google search for "Redis Helm Chart" will show us a few results, but we will use
the [Bitnami Redis Helm chart](https://github.com/bitnami/charts/tree/main/bitnami/redis) for this example.  
Take a look at this page.

First, we can see a "TL;DR" which shows us the `helm install` command.  
This is great! But as mentioned above, we want to customize the install so hold off on running this command for now.

As we scroll through the readme a bit more, we notice a few other things:

- There is no `helm repo add` command, which means we can just install this chart right away.
- The readme outlines all of the possible values we can set, has a description of each one, and its default value.

This will generally be the case for all Helm charts.  
However, what we are really looking for here, is the file named `values.yaml`.  
This should be a file in the GitHub repository for the Helm chart.

[Click on this file](https://github.com/bitnami/charts/blob/main/bitnami/redis/values.yaml).

The `values.yaml` file is a list of all of the potential configuration options that we can set (or change) in order to
deploy Redis to our cluster in the way that we want.  
In order to tell `helm` that we want to change some of these values, we need to copy this file, and pass a new version
of the file to the `helm install` command to override the defaults.

To do this, let's create a new file `redisValuesFull.yaml`, and copy the Helm chart's default values from the
`values.yaml` file into this new file.  
We should now have a `redisValuesFull.yaml` file which is identical to the `values.yaml` on GitHub.

We will now go through and change a few options to better fit our deployment.  
Specifically, we will change:

- `architecture` to be `standalone` instead of `replication`. This will deploy a single `Redis` instead of a cluster of
  3.
- `auth.enabled` to be `false`; as I didn't set up my Python app to use a user or password for accessing Redis.
- `master.persistence.enabled` to be `false`, as we don't want to persist any data right now.
- `replica.persistence.enabled` to be `false`, as we don't want to persist any data right now.
- `replica.replicaCount` to be `1`, as we only want 1 instance deployed.

Once you start using Kubernetes and Helm more, you will know what things to look for, but as you get started, just
scrolling through the `values.yaml` file and finding interesting settings is the best way to go.  
Once we have updated these settings, we should have a file that [looks like this](../charts/redis/redisValuesFull.yaml).

We can now run the `helm install` command
as [indicated in the readme](https://github.com/bitnami/charts/blob/main/bitnami/redis/README.md) of the Helm chart,
however, with one twist.  
We will also pass a `-f` file to point to an override file, and `-n` to indicate a namespace. Our command will look like
this:

```shell
kubectl create namespace redis || true
helm install redis oci://registry-1.docker.io/bitnamicharts/redis -f ../charts/redis/redisValuesFull.yaml -n redis
```

Once we have applied this, we will see some output from the `helm install` command:

```shell
$ helm install redis oci://registry-1.docker.io/bitnamicharts/redis -f redisValues.yaml
Pulled: registry-1.docker.io/bitnamicharts/redis:17.11.6
Digest: sha256:7885c16265e8b0ed5a02ecffa0cb7ef35b4ec6e1174cce8b26c010af21b938b9
NAME: redis
LAST DEPLOYED: Fri Jun 23 17:41:18 2023
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
CHART NAME: redis
CHART VERSION: 17.11.6
APP VERSION: 7.0.11
```

Specifically, `STATUS: deployed` tells us that our Helm chart has successfully been applied!  
We can now take a look at our cluster:

```shell
kubectl get pods -A
```

Which will output:

```shell
NAMESPACE            NAME                                         READY   STATUS    RESTARTS   AGE
calian               python-rest-api-56ddff9774-5fbjr             1/1     Running   0          137m
redis                redis-master-0                               1/1     Running   0          147m
```

Success! We deployed redis using Helm!
If you want to deploy any 3rd party technology or tool to a Kubernetes cluster, check to see if a helm chart exists for the tool as this is the fastest way to get up and running.

### Pro Tip!

When using `helm` commands, the `-f` flag is an "override" of the yaml values. Helm knows what the default values are, and therefore, helm only technically needs to know what is different to apply on top of the default.
Therefore, the file you pass in with the `-f` flag does not need to include any of the default values, only values you have changed!
In the previous step, our `redisValuesFull.yaml` file could have just [been this](../charts/redis/redisValuesDiff.yaml):

```yaml
architecture: standalone

auth:
  enabled: false

master:
  persistence:
    enabled: false

replica:
  replicaCount: 1
  persistence:
    enabled: false
```

This file only specifies the values we want to change away from the defaults.
It is up to you if you want to include the entire default `values.yaml` file in your repository for posterity, or, if you want to only include the diffs.

You can learn more about Helm by [checking out the docs](https://helm.sh/docs/).

One thing to note is that a helm chart will apply a number of kubernetes manifests to your cluster.
This means that we might get many resources created.

For example, the redis chart that we just installed also created a `Service`, to allow redis to be accessed from other pods.

```shell
kubectl get svc -n redis
```

will output:

```
NAME             TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
redis-headless   ClusterIP   None            <none>        6379/TCP   155m
redis-master     ClusterIP   10.96.230.133   <none>        6379/TCP   155m
```

We will now use this `Service` to connect our application to the redis database.

## Using Service Discovery

Now that we have redis up and running, the last step is to configure our app to talk to it.

Note that in the output above kubernetes told us about the IP address of the `redis-master` service... IGNORE THIS.
This IP address will change. If the pod or service ends up restarting, DHCP will assign the services and the pods a random IP address.
Therefore, using the "CLUSTER-IP" address is not advised.

Instead, we want to use the kubernetes DNS.
Kubernetes has an internal DNS server which will keep track of all of the cluster's services and allow other pods to communicate to any deployed service through a special DNS.

To use Kubernetes' service discovery to connect to other apps, use a DNS of the format:

```
<serviceName>.<namespace>.svc.cluster.local
```

* `cluster.local` indicates that we want to use our kubernetes cluster.
* `svc` indicates that we want to connect to a service
* `<namespace>` is the namespace where the service we want to connect to resides, and
* `<serviceName>` is the name of the service we want to connect to.

So, in the example above, we had this output:

```
$ kubectl get svc -n redis
NAME             TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
redis-headless   ClusterIP   None            <none>        6379/TCP   155m
redis-master     ClusterIP   10.96.230.133   <none>        6379/TCP   155m
```

Thus, to connect to the `redis-master`, we would need to use a connection string of: `redis-master.redis.svc.cluster.local`.

Let's give this a try. Let's update our `ConfigMap` for our application to enable redis, and connect to this new redis
service using the Kubernetes service discovery URL.

### What will our new ConfigMap be?

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: python-app-config
data:
  config.yaml: |
    server:
      host: "0.0.0.0"
      port: 8080
      cowsay: "Something different!!"

    redis:
      enabled: True
      host: "redis-master.redis.svc.cluster.local"
      port: "6379"
```

Here we have enabled redis, used the kubernetes service discovery URL, and set the redis port.

---

Using this `ConfigMap`, we should now be able to communicate with the `redis` that we just deployed using helm.

### Let's update our application:

```shell
kubectl apply -f ../manifests/5-TalkToRedis.yaml
```

> **Note**: Running this command will update the `ConfigMap`, however, it will not update our `Pod` to use/mount the
> updated configuration. To get the updated configuration, we will need to restart our pod.

### Can you restart our application to load the new ConfigMap?

```shell
kubectl get pods -n calian
```

Get your pod's name...

```shell
kubectl delete pod <podName> -n calian
```

This will delete the current pod, but, because we are using a `Deployment`, a new pod will be created and the new pod
will get the updated `ConfigMap`.

---

Once re-deployed, our pod will have restarted successfully! With redis enabled, our application can now make use of data
persistence!

### Try Redis Endpoints

Use a port-forward or the NodePort ports to access your app and try:

```text
http://localhost:30007/put/someValue
http://localhost:30007/put/anotherValue
http://localhost:30007/put/evenMore
http://localhost:30007/get
```

Using `/put`, you are adding elements to a list in the Redis database. And by accessing `/get`, the app is fetching that
list from Redis!

---

## Summary

In this section we deployed a 3rd party database by using Helm Charts and learnt how to install a chart by overriding
some values. However, Helm is still a bit of a mystery... What happened to allow us to deploy redis??

In the next section we will dive deeper into Helm and learn what is happening under the hood by creating a helm chart of
our own!

---

## Navigation

[Home](../README.md)

[Creating a Helm chart](./9-helmCharts.md)
