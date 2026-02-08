# Priority Classes

When attempting to schedule your pods using [Resources and Requests](14-resourceLimits.md), it is possible that you might
run into the following exception:

```plaintext
Events:
 Type     Reason            Age        From               Message
  ----     ------            ----       ----               -------
 Warning  FailedScheduling  <unknown>  default-scheduler   0/7 nodes are available: 1 Insufficient memory, 1 node(s) had volume node affinity conflict, 5 Insufficient cpu.
```

This message is stating that the scheduler is unable to find a node that has enough CPU resources to schedule the pod
that you requested.  
You might look at your cluster resources and realize that - if certain pods were to be evicted and moved to another
location - there would be enough resources to schedule the pod.

This is where Priority Classes come in.

## What are Priority Classes?

Priority Classes are used to assign a priority to a pod.  
This priority is used by the scheduler to determine which pods should be scheduled first.  
During scheduling, if the scheduler cannot find a location to place the pod, it will see if it can evict pods to make
room.  
If it can, the scheduler will evict the lowest priority pods, to ensure that a higher priority workload is able to be
scheduled.

A pod's priority is a value in the range 0 to 1000000000, where 0 is the lowest priority and 1000000000 is the
highest.  
If a pod does not have a priority class assigned, it is given a default priority of 0.  
Therefore, if you have not created or set any priority classes, all of your pods will have the same priority, and thus
no pod will be evicted for another one.

## Creating and using Priority Classes

To create a priority class, a PriorityClass resource must be made within the cluster.  
Once it is made, it can be referenced in a pod's spec.

```yaml
apiVersion: scheduling.k8s.io/v1
kind: PriorityClass
metadata:
  name: high-priority
value: 1000000
globalDefault: false
description: "This priority class is for high-priority pods."
```

In this example, a priority class named `high-priority` is created with a value of 1000000, which allows pods using this
priority class to be scheduled before pods with a lower priority.  
The `globalDefault` field is set to `false`, which means that this priority class is not the default priority class for
the cluster.  
The `description` field is optional and can be used to describe the purpose of the priority class.

To use this priority class in a pod, the `priorityClassName` field must be set in the pod's spec as shown below.

```yaml
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      priorityClassName: high-priority
```

I recommend having a few priority classes that you can use for different types of pods.  
Examples include:

- `high-priority`
    - Critical workloads of your application
- `volume-priority`
    - Pods that require disk storage on a particular node when using a local volume
- `infra-priority`
    - Infrastructure pods that are required for the cluster to function. (eg. Kafka, Zookeeper, etc.)

By using these priority classes, you can ensure that critical workloads are scheduled first,  
pods with local-disk requirements can get scheduled to the right place, and infrastructure pods are not evicted by other
workloads.

For more information, see the Kubernetes documentation
on [Priority Classes and Preemption](https://kubernetes.io/docs/concepts/scheduling-eviction/pod-priority-preemption).

## Navigation

We now know how to use Resource limits and priority classes.  
But wouldn't it be nice if there was an easy way to see CPU, Memory, disk utilization, etc. for our apps?

The next section will start to cover how to monitor your applications in Kubernetes.

[Home](../README.md)

Next: [Intro to Observability](./16-metricStack.md)