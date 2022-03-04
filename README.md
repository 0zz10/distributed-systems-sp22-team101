# Lab 6 - Building the Server

Created: February 22, 2022 4:36 PM
Date: March 4, 2022
References: https://github.com/gortonator/bsds-6650/blob/master/assignments-2021/Assignment-2.md

https://github.com/gortonator/bsds-6650/blob/master/Week-6.md
Tags: AWS EC2, Application Load Balancer, Collections, Java Multithreaded, RabbitMQ, Tomcat

# Overview

**More Building of your Server!  (please continue to work in your groups!)**

This lab builds on milestone 1. Your client *won’t* change (unless it has a bug in it!). You *will* implement more of the processing logic in your server and post the result to a queue for subsequent processing.  Please make sure that you organize your code so that Milestone 1 is separate from this evolution of your server (maybe a branch in your repo?).  The reason for this is that you might want to show this evolution to a stakeholder one day!

# **Step 1 - Extend the Implementation of your Server**

Implement the *doPost()* method in your SkierServlet to:

1. fully validate the URL and JSON payload
2. if valid, format the incoming data and send it as a payload to queue
    
    ```java
    // Convert request body to string
    String requestJsonString = request.getReader().lines().collect(Collectors.joining());
    ```
    

Choose your own queue technology. RabbitMQ you've seen a little last week, but AWS SQS another. Make sure you deploy RabbitMQ on its own EC2 instance. You can find various installation instructions [here (Links to an external site.)](https://www.rabbitmq.com/download.html) and instructions for configuring access to RabbitMQ on your EC2 [here (Links to an external site.)](https://www.rabbitmq.com/access-control.html).

Your aim is to keep response times as low as possible. One free tier server will probably get pretty busy, so you will want to gather some metrics to show the bottleneck, then introduce load balancing and show the improvement.

## Set up [AWS Elastic Load Balancing](https://aws.amazon.com/elasticloadbalancing/features/?nc=sn&loc=2)

You can set up [AWS Elastic Load Balancing (Links to an external site.)](https://aws.amazon.com/elasticloadbalancing/features/?nc=sn&loc=2) using either *Application* or *Network* load balancers. Enable load balancing with e.g. 4 free tier EC2 instances and see what effect this has on your performance.

[https://www.youtube.com/watch?v=OGEZn50iUtE](https://www.youtube.com/watch?v=OGEZn50iUtE)

### ****Product comparisons****

[https://aws.amazon.com/elasticloadbalancing/features/#Product_comparisons](https://aws.amazon.com/elasticloadbalancing/features/#Product_comparisons)

![Untitled](Lab%206%20-%20Bu%2086641/Untitled.png)

### **Configuring an Application Load Balancer**

[https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/environments-cfg-alb.html](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/environments-cfg-alb.html)

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%201.png)

[AWS ALB - Getting Started ...](https://awskarthik82.medium.com/aws-alb-getting-started-f77e18b9cf0b)

### Application Load Balancer with Multiple Tomcat Instances

[Application Load Balancer with Multiple Tomcat Instances - CloudOps](http://www.cloudops.in/application-load-balancer-with-multiple-tomcat-instances/)

![**Step 3:- Add instances in this target Group**](Lab%206%20-%20Bu%2086641/Untitled%202.png)

**Step 3:- Add instances in this target Group**

# **Step 2 - Implement a Consumer**

Implement a plain old Java program to pull messages off the queue. This program simply receives messages from the queue and keeps a record of the individual lift rides for each skier in a hash map.

Your aim is to consume messages, ideally, as quickly as they are produced. This means your consumer will need to be multithreaded and your hash map thread safe.

# **Step 3 - Load Testing**

Your aim here is to find the ‘best’ application configuration in terms of responsiveness to the client and managing queue size.

The questions you need to explore are:

- Do I need to scale out with load balancing for my server? Or can my system work with 1 free-tier server?
- How many queue consumers threads do I need to keep the queue size as close to zero as possible?

You can use the RabbitMQ management console to track the number of messages in the queue, and producers and consumer rates.

## 10runs_1024skiers_64threads

```
total requests: 6116
duration: 68737 ms
Throughput: 88 requests/s
Deliver rate: 95 requests/s
```

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%203.png)

## 10runs_1024skiers_128threads

```
total requests: 6128
duration: 35502 ms
Throughput: 172 requests/s
Deliver rate: 194 requests/s
```

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%204.png)

## 30runs_1024skiers_128threads

```
total requests: 18536
duration: 105551 ms
Throughput: 175 requests/s
Deliver rate: 187 requests/s
```

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%205.png)

# Deliverables

> Submit your work to Canvas Lab 5 as a pdf document. The document should contain:
> 
1. The URL for your git repo. Create a new folder for your Lab 5 server code
2. A 1-2 page description of your server design, including a high level architecture diagram. Include major classes, packages, relationships, how messages get sent/received, etc
3. Test runs (command lines, RMQ management windows showing queue size, send/receive rates) for 64, 128, 256, and 512 client threads (with numSkiers=20000, and numLifts=40) along with a 2-3 page description on your results and analysis.

## 40runs_20000skiers_64threads

```
total requests: 480815
duration: 1878219 ms
Throughput:  255 requests/s
Deliver rate: 257 requests/s
```

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%206.png)

## 40runs_20000skiers_128threads

```
total requests: 484835
mean response time: 491 ms
duration:  1912401ms
Throughput:  253 requests/s
Deliver rate: 261 requests/s
```

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%207.png)

## 40runs_20000skiers_256threads

```
total requests: 
duration:  ms
Throughput:  requests/s
Deliver rate: 187 requests/s
```

## 40runs_20000skiers_512threads

```
total requests: 18536
duration:  ms
Throughput:  requests/s
Deliver rate: 187 requests/s
```

# Troubleshoots

## Restart Tomcat on AWS EC2

[https://medium.com/@shrunk7byadagi/automatically-start-tomcat-on-instance-startup-reboot-in-amazon-ec2-ubuntu-instance-33849a9d9090](https://medium.com/@shrunk7byadagi/automatically-start-tomcat-on-instance-startup-reboot-in-amazon-ec2-ubuntu-instance-33849a9d9090)

### use `systemctl` command

Check status of Tomcat Service:

```bash
$ systemctl status tomcat
```

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%208.png)

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

## **Sharing : Fix the IP on Rabbit MQ server**

[https://piazza.com/class/kyj9s1t0zig1i6?cid=81](https://piazza.com/class/kyj9s1t0zig1i6?cid=81)

For lab6, as our tomcat always point to another EC2 running on RabbitMQ, it would be better for us to fix the IP on RabbitMQ server. You may allocate a elastic IP address, then assoicate to your RabbitMQ instance. Then the Rabbit MQ server IP will not change anymore.

### 1) AWS Console --> EC2 --> Network & Security --> Elastic IPs --> Click "Allocate Elastic IP Address"

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%209.png)

### 2) Click "Allocate"

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%2010.png)

### 3) Assoicate it to Rabbit MQ Instance

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%2011.png)

### 4) Choose the Instance that assoicate to this IP

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%2012.png)

### 5) The EC2 instance will change to this newly create elastic IP.

![Untitled](Lab%206%20-%20Bu%2086641/Untitled%2013.png)

## **Amazon SQS**

[https://github.com/gofore/aws-training/blob/master/workshop/complete/aws-workshop-common/src/main/java/com/gofore/aws/workshop/common/sqs/SqsClient.java](https://github.com/gofore/aws-training/blob/master/workshop/complete/aws-workshop-common/src/main/java/com/gofore/aws/workshop/common/sqs/SqsClient.java)

```java
package com.gofore.aws.workshop.common.sqs;

import java.util.concurrent.CompletableFuture;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SqsClient {
    
    private final AmazonSQSAsync sqs;
    
    @Inject
    public SqsClient(AmazonSQSAsync sqs) {
        this.sqs = sqs;
    }

    /**
     * Gets the AWS interface.
     * 
     * @return the original interface
     */
    public AmazonSQSAsync getSqs() {
        return sqs;
    }

    /**
     * Completable future wrapper for AmazonSQSAsync#sendMessageAsync.
     * 
     * @param request the send message request
     * @return future of send message result
     * @see com.amazonaws.services.sqs.AmazonSQSAsync#sendMessageAsync(com.amazonaws.services.sqs.model.SendMessageRequest)
     */
    public CompletableFuture<SendMessageResult> sendMessage(SendMessageRequest request) {
        CompletableFuture<SendMessageResult> future = new CompletableFuture<>();
        sqs.sendMessageAsync(request, new AsyncHandler<SendMessageRequest, SendMessageResult>() {
            @Override
            public void onError(Exception exception) {
                future.completeExceptionally(exception);
            }

            @Override
            public void onSuccess(SendMessageRequest request, SendMessageResult sendMessageResult) {
                future.complete(sendMessageResult);
            }
        });
        return future;
    }

    /**
     * Completable future wrapper for AmazonSQSAsync#receiveMessageAsync.
     * 
     * @param request the receive message request
     * @return future of receive message result
     * @see com.amazonaws.services.sqs.AmazonSQSAsync#receiveMessageAsync(com.amazonaws.services.sqs.model.ReceiveMessageRequest)
     */
    public CompletableFuture<ReceiveMessageResult> receiveMessage(ReceiveMessageRequest request) {
        CompletableFuture<ReceiveMessageResult> future = new CompletableFuture<>();
        sqs.receiveMessageAsync(request, new AsyncHandler<ReceiveMessageRequest, ReceiveMessageResult>() {
            @Override
            public void onError(Exception exception) {
                future.completeExceptionally(exception);
            }

            @Override
            public void onSuccess(ReceiveMessageRequest request, ReceiveMessageResult receiveMessageResult) {
                future.complete(receiveMessageResult);
            }
        });
        return future;
    }

    /**
     * Completable future wrapper for AmazonSQSAsync#deleteMessageAsync.
     * 
     * @param request the delete message request
     * @return future of delete message result
     * @see com.amazonaws.services.sqs.AmazonSQSAsync#deleteMessageAsync(com.amazonaws.services.sqs.model.DeleteMessageRequest)
     */
    public CompletableFuture<Void> deleteMessage(DeleteMessageRequest request) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        sqs.deleteMessageAsync(request, new AsyncHandler<DeleteMessageRequest, Void>() {
            @Override
            public void onError(Exception exception) {
                future.completeExceptionally(exception);
            }

            @Override
            public void onSuccess(DeleteMessageRequest request, Void result) {
                future.complete(result);
            }
        });
        return future;
    }

    /**
     * Completable future wrapper for AmazonSQSAsync#getQueueAttributesAsync.
     * 
     * @param request the qeueue attributes request
     * @return future of queue attributues result
     * @see com.amazonaws.services.sqs.AmazonSQSAsync#getQueueAttributesAsync(com.amazonaws.services.sqs.model.GetQueueAttributesRequest)
     */
    public CompletableFuture<GetQueueAttributesResult> getQueueAttributes(GetQueueAttributesRequest request) {
        CompletableFuture<GetQueueAttributesResult> future = new CompletableFuture<>();
        sqs.getQueueAttributesAsync(request, new AsyncHandler<GetQueueAttributesRequest, GetQueueAttributesResult>() {
            @Override
            public void onError(Exception exception) {
                future.completeExceptionally(exception);
            }

            @Override
            public void onSuccess(GetQueueAttributesRequest request, GetQueueAttributesResult getQueueAttributesResult) {
                future.complete(getQueueAttributesResult);
            }
        });
        return future;
    }
}
```

### **Standard Queues**

**Unlimited Throughput**: Standard queues support a nearly unlimited number of transactions per second (TPS) per API action.

**At-Least-Once Delivery**: A message is delivered at least once, but occasionally more than one copy of a message is delivered.

**Best-Effort Ordering**: Occasionally, messages might be delivered in an order different from which they were sent.

### **FIFO Queues**

**High Throughput**: By default, FIFO queues support up to 300 messages per second (300 send, receive, or delete operations per second). When you batch 10 messages per operation (maximum), FIFO queues can support up to 3,000 messages per second. If you require higher throughput, you can enable high throughput mode for FIFO on the Amazon SQS console, which will support up to 30,000 messages per second with batching, or up to 3,000 messages per second without batching.

**Exactly-Once Processing**: A message is delivered once and remains available until a consumer processes and deletes it. Duplicates aren't introduced into the queue.

**First-In-First-Out Delivery**: The order in which messages are sent and received is strictly preserved (i.e. First-In-First-Out).

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

## ****13 Common RabbitMQ Mistakes and How to Avoid Them****

[https://www.cloudamqp.com/blog/part4-rabbitmq-13-common-errors.html](https://www.cloudamqp.com/blog/part4-rabbitmq-13-common-errors.html)

## **Amazon MQ**

[https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/welcome.html](https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/welcome.html)