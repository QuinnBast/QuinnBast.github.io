---
title: "Why is my Resource Stuck Terminating?"
date: 2023-12-13 15:21
description: "Have you deleted a resources just for it to get stuck Terminating? It's probably due to your PV."
tags:
- Kubernetes
- Persistent Volumes
- Container Storage
---

<PageHeader/>

# Why is my Resource stuck Terminating?!

This is a common question I see often when helping to debug Kubernetes clusters.
I have googled this many times myself, and any blog post or answer that I have found, while it might help remove the resource, doesn't explain the root cause and how to avoid it in the future.

The most important thing to know is that the main reason a resource gets stuck "Terminating" is due to finalizers that get attached.
And the most common reason for a finalizer to be attached to your resources, is because it has an attached persistent volume.
All of the answers online will tell you "Oh, just go remove the finalizers and it will be deleted. No problem." But this is potentially dangerous! 

## So what is the culprit?

Did you just try to nuke your entire cluster?
Or an entire namespace?

<b>Ask yourself: "Is my volume provisioner still running?"</b><br/>
(Longhorn, Rook-Ceph, OpenEBS, etc.)

A majority of the time when I run into this problem, it is when I am doing a bulk delete operation like: "Delete all namespaces" or try to nuke the entire cluster all at once.
Whatever the situation, this issue usually arises due to a bulk delete where the volume-provisioner was included in the delete operation.

Imagine this scenario:

You've deployed a pod with an attached volume.
Before the pod can be taken offline, the volume provisioner needs to close the container's access to the PV, ensure that any nessecary replications have been performend, verify that no writes are happening, and finally un-mount the volume from the container and host filesystem.
But in order to do that, the volume provisioner (Longhorn, Ceph, openEBS, etc...) needs to be running!

When doing a bulk-delete, Kubernetes has no ordering concerns.
So it's very likely that your volume provisioner pods are now deleted and you have no CSI provisioner in your cluster that is able to manage access to the volume to be able to appropraitely clean it up. This causes your pod to get stuck Terminating, as there is no volume provisioner around to be able to clean up the dangling persistent volume.

### DO NOT FORCIBLY DELETE FINALIZERS.
Unlike every article or stackoverflow answer online suggests doing... 

This will cause undesired data loss and is potentially dangerous!
To get your resource to delete, it's seems counter intuitive, but that answer is quite simple:

# Re-deploy your volume provisioner

This will allow your volume provisioner to come back online, recognize that there are volume resources that want to be terminated, and it will clean them up and close access to their PVs. Then, only after everything else has been deleted, you can delete the volume-provisioner (if you want it to be gone).

As a rule of thumb:

1. Do not manually delete finalizers
2. When doing bulk deletes, do not delete your volume provisioner
3. Ensure your volume provisioner is the last thing in the cluster