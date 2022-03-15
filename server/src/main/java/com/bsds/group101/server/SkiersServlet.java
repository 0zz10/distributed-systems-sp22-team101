package com.bsds.group101.server;

import com.google.gson.Gson;

import com.bsds.group101.model.LiftRide;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SkiersServlet", value = "/SkiersServlet")
public class SkiersServlet extends HttpServlet {
  private static final int ServerWaitTime = 1000; // milliseconds
  // create a hello queue to which the message will be delivered
  private static final String QUEUE_NAME = "liftRides";
  // RabbitMQ EC2 Instance Credentials
  private static final String RABBITMQ_HOST = "35.168.93.165";
  private static final String RABBITMQ_USERNAME = "test";
  private static final String RABBITMQ_PASSWORD = "test";

  // Parse path variables into a Hashmap
  Map<String, String> pathMap = new ConcurrentHashMap<>();

  /**
   * Get ski day vertical for a skier
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // test System.getProperty
    // System.out.println(System.getProperty("RABBITMQ_USERNAME"));

    // return response in json
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // urlPath is the string after url pattern described in web.xml
    String urlPath = request.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("{ \"message\":\"missing parameters\"}");
      return;
    }

    // check full url at UrlValidator
    String reqUrl = request.getRequestURL().toString();
    // check path match in custom api rules
    String[] urlParts = urlPath.split("/");

    if (!isUrlValid(urlParts, reqUrl)) {
      // if not valid url, return 400 error - Invalid inputs
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      //      response.getWriter().write("{ \"message\":\"Invalid inputs supplied\"}");
      response
          .getWriter()
          .write("{ \"message\":\"" + isUrlValid(urlParts, reqUrl) + reqUrl + "\"}");

    } else {
      // return 200 success message
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().write("34507");

      //      // store url path variables  to a json format.
      //      PrintWriter out = response.getWriter();
      //      String pathJsonString = new Gson().toJson(pathMap);
      //      out.print(pathJsonString);
      //      out.flush();
    }
  }

  /**
   * Write a new lift ride for the skier
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // return response in json
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // urlPath is the string after url pattern described in web.xml
    String urlPath = request.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("{ \"message\":\"missing parameters\"}");
      return;
    }

    // check full url at UrlValidator
    String reqUrl = request.getRequestURL().toString();
    // check path match in custom api rules
    String[] urlParts = urlPath.split("/");

    if (!isUrlValid(urlParts, reqUrl)) {
      // if not valid url, return 400 error - Invalid inputs
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("{ \"message\":\"Invalid inputs supplied\"}");

    } else {
      // new season created - 201 success message
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.getWriter().write("Write successful");

      // join request body as a string.
      String requestJsonString = request.getReader().lines().collect(Collectors.joining());

      // use GSON to parse request jsonString and construct LiftRide object
      Gson gson = new Gson();
      LiftRide liftRide = gson.fromJson(requestJsonString, LiftRide.class);

      // set variables
      liftRide.setResortId(Integer.parseInt(pathMap.get("resortID")));
      liftRide.setSeasonId(Integer.parseInt(pathMap.get("seasonID")));
      liftRide.setDayId(Integer.parseInt(pathMap.get("dayID")));
      liftRide.setSkierId(Integer.parseInt(pathMap.get("skierID")));

      // Producer Process
      String message = liftRide.toString();
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost(RABBITMQ_HOST);
      factory.setUsername(RABBITMQ_USERNAME);
      factory.setPassword(RABBITMQ_PASSWORD);
      try (Connection connection = factory.newConnection();
          Channel channel = connection.createChannel()) {
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Parse path variables into a ConcurrentHashMap, threadsafe e.g.
   * /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}/skiers/{skierID}/vertical
   * {resortID:*, seasonID:*, dayID:*, skierID:*}
   *
   * @param urlParts
   */
  private void urlToMapHelper(String[] urlParts) {
    System.out.println(Arrays.toString(urlParts));
    System.out.println(urlParts.length);

    // case urlParts = [, {resortID}, seasons, {seasonID}, days, {dayID}, skiers, {skierID}]
    String resortID = urlParts[1];
    //    System.out.println(resortID);
    pathMap.put("resortID", resortID);

    for (int i = 2; i < urlParts.length; i++) {
      switch (urlParts[i]) {
        case "seasons":
          String seasonID = urlParts[i + 1];
          pathMap.put("seasonID", seasonID);
          //          System.out.println(seasonID);
          break;
        case "days":
          String dayID = urlParts[i + 1];
          pathMap.put("dayID", dayID);
          //          System.out.println(dayID);
          break;
        case "skiers":
          String skierID = urlParts[i + 1];
          pathMap.put("skierID", skierID);
          //          System.out.println(skierID);
          break;
        default:
          continue;
      }
    }
  }
  /**
   * Check if path pattern matches at
   * /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID} /skiers/{skierID}/vertical
   *
   * @param urlParts
   * @param reqUrl
   * @return
   */
  private boolean isUrlValid(String[] urlParts, String reqUrl) {
    // reqUrl  =
    // http://localhost:8080/server_war_exploded/skiers/3/seasons/2019/days/3/skiers/33 --pass
    // http://localhost:8080/server_war_exploded/skiers/3/vertical --pass
    UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);

    // Check if the url is valid through UrlValidator
    if (urlValidator.isValid(reqUrl)) {
      // case urlParts = [, {resortID}, seasons, {seasonID}, days, {dayID}, skiers, {skierID}]
      if (urlParts.length == 8) {
        // parse path variables into a Hashmap
        urlToMapHelper(urlParts);

        // last check if all info are parsed into hashmap
        return pathMap.size() == 4;
      }
      // case urlParts = [, {skierID}, vertical]
      if (urlParts.length == 3 && urlParts[2].equals("vertical")) {
        // TODO get the total vertical for the skier for specified seasons at the specified resort
        return true;
      }
    }
    return false;
  }
}
