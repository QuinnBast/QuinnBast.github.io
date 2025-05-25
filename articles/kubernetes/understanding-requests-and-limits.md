---
title: "Understanding Kubernetes Requests and Limits"
date: 2023-08-12 14:42
description: "What are they and why should they always be set?"
path: "Kubernetes"
tags:
- Kubernetes
- Resources
---
# Understanding Kubernetes Requests and Limits

Kubernetes <span style="color:green">requests</span> and <span style="color:red">limits</span> determine how a pod will ask for machine resources like CPU, memory, and more.
Requests indicate the amount that a container will be garunteed to have, while the limits indicate the absolute maximum it can use before getting throttled or stopped. Setting these values is VERY IMPORTANT and you should ALWAYS set your resource <span style="color:green">requests</span> and <span style="color:red">limits</span>.
The reason why these values are so important to set is because of the Kubernetes QoS classes that get applied to pods when particular conditions are met. QoS classes determine how pods are allocated cluster resources and what pods have priority.

|                | Requests                                                                                                                          | Limits                                                                                                                                                    |
|----------------|-----------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Definition     | The minimum amount required for your application to run.                                                                          | The maximum amount your application can use.                                                                                                              |
| Scheduling     | If a machine cannot provide the **requested** amount of resources, your pod will not get scheduled.                               | N/A                                                                                                                                                       |
| Throttling     | Kubernetes garuntees your app will have at least the **requested** amount of resources (or more).                                 | If your application uses above the CPU **Limit**, CPU will be throtled. If your application uses above the memory **Limit**, your pod will get OOMKilled. |
| Recommendation | **Requests** should be configured to be the regular amount of resources used during nominal operation. Don’t lowball this number. | Should be set to the MAXIMUM you want your app to use.                                                                                                    |
        
# Kubernetes QoS Classes

If a <span style="color:green">request</span> is set but not a <span style="color:red">limit</span>, the <span style="color:red">limit</span> is automatically set to the node’s maximum value.

|            | Garunteed                                                                                                                        | Burstable                                                                                                          | Best Effort                                                                                          |
|------------|----------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| Condition  | Requests == Limits                                                                                                               | Limit > Requests                                                                                                   | Requests unset                                                                                       |
| Definition | The pod is guaranteed to have the desired resources. Never killed unless they exceed the limit. Will evict other pods if it can. | Application is garunteed the requested amount, and cannot exceed the limit. May be evicted for Garunteed Qos Pods. | Can use any resources available that remain on the node. Lowest priority and will get evicted first. |

# Finding your Pod’s resource usage

Resource usage is only reported if you are running a [metrics server](https://github.com/kubernetes-sigs/metrics-server). I highly recommend running this service in your cluster.

While using [k9s](https://k9scli.io/) or by running `kubectl get pods -o wide` you will see columns that explain your pod resource utilization:

- `CPU` - CPU Measured in millicores
- `MEM` - Memory usage in Mb
- `%CPU/R` - The percentage of <span style="color:green">requested</span> CPU used
- `%MEM/R` - Percentage of memory used / <span style="color:green">requested</span>
- `%CPU/L` - Percent of CPU <span style="color:red">limit</span> used
    - If this is >100, the pod is getting CPU throttled
- `%MEM/L` - Percent of Memory <span style="color:red">limit</span> used
    - If this is high, it is very likely that the pod will get OOMKilled soon. Consider increasing the memory <span style="color:red">limit</span>.