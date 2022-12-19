**:star: Quirk**: At [ExecuteFunction](https://github.com/JonhyOliveira/AzureAuctionSystem/blob/e1da200a82c86b8ad95111ebdfd526e49ac687d3/scc2022-functions/src/scc/functions/ExecuteFunction.java) we leverage azure's annotations used in the first part of this project to find timer triggered tasks (CronJobs) 
to be executed in k8s.

# Deploying 

Make sure your `pwd` is the `scc2022-functions` directory.

Depending on the enviroment you are deploying into, you should change the implementation of each interface.

### Azure

| Interface    |   Implementation |
|:-------------|-----------------:|
| StorageLayer | BlobStorageLayer |
| DBLayer      |    CosmosDBLayer |

`mvn clean compile package azure-functions:deploy`

### Kubernetes

| Interface    |         Implementation |
|:-------------|-----------------------:|
| StorageLayer | FileSystemStorageLayer |
| DBLayer      |           MongoDBLayer |

1. `mvn clean compile assembly:single`:
A jar file including all the necessary dependencies will be created.

2. `docker build .`:
Add options to docker build command as needed.
Creates a docker image as a program with the entry point at `scc.functions.ExecuteFunction`.
This docker image can be deployed in kubernetes by providing as an argument the name of the function to run, 
given by the `@FunctionName` annotation.

---   
**last change: 09/12/2022**
