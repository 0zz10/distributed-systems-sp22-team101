package com.bsds.group101.server;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ResortsServlet", value = "/ResortsServlet")
public class ResortsServlet extends HttpServlet {
  private static final int ServerWaitTime = 1000; // milliseconds

  // TODO try dummy for Lab4
  String dummyResortsList =
      "{"
          + "\"resorts\": ["
          + "{"
          + "\"resortName\": \"stringA\","
          + "\"resortID\": \"0\""
          + "}, {"
          + "\"resortName\": \"stringB\","
          + "\"resortID\": \"3\""
          + "}]}";

  String dummySeasonsList = "{" + "\"seasons\": [" + "\"2008\"," + "\"2022\"" + "]}";

  /**
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // sleep for 1000ms. You can vary this value for different tests
    try {
      Thread.sleep(ServerWaitTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // return response in json
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // urlPath is the string after url pattern described in web.xml
    String urlPath = request.getPathInfo();

    // get a list of ski resorts in the database
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write(dummyResortsList);
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
      if (urlParts[urlParts.length - 1].equals("skiers")) {
        // return 200 success message
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{ \"time\":\"Mission Ridge\",  \"numSkiers\":\"78999\"}");
      } else if (urlParts[urlParts.length - 1].equals("seasons")) {
        // return 200 success message
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(dummySeasonsList);
      }
    }
  }

  /**
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // sleep for 1000ms. You can vary this value for different tests
    try {
      Thread.sleep(ServerWaitTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // return response in json
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // urlPath is the string after url pattern described in web.xml
    String urlPath = request.getPathInfo();

    // empty url after /resorts path
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("{ \"message\":\"Resort not found\"}");
      return;
    }

    // check full url at UrlValidator
    String reqUrl = request.getRequestURL().toString();
    // check path match in custom api rules
    String[] urlParts = urlPath.split("/");

    // case urlParts = [, {resortID}, seasons, {seasonID}, days, {dayID}, skiers]
    if (!isUrlValid(urlParts, reqUrl)) {
      // if not valid url, return 400 error - Invalid inputs
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("{ \"message\":\"Invalid inputs supplied\"}");

    } else {
      // new season created - 201 success message
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.getWriter().write("new season created");
    }
  }

  /**
   * Check if path pattern matches at
   * /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
   *
   * @param urlParts
   * @param reqUrl
   * @return
   */
  private boolean isUrlValid(String[] urlParts, String reqUrl) {
    // url validator help module
    UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);

    Map<String, String> resortPathMap = new HashMap<>();

    System.out.println(Arrays.toString(urlParts));
    System.out.println(urlParts.length);

    // Check if the url is valid through UrlValidator
    if (urlValidator.isValid(reqUrl)) {
      // case urlParts = [, {resortID}, seasons, {seasonID}, days, {dayID}, skiers]
      if (urlParts.length == 7) {
        String resortID = urlParts[1];
        System.out.println(resortID);
        resortPathMap.put("resortID", resortID);

        for (int i = 2; i < urlParts.length; i++) {
          switch (urlParts[i]) {
            case "seasons":
              String seasonID = urlParts[i + 1];
              resortPathMap.put("seasonID", seasonID);
              System.out.println(seasonID);
              break;
            case "days":
              String dayID = urlParts[i + 1];
              resortPathMap.put("dayID", dayID);
              System.out.println(dayID);
              break;
            default:
              continue;
          }
        }
        // last check if all info are parsed into hashmap
        return resortPathMap.size() == 3;
      }
      // case urlParts = [, {resortID}, seasons]
      if (urlParts.length == 3 && urlParts[2].equals("seasons")) {
        String resortID = urlParts[1];
        System.out.println(resortID);
        resortPathMap.put("resortID", resortID);
        System.out.println(resortPathMap.toString());
        return resortPathMap.size() == 1;
      }
    }
    return false;
  }
}
