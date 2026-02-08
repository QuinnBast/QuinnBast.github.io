# Applying Helm Charts

See [the lesson on Helm](../../lessons/helm.adoc).

# Cheat Sheet

Steps to use a helm chart:

1. Obtain the [Redis helm chart](https://github.com/bitnami/charts/tree/main/bitnami/redis), for example, by googling "Redis Helm Chart", for example.
2. Find the `values.yaml` for the chart and copy it into your repository
3. Surf the config (through the `values.yaml` or the chart's README.md) and figure out what to change. For example, for Redis:
   1. The architecture type to be `standalone` for single-redis deployment
   2. Disable persistence
   3. Disable authentication
4. Apply the custom values and install the helm chart with:

```bash
kubectl create namespace redis
helm install redis oci://registry-1.docker.io/bitnamicharts/redis -f redisValuesFull.yaml --namespace redis
```

### Connecting to other services:

Connect to any k8s service from another pod/app by using the k8s service discovery URL:

```yaml
<serviceName>.<namespace>.svc.cluster.local
```

Where `<serviceName>` is the `NAME` in `kubectl get svc -A`, and the `<namespace>` is the namespace the service is in.
This URL will allow kubernetes DNS to resolve to the correct IP address of the running service and properly route you to the app you need.

Do not use the pod's "IP Address" that is shown in `kubectl get svc`.
These IPs WILL CHANGE if the services or pods restart.
