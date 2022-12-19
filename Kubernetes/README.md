## Kubernetes Deployment Scripts

In this directory you can find kubernetes deployment scripts as well as `apply-all.sh`, a utility script which uses
`kubectl` to apply the scripts to a cluster.

## Features

* 🌐 **Services**
  * Redis
  * MongoDB
  * Web app: `scc2022-backend`
* 💪 **Persistency**
  * Database
  * Images
* ⏫ **Pod Auto-Scaling**
* ⏲ **CronJobs**: maintenance tasks, see `scc2022-functions`

## Local deployment

The script `docker-compose.yml` describes a deployment similar to that in K8S and can be used to test it locally. To use 
it you should build `scc2022-backend`'s docker image and change the script the built image.