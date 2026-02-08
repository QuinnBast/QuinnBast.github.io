# Creating Custom Helm Charts

In the last section, we learned how to install a 3rd party Helm chart to our cluster. However, by just installing this
chart, we didn't really get a full understanding of what Helm does or how it works.

Under the hood, Helm is just a file templating solution that allows customizing & filling templated files with
configured values. Think of it like templated strings in Java. For example,

```java
System.out.println(
    "I have {} apples, {} will expire in 1 day.",
    appleCount,
    expiringAppleCount
);
```

In this Java code, the `{}` will get replaced with the variables that are sent to the templating method. This is exactly
what Helm does. Let's dive right in.

## Our First Helm Chart

In the previous lessons, we have been manually running `kubectl apply -f` on a file that keeps getting bigger. The last
file we applied was [this one](../manifests/5-TalkToRedis.yaml), which includes a Deployment, Service, and ConfigMap.
However, this file has a lot of configurations and other values that are pre-configured. If someone else wanted to come
in and change things, it would be difficult to do.

To make things a bit easier, we are going to take our file and put it into a Helm chart.  
The Helm documentation [can be found here](https://helm.sh/docs/topics/charts/) and does a good job at explaining all of
the intricacies of the system. There is also a very good
introductory [Getting Started](https://helm.sh/docs/chart_template_guide/getting_started/) page on the Helm website that
I would recommend reading.

Taking some of their words:
> A chart is organized as a collection of files inside of a directory.

> Inside of this directory, Helm will expect a structure that matches this:

```plaintext
wordpress/
  Chart.yaml          # A YAML file containing information about the chart
  LICENSE             # OPTIONAL: A plain text file containing the license for the chart
  README.md           # OPTIONAL: A human-readable README file
  values.yaml         # The default configuration values for this chart
  values.schema.json  # OPTIONAL: A JSON Schema for imposing a structure on the values.yaml file
  charts/             # A directory containing any charts upon which this chart depends.
  crds/               # Custom Resource Definitions
  templates/          # A directory of templates that, when combined with values,
                      # will generate valid Kubernetes manifest files.
  templates/NOTES.txt # OPTIONAL: A plain text file containing short usage notes
```

Of these files, the ones that are the most important are:

- `Chart.yaml` - Contains metadata about the chart
- `values.yaml` - Default config values for the chart
- `templates/` - A directory of templated manifest files to apply

So let's start by creating a directory for our chart and creating these files.

```bash
mkdir ../charts/pythonChart
mkdir ../charts/pythonChart/templates
touch ../charts/pythonChart/Chart.yaml
touch ../charts/pythonChart/values.yaml
```

## The Chart.yaml

Let's start with the `Chart.yaml`. This file is the easiest to set up because it is just a bunch of metadata.  
[The helm docs](https://helm.sh/docs/topics/charts/#the-chartyaml-file) outline what is all required (and what is
optional) for this file.

Even though their example looks extremely large, there are only really 3-4 required fields that need to be in the
Chart.yaml file.

### Which fields are required?

<details>
<summary>Click to expand</summary>

- `apiVersion` - The apiVersion (version of Helm) we are using
- `name` - The helm chart name
- `version` - The version of our chart

</details>

That is pretty simple. The `apiVersion` for Helm charts using Helm 3 or above should have an `apiVersion` of `v2`.  
Knowing this, we can make our file!

### Look at the docs. What is the resulting file?

<details>
<summary>Click to expand</summary>

```yaml
# Set the apiVersion to indicate Helm 3
apiVersion: v2
# Name our Helm chart (Can be anything but when we publish charts, we need to use this name to reference it)
name: python-rest
# The current version of the chart. We should start this at 0.0.1 with semVer
# Note this MUST be a semVer: https://helm.sh/docs/topics/charts/#charts-and-versioning
version: 0.0.2
# Optionally add a description
description: Helm chart for deploying python REST application
```

</details>

## Starting a Template

Now that we've created our `Chart.yaml`, let's start templating!  
Everything in the `/templates` directory is a Kubernetes manifest to apply to a cluster.  
Well, that's convenient! We just so happen to have a Kubernetes manifest that we have been applying.

Let's copy our current manifest into the chart:

```bash
cp ../manifests/5-TalkToRedis.yaml ../charts/pythonChart/templates/pythonDeploy.yaml
```

Great, now we could package, publish, and even install this chart.  
However, that would be silly. We haven't allowed any downstream users to customize the deployment.

Let's look at our Kubernetes manifest.  
As we deploy this file, or we get potential downstream users, we will want to allow changes to our files based on
various deployment targets or changing software versions.

### What things might we want to change down the line?

<details>
<summary>Click to expand</summary>

The real answer is: Almost everything!!  
You never know what someone's deployment environment looks like, so allowing the downstream users to configure almost
everything is the best way to think.

However, we can't change everything for this tutorial. Some smaller examples might be:

- The docker image
- The container's environment variables
- The exposed port
- The app's config file

</details>

Let's start with something simple.  
A single value replacement.  
Let's allow people to change the docker image that gets deployed.

To do this, change the value on line 17 at `spec.template.spec.containers[0].image` to use Helm templating.

### What is a Helm template?

A Helm template is anything surrounded by `{{ }}`.  
Helm uses these delimiters to indicate that it should parse and fill in the contents with something else.  
To tell Helm that we want to allow a user to change this value through the `values.yaml` file, we use the syntax:
`{{ .Values.path.to.override }}`.

For example, `{{ .Values.path.to.override }}` would mean that in the `values.yaml`, a value at: `path.to.override` would
get templated in.

For example, some file in the `/templates/` dir might look like this:

```yaml
spec:
  template:
    spec:
      containers:
        - image: {{ .Values.app.image }}
```

With the corresponding `values.yaml`:

```yaml
app:
  image: "Beam me up, Scotty!"
```

Once we run a `helm template` against this helm chart, the resulting output would be:

```yaml
spec:
  template:
    spec:
      containers:
        - image: "Beam me up, Scotty!"
```

Let's try this out.  
Docker images have 3 parts: the registry, the name, and the tag in the form: `registry/name:tag`.

### Template our file to include a way to replace all 3 parts of the docker image.

<details>
<summary>Click to expand</summary>

#### templates/pythonDeploy.yaml

```yaml
# Generally include the appName as a top-level key as we may deploy multiple.
image: {{ .Values.pythonRestApi.image.registry }}/{{ .Values.pythonRestApi.image.name }}:{{ .Values.pythonRestApi.image.tag }}
```

#### values.yaml

```yaml
pythonRestApi:
  image:
    registry: at-docker.eng.at.caliangroup.com
    name: training/python-training-container
    tag: quinnbast
```

</details>

### How do I know it worked?

Easy. We can run `helm template` ([docs here](https://helm.sh/docs/helm/helm_template/#helm-template)).  
The `helm template` command will locally render helm charts and display the resulting output to the terminal.

To test, we can:

```bash
cd ../charts
helm template ./pythonChart
```

The command should dump the Kubernetes manifest file to the console.  
We can look through the output and find the `image:` tag. Or, optionally, we can run it with a `grep`:

```bash
helm template ../charts/pythonChart | grep image:
```

And we see:

```text
$ helm template ../charts/pythonChart | grep image:
image: at-docker.eng.at.caliangroup.com/training/python-training-container:quinnbast
```

Now, let's change the `values.yaml` to have some other stuff:

#### values.yaml

```yaml
pythonRestApi:
  image:
    registry: newRegistry
    name: someDockerImage
    tag: someTag
```

The new output:

```text
$ helm template ../charts/pythonChart | grep image:
image: newRegistry/someDockerImage:someTag
```

As we can see, the Kubernetes manifest is taking the values in from the `values.yaml` file and setting these values into
the Kubernetes manifest.

Let's continue templating our manifest!

### Templating Lists

Next, we will allow downstream users to set the environment variables.  
However, in the manifest, this is an array of many possible values, which means a user might have many possible
configurations.

Helm allows us to loop through a list of values using a `range`.  
[There is a good example here](https://helm.sh/docs/chart_template_guide/control_structures/#looping-with-the-range-action).

Imagine the `values.yaml`:

```yaml
list:
  - valueOne
  - valueTwo
```

This can be templated in Helm with:

```yaml
someList:
  # Loop the list. This is like a forEach.
  {{- range .Values.list }}
  # Add the "-" hyphen of the list and then render {{ . }} -- the value of each looped item
  - {{ . }}
  # End the loop
  {{- end }}
```

If we have a list of complex objects, for example:

```yaml
list:
  - name: myValue
    value: value
  - name: valueTwo
    value: Value!
```

Then instead of `{{ . }}`, we can render each item's keys as follows:

```yaml
someList:
  # Loop the list. This is like a forEach.
  {{- range .Values.list }}
  # Add the "-" hyphen of the list and then render a more complex object.
  # {{ .name }} grabs the "name" of each item in the list.
  - name: {{ .name }}
    value: {{ .value }}
  # End the loop
  {{- end }}
```

### Let's update the containers' environment variables to be a templateable list!

<details>
<summary>Click to expand</summary>

#### templates/pythonDeploy.yaml

```yaml
env:
  {{- range .Values.pythonRestApi.env }}
  - name: {{ .name }}
    value: {{ .value }}
  {{- end }}
```

#### values.yaml

```yaml
pythonRestApi:
  env:
    - name: API_USER_FIRST_NAME
      value: Quinn
    - name: API_USER_LAST_NAME
      value: Bast
    - name: API_USER_FAVORITE_PET
      value: Puppies!
```

</details>

We have now configured our helm chart to allow users to set any environment variable they want!

## What's the purpose?

You might be wondering by going through this, what is the point of doing this?  
People can just come in and change these files themselves if they want any changes.

Well, yes but no. Consider this:

- By copying the file to many places, there is no source of truth.
  - Any updates now need to be applied in every location.
- Downstream users would otherwise need to copy & paste files out of your repository.
- Having multiple deployment targets or environments would require many re-writes of these files.

The main reason to use Helm is the ability to overlay multiple `values.yaml` files on top of one another.  
This makes it extremely easy to set up configurations that are environment-specific, allowing you to more easily manage
configuration for your various environments.

When running helm commands, the `-f` flag allows you to pass in multiple extra `values.yaml` files.  
This flag is extremely important. Using this flag, we can pass as many `values.yaml` files on top of the chart as we
want!

Let's see this in action!

I have already created the helm chart we were templating above [here](../charts/ourChart).  
And I have included two `values.yaml` files.

The default `values.yaml` file references the image:

### values.yaml

```yaml
image:
  registry: 172.29.127.27:5000
  name: training/python-training-container
  tag: quinnbast
```

We can see this with a `helm template`:

```bash
$ helm template ../charts/ourChart | grep image:
image: 172.29.127.27:5000/training/python-training-container:quinnbast
```

The `example_override_values.yaml` includes a different config which changes the docker image:

### example_override_values.yaml

```yaml
pythonRestApi:
  image:
    registry: myPrivateRegistry
    name: myCustomContainer
    tag: myCustomTag
```

We can apply this using the `-f` flag to `helm template`, and notice the difference:

```bash
$ helm template ../charts/ourChart -f ../charts/ourChart/example_override_values.yaml | grep image:
image: myPrivateRegistry/myCustomContainer:myCustomTag
```

The `image` has been overwritten! This is why Helm is so powerful.  
Let's look at a real example where a user might be deploying something to a variety of locations:

Consider the helm chart we just made.  
What if someone is deploying this to their cluster but doesn't want certain features enabled.

Their `values.yaml` might look like:

### values.yaml

```yaml
pythonRestApi:
  env:
    - name: SOME_FEATURE
      value: False
```

They want this applied to every deployment.

But, then, imagine they need to deploy to various different clusters.  
Each cluster cannot access the internet and so each cluster needs to set the docker image registry differently.

One cluster might have:

```yaml
pythonRestApi:
  image:
    registry: testbedRegistry:5000
```

While a different cluster might have:

```yaml
pythonRestApi:
  image:
    registry: productionRegistry:5000
```

Helm makes this easy. Their file structure might look like this:

```plaintext
| - base_values.yaml
| - testbed_values.yaml
| - production_values.yaml
```

And based on the environment they deploy to, they just use a different override file:

```bash
# For testbed deployments they apply the testbed values
helm template ../charts/pythonChart -f base_values.yaml -f testbed_values.yaml
# For production deployments they apply the production values
helm template ../charts/pythonChart -f base_values.yaml -f production_values.yaml
```

## Publishing Helm Charts

Once we have created a helm chart, we don't want our downstream users to have to have access to our entire git
repository just to deploy it.  
Luckily, helm repositories allow us to publish our helm charts for downstream consumers.  
This makes it easy to create a "deployment" that can be applied to any Kubernetes cluster.

Before we can upload our chart to a registry, we need to check a few things.

### Verifying our Chart

- Ensure that the `Chart.yaml` has the correct name and version

The upload command uses the chart's `Chart.yaml` to figure out the chart name and version.  
Ensure that if you've made changes, you bump the version number or you risk overwriting a previous version!

- Ensure our Helm chart lints

It wouldn't make sense if we uploaded a helm chart that was broken!  
Helm gives us two options to verify things are working.

- `helm lint` will lint and check a helm chart to make sure it works.
- `helm template` will try to render the helm chart locally and dump the output.

For checking helm charts, I prefer `helm lint`.

```bash
$ helm lint ../charts/ourChart
> ==> Linting ../charts/ourChart<br/>
> [INFO] Chart.yaml: icon is recommended<br/>
>
> 1 chart(s) linted, 0 chart(s) failed
```

### Publishing our Chart

Once we have confirmed our chart is ready, we need to save it as a `tgz`. This can be done with:

```bash
helm package ../charts/ourChart
```

Notice the output:

```bash
$ helm package ../charts/ourChart
Successfully packaged chart and saved it to: /Documents/kubernetes-training/lessons/python-rest-0.0.2.tgz
```

Once we have the `tgz`, we can upload it to a registry.

Calian has an internal registry called Artifactory which can be used.  
In Artifactory, there is a repository
called `at-helm-local`, which is where we can upload our charts.

First, let's add the Artifactory repository:

```bash
helm repo add calian-helm https://artifactory.eng.at.caliangroup.com:443/artifactory/at-helm-local --username <username> --password <password>
helm repo update
```

Once the repository has been added, publishing a chart needs to be done with CURL:

```bash
curl -u <username>:<password> -T <yourFileName> https://artifactory.eng.at.caliangroup.com:443/artifactory/at-helm-local/<yourFileName>
```

This will upload your chart to Artifactory.

> **Note**: There are also `OCI` registries that can accept helm charts.  
> The Gitlab Registry is an example of an `OCI` registry.

To push to OCI repositories, we can use:

```bash
helm push <your tgz> oci://<yourRegistry>
```

For example, to push to the Gitlab registry, we could do:

```bash
helm push ./python-rest-0.0.2.tgz oci://registry.gitlab.com/calianat/software/training/kubernetes-training/helmcharts
```

## Summary

In this lesson, we learned the basics of creating your own Helm chart and we also learned some practical applications of
Helm.  
Although we only just scratched the surface of Helm, this introduction gives an understanding as to why we should be
making Helm charts for any Kubernetes deployment.

### Advanced Techniques

There are many different templating techniques that helm uses.  
The `range` and the `.Values` keywords are just two of the most commonly used templating functions.  
If you are going to be doing helm chart development, I would
recommend [reading their getting started guide](https://helm.sh/docs/chart_template_guide/getting_started/) to get a
full understanding of everything you can do.

Additionally, don't start from scratch!  
There are many helm chart template you can reference online for things like Serivces, Deployments, etc. 
For example, Deployments, Deployments with a Service, and ConfigMaps which are very commonly used.

Great work!  
In the next section, we will talk about Ingress controllers and learn how `nginx` works.

## Navigation

[Home](../README.md)

Next: [Ingress Controllers - Route your HTTP Traffic](./10-ingress.md)
