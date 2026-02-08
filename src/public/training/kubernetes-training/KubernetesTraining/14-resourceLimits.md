# Resource Limits and Requests

Kubernetes **requests** and **limits** determine how pods will ask for machine resources like CPU, memory, and more.  
Setting these values is VERY IMPORTANT. **ALWAYS** set your resource **requests** and **limits**.

|                     | **Requests**                                                                                                                                                                                                                                                                                                                                | **Limits**                                                                                                                                                                                                                        |
|---------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Definition**      | * The *minimum* amount required for your application to run.  <br>* Kubernetes *guarantees* that your app will have at least the **requested** amount of resources (with the possibility of getting more depending on the QoS class).  <br>* If a machine cannot provide the requested amount of resources your pod will not get scheduled. | * This is the maximum amount your application can use.  <br>* If your application uses above the **limit**:  <br>  ** CPU: Your application will get throttled.  <br>  ** Memory: Your pod will get OOMKilled and restart itself. |
| **Recommendations** | * This should be configured to be the regular amount of resources used during nominal operation.  <br>* Don’t lowball this number.  <br>* See the Quality of Service (QoS) Classes below.                                                                                                                                                   | * Should be set to the MAXIMUM you want your app to use.  <br>* See the Quality of Service (QoS) Classes below.                                                                                                                   |

## Kubernetes QoS Classes

Kubernetes has three Quality of Service (QoS) classes that are used to determine how pods are scheduled and how they are
treated when resources are scarce.

**NOTE**: If a request is set but not a limit, the limit is automatically set to the node’s maximum value.

|                | Guaranteed                                                                                                                                                                   | Burstable                                                                                                                                                                                    | Best Effort                                                                                                                                                                                                                                                                                               |
|----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Condition**  | **Requests** == **Limits**                                                                                                                                                   | **Requests** < **Limits**                                                                                                                                                                    | **Requests** are unset                                                                                                                                                                                                                                                                                    |
| **Definition** | * The pod is guaranteed to have the desired resources.  <br>* Never killed unless they exceed their **limit**.  <br>* Will evict other pods if it can to be able to operate. | * Application is guaranteed the **requested** amount.  <br>* Application can use up to the **limit** amount.  <br>* May be evicted to make room for Guaranteed QoS applications (if needed). | * Can use any resources available that remain on the node.  <br>* Lowest priority – Burstable pods will be prioritized resources.  <br>* Will get evicted to make room for other pods if they have a higher QoS and need the resources.  <br>* First to get killed if the cluster runs out of memory/CPU. |

## Finding your pod's resource usage

Resource usage is only reported if you are running
a [metrics server](https://github.com/kubernetes-sigs/metrics-server).  
I highly recommend running this service in your cluster.

While using [k9s](https://k9scli.io/) or by running `kubectl get pods -o wide` you will see columns that explain your
pod resource utilization:

* `CPU` - CPU Measured in millicores.
* `MEM` - Memory usage in Mb.
* `%CPU/R` - The percentage of **requested** CPU used.
* `%MEM/R` - Percentage of memory used / **requested**.
* `%CPU/L` - Percent of CPU **limit** used.
    - If this is >100, the pod is getting CPU throttled.
* `%MEM/L` - Percent of Memory **limit** used.
    - If this is high, it is very likely that the pod will get OOMKilled soon. Consider increasing the memory limit.

## Setting Resource Requests and Limits

To set resource requests and limits, you need to add the following to your pod spec:

```yaml
spec:
  containers:
  - name: myapp
    image: myapp:latest
    # Add a resource section to your container
    # This will set the requests and limits for CPU and Memory
    resources:
      requests:
        memory: "64Mi"
        cpu: "250m"
      limits:
        memory: "128Mi"
        cpu: "500m"
```

## Setting Resource Requests and Limits in Helm

Because we have updated our deployment to use helm, let's change our chart to allow users to set the resources.  
To do this, you can use the `values.yaml` file to set the defaults and allow the user to override them.

An example of a template that includes overrides for Kubernetes resources is shown below:

```yaml
spec:
  template:
    spec:
      containers:
        - name: yourContainerName
          resources: {{ toYaml .Values.container.resources | nindent 12 }}
```

This can then be set in the `values.yaml` file like so:

```yaml
container:
  resources:
    # Complex object here gets rendered in helm
    # Places the resources at the right spot in the template
    requests:
      memory: "64Mi"
      cpu: "250m"
    limits:
      memory: "128Mi"
      cpu: "500m"
```

Update our helm chart with these changes, deploy the new chart, and try modifying the resource limits to see what
happens.  
By setting the resource limits too low, you can see how the pod will behave when it's CPU is throttled or see how it
gets OOMKilled when exceeding the memory limit.

## Summary

When using Kubernetes, it is important to set your resource requests and limits.  
By setting these values, you can ensure that your pods are running efficiently and that they are not using more
resources than they need.  
This will help you to avoid issues like CPU throttling and especially to avoid your apps from the dreaded "OOMKilled".

## Navigation

[Home](../README.md)

Next: [Priority Classes - Boot useless pods](15-priorityClasses.md)