# Images and Tags

In the last lesson, we built a Dockerfile, and built an image from it. This lesson will learn more about images, their "
layers", and how to give images a meaningful name.

From the previous lesson, we can run `docker image ls` to see the image we created:

```bash
$ docker image ls
REPOSITORY  TAG        IMAGE ID       CREATED         SIZE
<none>      <none>     cad734f53f2a   3 hours ago     922MB
```

From this output, our container has no meaningful `tag`. Tags are the main identifiers for a docker image. In previous
lessons, we did `FROM python:3.9`, or `docker pull redis`. In these examples, `python:3.9` and `redis:latest` are the
image tags. These give meaning to docker image and tell you exactly what the docker image does.

To give our image a name, we can do one of two things:

- Rebuild the image and set the tag during the build
- Tag the existing image by referencing its image ID.

## Tagging images in a build

In the last lesson, we ran the command `docker build .` which generated this un-tagged image. We can easily set a tag
with the `-t` flag. Let's try it out:

```bash
docker build . -t python-training-container:1.0
```

The output from the command should say:

```bash
Successfully built cad734f53f2a
Successfully tagged python-training-container:1.0
```

And we can verify the image has a tag by running `docker image ls` again.

```bash
$ docker image ls
REPOSITORY                     TAG     IMAGE ID       CREATED         SIZE
python-training-container      1.0     cad734f53f2a   3 hours ago     922MB
```

Now our docker image has a meaningful name, as well as a version, so we can easily create new releases (like 1.1, etc.).
It is recommended to always tag your images.

## Re-tagging existing images

Alternatively, we can re-tag an existing image. Tags do not add any additional size to the image and are purely
metadata. Let's re-name our image again using `docker tag <currentTagOrImageId> <newTag>`

```bash
docker tag python-training-container:1.0 training-container:0.1
```

Alternatively, we could use the ImageID if no tag exists:

```bash
docker tag cad734f53f2a training-rename-again:wahoo
```

After re-tagging the image, a `docker image ls` will show you the results:

```bash
$ docker image ls
REPOSITORY                  TAG     IMAGE ID       CREATED         SIZE
python-training-container   1.0     cad734f53f2a   3 hours ago     922MB
training-container          0.1     cad734f53f2a   3 hours ago     922MB
training-rename-again       wahoo   cad734f53f2a   3 hours ago     922MB
```

Tags are just references to the underlying imageID. You can see that all 3 tags are pointing to the same image ID. This
means that all three of the tags, `python-training-container:1.0`, `training-container:0.1` or
`training-rename-again:wahoo` will get you the same docker image.

## Image layers

I mentioned before that images are built up with read-only layers. All three of these tags reference the image
`cad734f53f2a`, but where can we find the `layers` for this image?

Type `docker inspect cad734f53f2a`. This dumps a ton of information about the specific docker image, and also indicates
the file paths where the layers for the image are located. For me, this is under `/var/lib/docker/overlay2`.

## Removing Images

Removing images is fairly straight forward, simply run `docker image rm <imageTagOrImageId>`. However, it is important
to note that removing a `tag` is different from removing an `imageID`.

Let's remove a `tag` first:

```bash
$ docker image rm training-rename-again:wahoo
Untagged: training-rename-again:wahoo
```

You can see the output of this command is `Untagged`. Doing a `docker image ls`, the other two tags still exist for the
image. If the `tag` was the only one, the image would be removed completely, however, since other tags reference the
imageID, the image stays around.

However, removing the imageID, will completely remove the image:

```bash
$ docker image rm cad734f53f2a
Error response from daemon: conflict: unable to delete cad734f53f2a (must be forced) - image is referenced in multiple repositories
```

You can see the command errors because multiple tags are referencing the imageID. We can forcibly delete the imageID and
all associated tags with `-f`

```bash
$ docker image rm cad734f53f2a -f
Untagged: python-training-container:1.0
Untagged: training-container:0.1
Deleted: sha256:cad734f53f2acae5ddf712da54ce59862eca197d445ecffb99fa64074ec7766b
Deleted: sha256:0d6d8c285a9d96af1043f904610d4a71256a59b8d195f9c643ad116d7c437c32
Deleted: sha256:141e0ee49ec9df005d3695dae437a0be357b69ad37e6f81041a914dca8694c54
Deleted: sha256:defa2c4ce34dc192ef5bd696925e2364412fdc42bad31547d12c6aee27623ccd
Deleted: sha256:b43a4debd34074c7245bb9144ac86429be9c086d323c9b59f79857810c507d78
Deleted: sha256:7a634762500d0e5d427561b78737760e24e5e847a42097a1acc7392d45238881
```

In the output of this command you can see that it not only un-tags the existing tags, but it also completely deletes all
of the read-only files that define the image. Doing a `docker image ls`, the imageID no longer exists which means we
will need to perform another docker build to get it back.

For the next lesson, we will need the image around, so let's re-build our image:

```bash
docker build . -t python-training-container:1.0
```

## Cached layers

When docker builds your image, it will attempt to use cached layers. This makes running the build command multiple times
in a row quite fast. Once docker finds an invalid cached layer, it will invalidate ALL of the next layers. An invalid
cached layer is one where the command in the dockerfile has not changed.

For example, running this Dockerfile command, will ALWAYS result in the same layer, and therefore would be cached every
time.

```dockerfile
RUN echo "This is always the same"
```

However, consider this Dockerfile:

```dockerfile
COPY . /app
RUN echo "This is always the same"
```

In this scenario, we are copying files from our machine into the dockerfile, and then running the `echo` statement. If
the files from the COPY stage are different, docker will stop using cached layers, and begin creating new layers,
meaning that the `RUN echo`, command, which will always be the same, is now invalidated and will have to be re-executed.

We can use this to our advantage to reduce our build times by making commands that will change the least at the top of
the Dockerfile. Similarly, we can make commands that change the most often at the bottom.

For python this doesn't matter too much, but there is one optimization that we can make.

The first thing we do in our Dockerfile is `COPY ./src /app`, but, in a future step, we install all of our dependencies
while only using one file from the entire source set:

```dockerfile
RUN pip install -r requirements.txt
```

Our dependencies likely won't change often, and caching this layer will help to ensure that our dependencies are not
re-installed each time we go to build a new image if we aren't changing or updating them. To make our builds faster we
can instead:

- Copy only the `requirements.txt` file
- Install pip requirements from the file
- Copy the rest of the project
- Run the app

This should not be too hard of a change. Try doing this on your
own! [Reference the Dockerfile lesson for help](./3-Dockerfiles.md).

[Solution](./4-Dockerfile)

## Summary

You now know how to work with docker images, create docker images, and tag images.  
In the [next lesson](./5-containers.md), we will work with running your docker image as a container!

Before the next lesson ensure you have a docker image built:

```bash
docker build . -t python-training-container:1.0
```

## Navigation

Next: [Containers](./5-containers.md)
