# Create a Local Kubernetes Environment

Setting up and creating a Kubernetes Environment is probably the hardest part of working with Kubernetes.  
Bootstrapping and creating a cluster is a difficult task.  
However, for local development environments, there are a few tools that make this process very easy.

Which tool you use is up to you.  
Both tools have a few differences, however, at the surface, they are both Kubernetes clusters.

Select one of the following tools:

- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [KinD (Kubernetes in Docker)](https://kind.sigs.k8s.io/)

Both tools make use of Docker to start up a local Kubernetes environment.  
Minikube is more "out of the box" and "just works".  
KinD is similar but requires a bit of config to ensure ports are accessible outside the Docker container.

## Minikube

Minikube is probably the most popular tool for local Kubernetes environments.  
Install the latest version of Minikube with the following command:

```shell
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube
```

Verify the installation with:

```shell
minikube version
```

If this does not work, [follow the instructions here](https://minikube.sigs.k8s.io/docs/start/).

Once you have Minikube, start a local development environment [**using the Docker driver**](https://minikube.sigs.k8s.io/docs/drivers/docker/).  
To do this, run the following command:

```shell
minikube start --driver=docker
```

Wait for the cluster to start, and once it is running, verify your cluster is working with:

```shell
kubectl get pods -A
```

You should see some `kube-system` pods show up.

Success! You have successfully installed Minikube and created a local cluster.  
(Skip the KinD installation if you followed this step.)

You can stop your cluster at any time with `minikube stop`.

### KinD (Kubernetes in Docker)

KinD is an alternative to Minikube but functions in a similar way, however, before running a cluster, KinD requires configuration.  
Install KinD with the following command:

```shell
curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.22.0/kind-linux-amd64
chmod +x ./kind
sudo mv ./kind /usr/local/bin/kind
```

Verify the installation with:

```shell
kind --version
```

If this does not work, [follow the instructions here](https://kind.sigs.k8s.io/docs/user/quick-start/).

Once installed, it is time to start a Kubernetes cluster.  
Run the following command to start a new cluster:

```shell
kind create cluster --config kind.yaml
```

**NOTE:** `kind.yaml` is a file in this repository. You will want to copy this file to reference it during cluster creation.

Wait for the cluster to start, and once it is running, verify your cluster is working with:

```shell
kubectl get pods -A
```

You should see some `kube-system` pods show up.

Success! You have successfully installed KinD and created a local cluster.

You can stop your cluster at any time with `kind delete cluster` (but please don't).

## One step closer!

With a running local Kubernetes cluster, you are one step closer to working with Kubernetes.  
In the next section, we will start getting technical.  
We will learn about what Kubernetes manifests are and how to create and apply them to a cluster.

## Navigation

[Home](../README.md)  
Next: [Kubernetes Manifests](./2-manifests.md)