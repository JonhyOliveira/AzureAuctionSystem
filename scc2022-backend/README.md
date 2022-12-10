# Deploying 

Make sure your `pwd` is the `scc2022-backend` directory.

Depending on the enviroment you are deploying into, you should change the implementation of each interface.

### Azure

| Interface    |   Implementation |
|:-------------|-----------------:|
| StorageLayer | BlobStorageLayer |
| DBLayer      |    CosmosDBLayer |

`mvn clean compile package azure-webapp:deploy`

### Kubernetes

| Interface    |         Implementation |
|:-------------|-----------------------:|
| StorageLayer | FileSystemStorageLayer |
| DBLayer      |           MongoDBLayer |

1. `mvn clean compile package`:
Creates a war (Web-app ARchive) that can be deployed in a tomcat server.

2. `docker build .`:
Add options to docker build command as needed.
The produced image can be run and the web-app will be running at `localhost:8080`

---   
**last change: 09/12/2022**