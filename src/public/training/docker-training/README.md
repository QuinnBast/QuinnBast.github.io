# Docker Training

This training guide gives a hands-on experience to learning about docker.
After this training you will be able to run, manage, network, and even create your own custom containers.

This guide is broken up into multiple sections, however, it is recommended to go through the guide in order.

## Table of Contents

1. Installing Docker (This page)
2. [Understanding Docker](DockerTraining/1-UnderstandingDocker.md)
3. [Dockerhub & Running existing images](DockerTraining/2-Dockerhub.md)
4. [Dockerfiles](DockerTraining/3-Dockerfiles.md)
5. [Images and Tags](DockerTraining/4-images-and-tags.md)
6. [Working with Containers](DockerTraining/5-containers.md)
7. [Volumes](DockerTraining/6-volumes.md)
8. [Networks](DockerTraining/7-networks.md)
9. [Image Registry](DockerTraining/8-image-registry.md)
10. [Container Orchestration - Docker Compose](DockerTraining/9-docker-compose.md)

## What is Docker?

Docker allows you to put your software applications into an isolated virtual environment. Running your software in an
isolated environment prevents any outside influences from acting upon your software. This ensures that if your docker
container runs on one machine, it will run on any machine with docker installed. This eliminates the "Well it works on
my machine" phrase.

## Installing Docker

To follow along with this guide, it is assumed that you can follow along on a machine that has docker installed. If that
is not the case, you will need to install docker on your machine.

Follow the steps below depending on your OS:

* [Windows](https://docs.docker.com/desktop/install/windows-install/)
* [MacOS](https://docs.docker.com/desktop/install/mac-install/)
* [Linux](https://docs.docker.com/engine/install/#server)
    * Select your distribution from the "Server" section

To check if you have docker installed, run the following command:

```bash
docker version
```

If you have docker installed, this will print out some information about docker to your machine. If you do not have it
installed, it will fail with "command not found".

> **Note:** Sometimes, after the initial installation, you may need to run docker as `sudo`.

## What are you waiting for?

Docker is installed?

This tutorial will go through a full cycle of a sample project.
We will:

* Learn about docker images
* Find already existing images and learn how to run them
* Write a dockerfile to containerize a python application
    * No need to know python! The app is written for you!
    * Also see example Dockerfiles for Kotlin, Java, and more!
* Learn various way to run and configure your container
    * Change CLI params
    * Mount config files
    * Expose ports
* Network your container with other containers
    * Link to a database!
* Publish your image for the world (ie. Calian) to see and use!
* Learn how to orchestrate multi-container deployments
    * Docker compose!

[Dive right in](DockerTraining/1-UnderstandingDocker.md)
