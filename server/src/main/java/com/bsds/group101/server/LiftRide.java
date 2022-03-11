package com.bsds.group101.server;

public class LiftRide {
  private int skierId;
  private int resortId;
  private int seasonId;
  private int dayId;
  private int time;
  private int waitTime;
  private int liftId;

  public LiftRide(
      int skierId, int resortId, int seasonId, int dayId, int time, int waitTime, int liftId) {
    this.skierId = skierId;
    this.resortId = resortId;
    this.seasonId = seasonId;
    this.dayId = dayId;
    this.time = time;
    this.waitTime = waitTime;
    this.liftId = liftId;
  }

  public int getSkierId() {
    return skierId;
  }

  public void setSkierId(int skierId) {
    this.skierId = skierId;
  }

  public int getResortId() {
    return resortId;
  }

  public void setResortId(int resortId) {
    this.resortId = resortId;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public void setSeasonId(int seasonId) {
    this.seasonId = seasonId;
  }

  public int getDayId() {
    return dayId;
  }

  public void setDayId(int dayId) {
    this.dayId = dayId;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public int getWaitTime() {
    return waitTime;
  }

  public void setWaitTime(int waitTime) {
    this.waitTime = waitTime;
  }

  public int getLiftId() {
    return liftId;
  }

  public void setLiftId(int liftId) {
    this.liftId = liftId;
  }

  @Override
  public String toString() {
    return "{" +
            "'skierId':" + skierId +
            ", 'resortId':" + resortId +
            ", 'seasonId':" + seasonId +
            ", 'dayId':" + dayId +
            ", 'time':" + time +
            ", 'waitTime':" + waitTime +
            ", 'liftId':" + liftId +
            '}';
  }
}
