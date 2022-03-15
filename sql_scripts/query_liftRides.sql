SELECT * FROM LiftRides.LiftRides;

SELECT COUNT(recordId)
FROM LiftRides.LiftRides;

SELECT skierId, COUNT(recordId), AVG(time), AVG(waitTime)
FROM LiftRides.LiftRides
GROUP BY skierId
ORDER BY COUNT(recordId) DESC;