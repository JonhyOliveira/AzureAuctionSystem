# Artillery Scripts 
These scripts were adapted from those given to us by the professors.

### Scripts to initialize the system

- `create-users.yml`: creates 100 users (if starting with a fresh database, delete file `users.data`)
- `create-auctions.yml`: creates 300 auctions with a variable number of bids and questions

### Script to test the system

`baseworkload.yml` is a workload with 2 scenarios: 
1. user that checks her auctions and responds to questions;
2. user searches for auctions and bids on some of them.

The script specifies a warm-up stage with a lower amount of request and a test stage where the request rate is higher.
