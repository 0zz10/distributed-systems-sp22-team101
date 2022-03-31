package com.bsds.group101.dal;

import com.bsds.group101.model.LiftRide;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LiftRideDao {
  private static HikariDataSource hikariDataSource;

  public LiftRideDao() {
    hikariDataSource = HikariCPDataSource.getDataSource();
  }

  // Make sure database and table exists before you call this data insertion
  public void createLiftRide(LiftRide newLiftRide) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement =
        "INSERT INTO LiftRides (skierId, resortId, seasonId, dayId, time, waitTime, liftID) "
            + "VALUES (?,?,?,?,?,?,?)";
    try {
      conn = hikariDataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, newLiftRide.getSkierId());
      preparedStatement.setInt(2, newLiftRide.getResortId());
      preparedStatement.setInt(3, newLiftRide.getSeasonId());
      preparedStatement.setInt(4, newLiftRide.getDayId());
      preparedStatement.setInt(5, newLiftRide.getTime());
      preparedStatement.setInt(6, newLiftRide.getWaitTime());
      preparedStatement.setInt(7, newLiftRide.getLiftID());

      // execute insert SQL statement
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }
}
