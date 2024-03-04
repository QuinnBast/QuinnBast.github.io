---
title: "Understanding Kubernetes Requests and Limits"
date: 2023-08-12 14:42
description: "What are they and why should they always be set?"
tags:
- Kubernetes
- Resources
---

<PageHeader/>

# Understanding Kubernetes Requests and Limits

Kubernetes <span style="color:green">requests</span> and <span style="color:red">limits</span> determine how a pod will ask for machine resources like CPU, memory, and more.
Requests indicate the amount that a container will be garunteed to have, while the limits indicate the absolute maximum it can use before getting throttled or stopped. Setting these values is VERY IMPORTANT and you should ALWAYS set your resource <span style="color:green">requests</span> and <span style="color:red">limits</span>.
The reason why these values are so important to set is because of the Kubernetes QoS classes that get applied to pods when particular conditions are met. QoS classes determine how pods are allocated cluster resources and what pods have priority.


<table>
    <tr>
        <th scope="col"></th>
        <th scope="col"><span style="color:green">Requests</span></th>
        <th scope="col"><span style="color:red">Limits</span></th>
    </tr>
    <tr>
        <th scope="row">Definition</th>
        <td>
            <ul>
                <li>The minimum amount required for your application to run.</li>
                <li>Kubernetes guarantees that your app will have at least the <span style="color:green">requested</span> amount of resources (with the possibility of getting more depending on the QoS class).</li>
                <li>If a machine cannot provide the <span style="color:green">requested</span> amount of resources your pod will not get scheduled.</li>
            </ul>
        </td>
        <td>
            <ul>
                <li>This is the maximum amount your application can use.</li>
                <li> If your application uses above the <span style="color:red">limit</span>: </li>
                <ul>
                    <li>CPU</li>
                    <ul><li>Your application will get throttled</li></ul>
                    <li>Memory</li>
                    <ul><li>Your pod will get OOMKilled and restart itself</li></ul>
                </ul>
            </ul>
        </td>
    </tr>
    <tr>
        <th scope="row">Recommendations</th>
        <td>
            <ul>
                <li>This should be configured to be the regular amount of resources used during nominal operation.</li>
                <li>Don’t lowball this number.</li>
                <li>See the Quality of Service (QoS) Classes below</li>
            </ul>
        </td>
        <td>
            <ul>
                <li>Should be set to the MAXIMUM you want your app to use.</li>
                <li>See the Quality of Service (QoS) Classes below</li>
            </ul>
        </td>
    </tr>
</table>


# Kubernetes QoS Classes

If a <span style="color:green">request</span> is set but not a <span style="color:red">limit</span>, the <span style="color:red">limit</span> is automatically set to the node’s maximum value.

<table>
    <tr>
        <th scope="col"></th>
        <th scope="col">Garunteed</th>
        <th scope="col">Burstable</th>
        <th scope="col">Best Effort</th>
    </tr>
    <tr>
        <th scope="row">Condition</th>
        <td><span style="color:green">requests</span> == <span style="color:red">limits</span></td>
        <td><span style="color:red">Limit</span> > <span style="color:green">requests</span></td>
        <td><span style="color:green">Requests</span> are unset</td>
    </tr>
    <tr>
        <th scope="row">Definition</th>
        <td>
            <ul>
                <li>The pod is guaranteed to have the desired resources.</li>
                <li>Never killed unless they exceed their <span style="color:red">limit</span>.</li>
                <li>Will evict other pods if it can to be able to operate</li>
            </ul>
        </td>
        <td>
            <ul>
                <li>Application is guaranteed the <span style="color:green">requested</span> amount.</li>
                <li>Application can use up to the <span style="color:red">limit</span> amount.</li>
                <li>May be evicted to make room for Guaranteed QoS applications (if needed).</li>
            </ul>
        </td>
        <td>
            <ul>
                <li>Can use any resources available that remain on the node.</li>
                <li>Lowest priority – Burstable pods will be prioritized resources.</li>
                <li>Will get evicted to make room for other pods if they have a higher QoS and need the resources.</li>
                <li>First to get killed if the cluster runs out of memory/CPU.</li>
            </ul>
        </td>
    </tr>
</table>

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
