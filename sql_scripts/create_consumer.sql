CREATE DATABASE Consumer;

CREATE TABLE `Consumer`.`LiftRides` (
  `recordId` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`recordId`),
  `skierId` INT NOT NULL,
  `resortId` INT NULL,
  `seasonId` INT NULL,
  `dayId` INT NULL,
  `time` INT NULL,
  `waitTime` INT NULL,
  `liftID` INT NULL);