# Volumes

In the previous section we learnt how to configure applications use CLI parameters, and environment variables. However,
we did not mention how to configure your application using external files.

Once your docker image is built, the layers in the image are read-only which makes it hard to pass in configuration
files to the container without re-building the image entirely.

Docker's solution to this is volumes.

## Bind Mounts

Bind mounts are the simplest version of volume mounts. Bind mounts map a directory on the host's filesystem to a
directory within the container. This is a two-way binding, and files written by the container will persist to the host,
while any new files generated on the host will also be accessible by the container as they are created.

The first step to creating a bind mount is creating a directory that contains the files you want to mount. Let's make
that directory:

```bash
mkdir docker-app-bind
```

Let's configure our application. Copy the default application's configuration file into the directory and let's modify
it:

```bash
cp ./src/exampleSettings.yaml ./docker-app-bind/settings.yaml
```

Change the settings to run on a different port (let's do 9999), and change `cowsay` to say something else. Our config
should look like this:

```bash
server:
  host: "0.0.0.0"
  port: 9999
  cowsay: "This is something different!!"
```

Once we have this new configuration file, we can run our docker container with a bind-mount. Bind mounts are similar to
port forwards (`-p hostPort:ContainerPort`); `-v hostPath:containerPath`.

Let's mount our files at `/data` within the container.

### Let's remove our previous container

```bash
docker rm python-container -f
```

And start the container with our volume mount (notice the `-p` is now using 9999 since we changed the port number):  
We could use `-p 8080:9999`. This will make it so that 8080 on our host will serve port 9999 on the container.

```bash
docker run \
   -it \
   -p 9999:9999 \
   -v $(pwd)/docker-app-bind:/data \
   --name python-container \
   python-training-container:1.0
```

Great! However.... let's look a bit closer at our output.

> {'server': {'host': '0.0.0.0', 'port': 8080, 'cowsay': 'moo'}, 'redis': {'enabled': False, 'host': '', 'port': ''}, '
> user': {'firstName': 'Admin', 'lastName': 'Admin', 'pet': 'Dogs'}}  
> Bottle v0.12.23 server starting up (using WSGIRefServer())...  
> Listening on http://0.0.0.0:8080/

Our config is printed out as the first line in the application. Not only is this configuration incorrect, but the
application is being served on the wrong port!

### What went wrong?

We didn't tell our app where to load our config file from!  
It uses a `--file` CLI parameter that has a default value if not set.

### Let's fix that! Can you figure out how?

```bash
docker run \
   -it \
   -p 9999:9999 \
   -v $(pwd)/docker-app-bind:/data \
   --name python-container \
   python-training-container:1.0 python3 -u main.py --file /data/settings.yaml
```

That worked! Our new configuration is printed out to the screen and we can also see that our application started on port
9999!  
We can now access our app at `localhost:9999`, and browsing to `localhost:9999/config` lets us see the config file.

However, what if we weren't sure if our files got attached to the container?  
Well, we can `docker exec` into our container to find out!

### Docker exec into the running container

```bash
docker exec python-container /bin/bash
```


Once in the container, `cd /data` (we mounted the directory to `/data` in the container). Run an `ls` and we can see
that the file exists. We can also `cat settings.yaml` to confirm this is the correct file!

Before exiting the terminal, let's showcase one more thing.  
In the terminal run `touch containerFile.txt`.

Now, let's go back to our host's `docker-app-bind` folder.  
You should see that the file got added here as well!  
This works both ways. You can add a new file on your host and doing an `ls` in the container will show the file within
the directory.

This is a powerful ability and lets you not only provide files into containers, but also take files out of your
containers.

## Summary

Volumes allow you to put files into a container as well as allow you to take files out of a container.  
Now that we can provide configuration files into our container, we can configure our container to talk to redis!

In the next lesson, we will talk about [Networking](./7-networks.md) and will experience how we can get our container
to talk to other services on various networks!

### Navigation

Next: [Networking](./7-networks.md)