# Lab 8 - Milestone 2

Created: March 7, 2022 10:04 AM
Date: March 11, 2022
References: https://github.com/gortonator/bsds-6650/blob/master/assignments-2021/Assignment-3.md
Tags: AWS EC2, Collections, Java Multithreaded, Tomcat, threads

# Overview

1. Server and database implementations working and have a clear high level diagram
2. Database design description
3. Experimental Results - what are the metrics, are queue lengths short, etc. ?
4. Comparison of results with no mitigation strategy and results with mitigation strategy in place. Analysis of the difference.
5. Prepare another short team presentation (10minutes) for March 22!

# ****Adding a Resorts Microservice****

Let's add another microservice to your server!  Add a new consumer and database for storing data pertinent to the ski resort. The consumer should ingest the same data as the Skier Microservice. The data model should be designed to answer questions like:

- “How many unique skiers visited resort X on day N?”
- “How many rides on lift N happened on day N?”
- “On day N, show me how many lift rides took place in each hour of the ski day”

Again, test to see if you can write to the database as quickly as the data is published to RMQ.

Test this configuration **(without skier microservice)** by reporting the same results as Lab 6 for 128, 256 clients

![initial state](Lab%208%20-%20Mi%20d9499/Untitled.png)

initial state

![producer + consumer](Lab%208%20-%20Mi%20d9499/Untitled%201.png)

producer + consumer

![consumer continues](Lab%208%20-%20Mi%20d9499/Untitled%202.png)

consumer continues

# **Experiments**

First, run tests for 128 and 256 with **both** microservices and see what happens!

It’s now VERY likely you will see significant backlogs in your queues, servlet and maybe even consumers.

If you do, again, you have two choices:

1. Increase capacity - this means deploying more than free tier instances. Watch the $$s.
    
    ![change instance type to t2.medium](Lab%208%20-%20Mi%20d9499/Untitled%203.png)
    
    change instance type to t2.medium
    
2. Introduce throttling - you could do this e.g. in the client by introducing a throughput-based circuit breaker with exponential backoffs in client POSTs and/or RMQ posts/configuration

You don’t have to do both. But your aim is to try and deliver more stable throughput and eliminate client errors that may occur when no mitigation measures were used!

# Deliverables

> Submit your work to Canvas Assignment 3 as a pdf document. The document should contain:
> 
1. The URL for your git repo. Create a new folder for your Milestone 2 server code
2. A short description of your database designs and deployment topologies on AWS
3. Test runs (command windows, RMQ management windows showing queue size, send/receive rates) 128, 256 client threads the new microservice.
    
    ## 40runs_20000skiers_128threadsClient_100threadsConsumer
    
    ### Statistics at clients
    
    ```
    total requests: 484658
    mean response time: 502 ms
    duration:  1964431 ms
    Throughput: 246 requests/s
    ```
    
    ![Untitled](Lab%208%20-%20Mi%20d9499/Untitled%204.png)
    
    ![Untitled](Lab%208%20-%20Mi%20d9499/Untitled%205.png)
    
    ![Untitled](Lab%208%20-%20Mi%20d9499/Untitled%206.png)
    
    ## 40runs_20000skiers_256threadsClient_100threadsConsumer
    
    ### Statistics at clients
    
    ```
    total requests: 481531
    mean response time: 1496 ms
    duration:  5906761ms
    Throughput: 253 requests/s
    ```
    
    ![1minute range](Lab%208%20-%20Mi%20d9499/Untitled%207.png)
    
    1minute range
    
    ![10minute range](Lab%208%20-%20Mi%20d9499/Untitled%208.png)
    
    10minute range
    
    ![1minute range](Lab%208%20-%20Mi%20d9499/Untitled%209.png)
    
    1minute range
    
    ![10minute range](Lab%208%20-%20Mi%20d9499/Untitled%2010.png)
    
    10minute range
    
4. Test run for 128, 256 clients (command windows, RMQ management windows showing queue size, send/receive rates) with both microservices consuming and writing to the database.
5. A brief explanation of your mitigation strategy and results with 128. 256 clients to show their effects. Hopefully positive but negative is fine with good analysts!

# Troubleshoots

## Restart Tomcat on AWS EC2

[https://medium.com/@shrunk7byadagi/automatically-start-tomcat-on-instance-startup-reboot-in-amazon-ec2-ubuntu-instance-33849a9d9090](https://medium.com/@shrunk7byadagi/automatically-start-tomcat-on-instance-startup-reboot-in-amazon-ec2-ubuntu-instance-33849a9d9090)

### use `systemctl` command

Check status of Tomcat Service:

```bash
$ systemctl status tomcat
```

![Untitled](Lab%208%20-%20Mi%20d9499/Untitled%2011.png)

```bash
$ sudo systemctl start tomcat
```

## Manage the RabbitMQ Service

[https://www.rabbitmq.com/install-debian.html#managing-service](https://www.rabbitmq.com/install-debian.html#managing-service)

To start and stop the server, use the `systemctl` tool. The service name is `rabbitmq-server`:

```bash
# stop the local node
$ sudo systemctl stop rabbitmq-server

# start it back
$ sudo systemctl start rabbitmq-server
```

`systemctl status rabbitmq-server` will report service status as observed by systemd (or similar service manager):

```bash
# check on service status as observed by service manager
$ sudo systemctl status rabbitmq-server
```

# References:

## **Addendum: Multithreading and RabbitMQ**

RabbitMQ and multithreading needs a few considerations. Read on ....

The basic abstraction that needs to be operated on by each thread is the channel. This means:

In your servlet (or equivalent if not using a servlet):

1. In the init() method, initialize the connection (this is the socket, so is slow)
2. In the dopost(), create a channel and use that to publish to RabbitMQ. Close it at end of the request.

This should work fine, although the [documentation](https://www.rabbitmq.com/api-guide.html#concurrency) say channels are meant to be long-lived and caution again churn.

So a better solution would be to create a channel pool that shares a bunch of pre-created channels (in .init()) to form a connection pool.

Roll your own is not too hard, but apache commons has a [generic pool implementation](http://commons.apache.org/proper/commons-pool/examples.html) that you could build on. The reading has an example of how to do this.

Another approach to implementing a channel pool would be to use a BlockingQueue. Not too tricky ... give it a try!

On the consumer side, you probably want a multi-threaded consumer that just gets a message and writes to the hash map. In this case you can just create a channel per thread and all should be fine.

There's an excellent write up that describes the complexities of multi-threaded RMQ clients [here](http://moi.vonos.net/bigdata/rabbitmq-threading/)

And [here's](https://github.com/gortonator/bsds-6650/tree/master/code/week-6) some sample code you can work from.

## ****Part 1: RabbitMQ Best Practices****

[https://www.cloudamqp.com/blog/part1-rabbitmq-best-practice.html](https://www.cloudamqp.com/blog/part1-rabbitmq-best-practice.html)

## **CQRS pattern**

[https://docs.aws.amazon.com/prescriptive-guidance/latest/modernization-data-persistence/cqrs-pattern.html](https://docs.aws.amazon.com/prescriptive-guidance/latest/modernization-data-persistence/cqrs-pattern.html)