# Config Maps

## Configmaps

![Configmaps](../images/icons/cm.png)

In the previous section, we were able to deploy our application, and deploy it with a `Service` type. This allowed us to
deploy our app, configure it with some `env` or `cli` options, and then expose its ports to the outside world!

If you haven’t been following along with the previous section, run the following command:

```shell
kubectl create namespace calian || true
kubectl apply -f ../manifests/3-Service.yaml -n calian
```

**But we didn’t configure everything!**

That’s right. We were able to configure our app in every way EXCEPT by providing it a config file. Kubernetes allows you
to create configuration files in the cluster and provide them to your apps through `etcd`. This is a bit weird, as
`etcd` is actually just a key-value store, but it works for smaller configurations.

> **Note:** ConfigMaps have a file size limit, so large files cannot be provided through ConfigMaps.

To get started using ConfigMaps to provide config to your app, we will need to look at the documentation!

A Google search for *Kubernetes ConfigMap* will bring us
to [this page](https://kubernetes.io/docs/concepts/configuration/configmap/).

Again, everything in Kubernetes is a YAML manifest file, so we can either make a new file, or append to the previous
file to add our new `ConfigMap`.

### What will the start of our ConfigMap look like?

<details>
<summary>Show example</summary>

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: my-config-file
data:
```

</details>

Wait... `data`? I thought it was `spec`? Well, not for the `kind: ConfigMap`. In the `ConfigMap` kind, the `data`
section specifies a list of key-value pairs that should be provided to `etcd`. Pods can then query `etcd` using
volume-mounts to find the configuration they need.

`ConfigMaps` have two ways to provide config. One is simple key-value pairs (I wouldn’t recommend), and the other is
using your keys as file names, and the value as a large string containing your file
contents. [The documentation has an example of both](https://kubernetes.io/docs/concepts/configuration/configmap/):

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: game-demo
data:
  # property-like keys; each key maps to a simple value
  player_initial_lives: "3"
  ui_properties_file_name: "user-interface.properties"

  # file-like keys
  game.properties: |
    # This is now the contents of the file "game.properties"
    enemy.types=aliens,monsters
    player.maximum-lives=5    
  user-interface.properties: |
    color.good=purple
    color.bad=yellow
    allow.textmode=true  
```

Our app can use a configuration
file that looks like this:

```yaml
server:
  host: "0.0.0.0"
  port: 8080
  cowsay: "moo"

redis:
  enabled: False
  host: ""
  port: ""
```

Let’s port this to a `ConfigMap` type and use the file name: `config.yaml`. While we’re at it, let’s also change
something in the config; let’s change `cowsay` to anything you want!

### Can you figure out what the file will be?

<details>
<summary>Show example</summary>

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
      enabled: False
      host: ""
      port: ""
```

</details>

Perfect! We have created our configmap and we can now apply it to the cluster.

```shell
kubectl apply -f <path/to/configmap.yaml> -n calian
```

Unfortunately, it’s not that simple. Just creating the configmap will upload your file to the cluster (in `etcd`), but
it will not tell your pod to use the configmap. In order to tell your pod to use a configmap, you need to use a volume
mount to add files to the container. The k8s documentation also has a section
on [using ConfigMaps](https://kubernetes.io/docs/concepts/configuration/configmap/#using-configmaps) which shows how we
can update our Deployment to include the configmap.

What we need to do is:

1. In our `Deployment`, at the same indentation as `containers:`, add a new section for `volumes:`.
2. In the `volumes:` section, create a new volume, give the volume a `name`, and specify that the volume is from a
   `configMap`, and specify the `configMap.name` to load.
3. Once the `volume` is created and has a `name`, in any containers where we would like to put the config, add a
   `volumeMounts` section. This will specify where the file should be placed into the container.
4. In the `volumeMounts` section, specify the `name` of the VOLUME to mount, the `mountPath` (which directory it should
   go into), and if it should be `readonly`.

This is what we would need to add:

```yaml
kind: Pod
spec:
  containers:
    # This section mounts additional files to a container
    - volumeMounts:
        # The "name" here should reference the `volumes.name` that you want to mount
      - name: foo
        # The mountPath indicates the directory where we want to place the file.
        # The file will be named whatever your `key` in the ConfigMap was;
        # So for example, this would end up being placed at: /etc/foo/<ConfigMapKey>
        mountPath: "/etc/foo"
        # Indicates readOnly
        readOnly: true
  # Here we specify a list of volumes to be available
  volumes:
    # We set the name of our volume (to reference in the `volumeMounts` section)
  - name: foo
    # We indicate it should be populated from a configMap
    configMap:
      # We specify the `metadata.name` of the ConfigMap to use.
      # You can use `kubectl get configmap -A` to find the name of your configmap.
      name: myconfigmap
```

Let’s update our Deployment to:

- Make the container’s command now use the `--file <path/to/config.yaml>` argument
- Include the configMap in the `volumes` section
- Use a `volumeMount` to mount the configMap to the `/app/config` directory

### What will our updated Deployment look like?

<details>
<summary>Show example</summary>

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: python-rest-api
spec:
  selector:
    matchLabels:
      app: python-rest-api
  template:
    metadata:
      labels:
        app: python-rest-api
    spec:
      containers:
        - name: python-rest-api
          image: at-docker.eng.at.caliangroup.com/training/python-training-container:quinnbast
          env:
            - name: API_USER_FIRST_NAME
              value: Quinn
            - name: API_USER_LAST_NAME
              value: Bast
            - name: API_USER_FAVORITE_PET
              value: Puppies!
          ports:
            - containerPort: 8080
              
          # We can update the Command to pass `--file` which will load our config file!
          command: [ "python3", "main.py", "--experimental", "--file", "/app/config/config.yaml" ]

          # We add a volume mount to provide the file into the specified directory
          volumeMounts:
            - name: python-config
              # We specify that we want `config.yaml` (the key in the ConfigMap) to be placed in the `/app/config` directory
              mountPath: "/app/config"
              readOnly: true
      # Add a volumes section to indicate we want to include the configmap by its name
      volumes:
        - name: python-config
          configMap:
            name: python-app-config
```

</details>

Perfect, we are set! [See the full example here](../manifests/4-ConfigMap.yaml). Let’s apply our manifest!

```shell
kubectl apply -f ../manifests/4-ConfigMap.yaml -n calian
```

Our previously existing pod should terminate itself and a new pod should take its place. This new pod will now load from
the config file (through the `--file` CLI command). We can now check our app (use a port forward or check through the
NodePort service) to see that `http://localhost:8080/config` is now updated to be using our new configuration values!

## Summary

We have learnt how to use the `ConfigMap` resource type to specify a config file and provide configuration files to our
applications. In the next section, we will start looking at how to deploy 3rd party services!

The application we’ve deployed is able to interact with a Redis backend, and we want to deploy Redis to our cluster too!
We will learn a brief introduction to Helm to deploy 3rd party charts, and learn about Service Discovery.

## Navigation

[Home](../README.md)  
Next: [Helm - The Kubernetes Package Manager](./8-helm.md)
