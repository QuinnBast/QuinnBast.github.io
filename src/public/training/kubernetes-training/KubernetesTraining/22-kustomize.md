# Kustomize - Fixing Bad Helm Charts

Sometimes upstream Helm charts are not that great.
There can be missing fields, or maybe they hard-code some values that you might want to change.
Lots of things can go wrong, especially when beginners, who have no experience with Kubernetes, create a Helm chart from scratch.

In order to get around the limitations of Helm Charts without having to maintain a seperate fork their repository, we can use a tool called kustomize.

NOTE: A majority of the k8s community dislikes Helm (including myself).
Mainly because it is a templating engine but it tries to act like a package manager.
Unfortunately, it's the best thing we have right now, so we just have to deal with it...

## Kustomize

TODO

## Navigation

[Home](../README.md)

Next: []()