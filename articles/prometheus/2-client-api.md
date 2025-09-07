---
title: "Instrumenting Prometheus Metrics"
date: 2025-09-06 19:00
description: "Instrument your applications to export Prometheus Metrics and enhance your application's observability."
path: "prometheus"
tags:
- Prometheus
- Observability
---
# Prometheus Client Metrics

In order to make our application expose metrics to Prometheus, we need to understand what kinds of metrics Prometheus has to offer.

One important thing to take note of is that Prometheus is NOT for string data.
Prometheus is mainly for numerical and quantitative results.
If you want to store high-cardinality string data, you will want to look at another solution.
Additionally, Prometheus is NOT a relational database.

And finally, Prometheus is not 100% accurate.
Because Prometheus scrapes your data on a timed interval, Prometheus aggregates data into time-chunks.
Prometheus does not keep every individually reported value, and instead they are summed over time.

### Metric Types

Prometheus offers 4 data types:

- Counters
- Gauges
- Histograms
- Summaries

#### Counters

A cumulative counter that can only increase or be reset to zero.
Counters should only represent numbers that can only increase and should not be used to represent things that go down.

Examples:

- Total method calls
- Tasks Completed
- Errors Thrown

#### Gauges

A gauge is a numerica value that can both increase and decrease.
Counters and Gauges will be your most frequently used metric types.

Examples:
- Number of running processes
- Threads being used
- GC Time

#### Histogram

Histograms are more complex. Histograms are more usefull for individually based metrics or data samples that do not relate to one another.
For example, the latency of a request, timings of functions, or data sizes.
The values can vary wildly, and having a latency of 5ms followed by 5s should be distinguishable.

Histograms break the collected data into "buckets", and the buckets allow you to easily determine how many collected values have been placed in a bucket within the given time.

The default buckets are:

.005, .01, .025, .05, .075, .1, .25, .5, .75, 1, 2.5, 5, 7.5, 10

However, I would recommend configuring the buckets based on your expected data.
Histograms expose a `count` and `sum` metric (which allows determining the average), and also exposes a `bucket` where you can get the sum of all values up to a specific upper bounded bucket.

Histograms should be used if you have many measurements and are looking for an average or percentile, you don't care much about the extreme outliers, or you want to know/isolate particular value ranges.

Examples:
- Request Timings
- Function Duration
- Data Sizes

#### Summaries

Summaries are the same as histograms, however, instead of known bucket values, summaries are quantile based.
Prometheus performs a sliding-window over a parcitular time range and determines the max and min values within the window.
These are then used to break down the other collected values into quantile buckets to view, for example, the 99th% for example.

Summaries are best used if you do not know the min/max values or are uncertain what buckets to select in a histogram.

# Using the Client Library

Now that we know about all the metric types, we can start to instrument our codebase to expose metrics to Prometheus!
Our most common uses will be the Counter and Gauge.

## Importing the Client Library

To get started, I have setup a sample Gradle Java project in this repository, however I have not instrumented it with any metrics.
To get started, open this repository in IntelliJ and open the `settings.gradle.kts` file.
A ribbon should appear to link the gradle project. Click the ribbon and the project will load.

To start instrumenting metrics, we need to add the Prometheus client library.
[Take a look at all of the Prometheus client libraries here.](https://prometheus.io/docs/instrumenting/clientlibs/)
There are some for nearly every language.

Since we are using a Gradle Java project, we will use the Java client library.
The Java library readme provides documentation on how to import the library with Maven, but we can easily update this to work with Gradle.

Add the following lines to the `build.gradle.kts` in the `dependencies` block (if not already there):

```kt
// Prometheus
implementation("io.prometheus:simpleclient:0.16.0")     // Prometheus metric lib
implementation("io.prometheus:simpleclient_hotspot:0.16.0")  // Prometheus JVM metrics
implementation("io.prometheus:simpleclient_httpserver:0.16.0") // Prometheus Server
```

## Instrumenting the Code

Now that the client is imported, we can start to instrument our code.
To start, we need to setup a server to expose our metrics.

#### Setting up the Prometheus Server

To do this, the java client readme shows an example:

```java
HTTPServer server = new HTTPServer.Builder()
    .withPort(1234)
    .build();
```

This will expose a metrics server at port `1234` locally. Let's put this code at the top of our `main` method.

Now, we want to take advantage of the default JVM metrics that Prometheus makes available to us.
To do this, simply add this line right below the creation of the server:

```java
DefaultExports.initialize();
```

With this setup, let's try to run our application. Press the play button to start the app.

By default, nothin will appear to happen. However, navigate to `localhost:1234` in a local web browser.

![Application Metrics](./images/prometheus/ExportedMetrics.png)

You should see a website with, what seems like a bit of gibberish.
However, these are all of the default JVM statistics that Prometheus is reporting about your application.
You will be able to see things like the allocated heap size, memory pool, garbage collection times, and more.

Additionally, looking back at the Prometheus GUI, our scrape target is now live!

![Successful Target](./images/prometheus/PrometheusSuccessfulTarget.png)


These are nice to know, and certainly nice metrics to have when looking at a JVM based application.
However, we want to see some custom information!
We want to know how long functions are running, what results we are capturing, how many times our methods are being called, and how many exceptions are being thrown!

#### Registering a Metric

To start reporting metrics, we need to setup one of the Metric types above.
This is extremely easy to do, setup a new `Counter` by adding a `static` variable to any class:

```java
    public static Counter countFoo = Counter.build()
        
        // The 'name' sets the name of the metric in Prometheus.
        // IMPORTANT: PREFIX ALL OF YOUR METRICS WITH THE SAME NAME.
        // For example: myProject_foo_count
        // This makes it easy for you to know what metrics in Prometheus are coming from your app!
        .name("sample_sampleClientApp_foo_count")
        
        // The help field describes what is being captured.
        // Required for every metric.
        .help("The number of times Foo has been called")
        
        // Completes the `Builder` and registers the metrics in the client so it can be exposed
        .register();
```

Making this variable `static` ensures that the `.register()` method is only called once for the metric value and ensures that the counter is only created once.

Once we have added this counter, we can make use of the `countFoo` variable in our code.
As the name suggests, let's count the number of times `Foo` was called.
Inside of the `Foo` method, at the top of the method add:

```java
countFoo.inc();
```
 
Great! Let's stop our app and run it again using the newly implemented Prometheus metric!
Once running, let's refresh our browser (`localhost:1234`) and Ctrl+F for `foo_count`.

![Custom Metric](./images/prometheus/OurMetricValue.png)

There it is! Our counter is in the list of metrics and is reporting a value of `2.0`!

### Understanding Labels

The metrics we have added so far are pure counters with now way to identify subsets of data.
But what if we want to have a metric that is differentiated by various fields?

We can use labels.

Labels are prometheus's way of adding columns/fields to your data.

Some examples might be:
- Counting method calls based on what parameters are passed to it.
- Get metric values based on various filters/fields.

Labels are a bit difficult to wrap your head around until you start using them so let's see this in action.

Let's assume we have a method that has various attributes:
- The returned value
- An input parameter that might effect calculations or an if statement
- A boolean that triggers certain functionality

We want to report the *value* of everything returned from this function as a metric, but we also want to be able to filter out and query based on the various inputs.
To do this, we need to add a label to be able to filter the data.
Let's make a new `Gauge` to track the metric (since the return type can be positive or negative).

This time, we want to add labels to the metric:

```java
public static Gauge calculationValue = Gauge.build()
        .name("sample_sampleClientApp_calculation_value")
        .help("The total value of calculations returned")

        // Indicates the metric has a label named "positive" and "satellite"
        // labelNames is vararg so you can add as many labels to this function as needed.
        .labelNames("wasFeatureEnabled", "color", "accountType")
        .register();
```

We use the `labelNames` field to indicate that this gauge should be able to be filtered by the `wasFeatureEnabled`, `color`, and `accountType` fields.
In relational database terms, adding `labels`, lets you perform a `WHERE` clause on these two fields.

IMPORTANT: Labels should be enumerable values. You should not use unique values like UUIDs or IDs as labels as this will
cause timeseries explosion.

Once we have registered the metric, we can record the calculated value whenever it happens:

```java
calculationValue
    // IMPORTANT:
    // .labels(...) sets the value of the labels for the reported metric
    // The values MUST be in the same order that they are specified in the .labelNames() function!!
    // Additionally, labels must be strings.
    .labels(featureFlag, color.toString(), accountType.toString())
        
    // Gauge's use `inc` with a specific value to increase the gauge's value
    .inc(calculationResult);
```

Once we have added our metrics, we can stop and restart our app to start reporting the new metrics we just added.

Easy Right?

Adding new Prometheus metrics to your app is as simple as adding a `static` variable to any existing class with the metric you want to collect.
This tutorial only covered `Counter` and `Gauge` types, but instrumenting a `Histogram`, or `Summary` is almost identical.

One important thing to note:

Avoid collecting "averages" or "rate" metric values with Prometheus, and instead, collect the sum of the total value.
Prometheus has averaging functions built-in to it's query language so instead of recording an `average` or "per second" value record the total count and let prometheus calculate the average when you query!

[In the next section](./3-promql.md) we will learn about how to find our data in the Prometheus GUI, and how to make use of PromQL.
