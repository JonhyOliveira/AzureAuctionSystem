# Auction System

This project was developed during the 1st semester of 2022/23 for the **Cloud Computing Systems** course @ FCT-UNL.

The development involved a first phase where the auction system was developed using Microsoft Azure's Cloud platform resources
and a second phase where that system was migrated to a Kubernetes environment.

**Note**: the system is ready for deployment in kubernetes as is.

## Goal ⭐️

Understand how services available in cloud computing platforms can be used for creating applications that are
scalable, fast, and highly available.

## Features 🎉️

* **Garbage Collection**: unaccessible data is regularly collected and disposed of.
* **Geo Replication**: data is replicated to other regions, delivering the lowest latency possible.

## Modules 🕸

Here you can find how the project is organized. Each of these modules has a `README.md` where you can find more information.

* **scc2022-backend**: REST-based application server implemented using Resteasy
* **scc2022-functions**: serverless code used to implement some functionalities
* **Kubernetes**: kubernetes scripts to deploy the system in a kubernetes environment.
* **Artillery**: test scripts to load test the system, can be dockerized and run remotely to test latency in different
  regions

---

## Authors


| Name                    | University prefix | Github Profile                                       |
| ----------------------- | :---------------: | :--------------------------------------------------: |
| Romão Costa             |     rlm.costa     | [@romaomcosta98](https://github.com/romaomcosta98)   |
| André Cordeiro          |    am.cordeiro    | [@Andre-Cordeiro](https://github.com/Andre-Cordeiro) |
| João Oliveira **(me)**  |   jfv.oliveira    | .                                                    |
