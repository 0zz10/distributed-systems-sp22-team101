package consumer;

import com.google.gson.Gson;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import dal.LiftRideDao;
import model.LiftRide;

public class Recv {

  // create a hello queue to which the message will be delivered
  private static final String QUEUE_NAME = "liftRides";
  // RabbitMQ EC2 Instance Credentials
  private static final String RABBITMQ_HOST = "35.168.93.165";
  private static final String RABBITMQ_USERNAME = "test";
  private static final String RABBITMQ_PASSWORD = "test";

  public static void main(String[] argv) throws Exception {
    // Consumer Process
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(RABBITMQ_HOST);
    factory.setUsername(RABBITMQ_USERNAME);
    factory.setPassword(RABBITMQ_PASSWORD);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
//
//    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//
//    DeliverCallback deliverCallback =
//        (consumerTag, delivery) -> {
//          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//          System.out.println(" [x] Received '" + message + "'");
//        };
//    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    // max one message per receiver
    channel.basicQos(1);
    System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
      channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
      System.out.println("Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");

      // use GSON to parse request jsonString and construct model.LiftRide object
      // e.g. "{'skierId':241, 'resortId':56, 'seasonId':56, 'dayId':56, 'time':386, 'waitTime':59, 'liftID':17}"
      Gson gson = new Gson();
      LiftRide liftRide = gson.fromJson(message, LiftRide.class);

      System.out.println("****model.LiftRide Object consumed*****" + liftRide.toString());

      // pass that object to the DAO layer
      LiftRideDao liftRideDao = new LiftRideDao();

      // construct a model.LiftRide object with those values
      liftRideDao.createLiftRide(liftRide);
    };
    // process messages
    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
    });
  }

}
