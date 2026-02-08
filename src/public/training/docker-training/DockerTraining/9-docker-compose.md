# Docker compose

Docker compose is a tool to help deploy multiple docker containers at the same time.  
Generally, if you deploy one docker container, you will likely want to deploy another container alongside it, for
example a database or other network service for it to talk to.

Docker compose allows you to define all the docker containers, networks, volumes, etc. that you want to deploy and will
ensure that all of your containers start with a single command.

Docker compose is now installed along with docker installations and so the commands should be available to you.  
However, docker-compose used to be a standalone binary that was installed outside of docker.  
If you do not have access to docker compose you may need to install it.

## Understanding docker compose files

The docker compose file is a YAML file that defines a list of the services you want to deploy.  
It also allows you to specify volume mounts, environment variable overrides, as well as ensure that a container does not
start until dependent containers are running.

To start, let's write a docker-compose file for our application as standalone and then let's add redis after.  
[See the full YAML specification here](https://docs.docker.com/compose/compose-file/compose-file-v3/).

Create a new file called `docker-compose.yaml`, and let's add the following content (read through the file to
understand):

```yaml
# First, we indicate what version of docker-compose we want to use:
version: "3.9"

# Next, the 'services' block defines the list of services we want to deploy:
services:

  # The top level keys are the name of the service.
  # For us, let's keep using "python-container" as the name
  python-container:
    # Now we defined the parameters to the docker run command in a YAML format:

    # Same as `-p`
    ports:
      - 9999:9999

    # The image name
    image: at-docker.eng.at.caliangroup.com/training/python-training-container:1.0

    # Environment variables
    environment:
      API_USER_FIRST_NAME: "Quinn"
      API_USER_LAST_NAME: "Bast"
      API_USER_FAVORITE_PET: "Puppies!"

    # Volume mounts
    volumes:
      - ./docker-app-bind:/data

    # Override the default command
    command: "python3 -u main.py --file /data/settings.yaml"
```

This file format should look extremely familiar.  
All the YAML tags in this file we have already learnt about when learning the various ways we can run a container.  
However, now we have encapsulated the `docker run` command as a YAML file, so not only is the container's runtime
environment known to anyone trying to run the container, it is also tracked within git.

Since we are not starting redis just yet, let's disable redis in the config file by setting `redis.enabled` to false.

Once that is all set, we should be ready to start our service!  
Run the command `docker compose up` to start all the containers that are defined within the `docker-compose.yaml`
file.  
This will put the running containers in your terminal.  
Just like other docker commands, we can use `-d` to detach the process: `docker compose up -d`.

Just like before, we now have a running container, the ports are forwarded, the environment variables have been set, the
volume has been mounted, and our container is up!  
You can even navigate to `localhost:9999` in your browser to confirm that the application is accessible and the settings
are updated.

To stop the services, simply run `docker compose down`. This will stop all the running containers.

## Networking and depending on other Services

Let's add redis to our docker-compose file.

This should be pretty simple to do:

```yaml
services:

  # Add this part under the 'services' block
  redis:
    image: redis
```

That's it! We are not setting any special configurations for redis so there is no need to set ports, volumes, or
anything more.

Now that we have redis, how do we talk to it?  
Well, as we saw in the networking section, there are various ways to talk to other containers.  
Luckily, docker-compose creates a default network for all containers that are in the same file!  
This lets us easily just change our redis configuration to communicate with the `redis` host, and it will be able to
find our redis instances.

Set `redis.enabled` to true, and set the host to `redis`, and our services (containers) should be able to talk!  
Run another `docker compose up -d` and watch docker compose start all of our containers at the same time!  
Running a `docker ps` we can see that all of our containers have been started and accessing our application in the
browser works perfectly!

Now, it may be the case that one of our other containers should not start unless the first is started. For example, we
probably don't want our container to start until redis has started.  
To do this, we can use the [
`depends_on` keyword](https://docs.docker.com/compose/compose-file/compose-file-v3/#depends_on).

Let's make our python container depend on redis:

```yaml
services:
  python-container:

    # Add this to the python container service
    depends_on:
      - redis
```

Now when we run a `docker compose up`, the redis container will start first, followed by our application.  
This ensures that our application will always be able to connect to redis when it starts up.

## Summary

Docker compose is an extremely powerful tool that lets you manage and deploy multiple containers at the same time.  
In addition, it makes your docker commands into files which can be managed and tracked by git.  
This also makes it easier for other developers to see your deployment settings and make changes if needed.

If you've followed along with everything so far, you should be a docker pro!  
Thanks for learning along and feel free to contribute to this guide if there is anything missing!

For a more advanced course using docker, see the [kubernetes-training](../../kubernetes-training/README.md) module to get started with kubernetes!
