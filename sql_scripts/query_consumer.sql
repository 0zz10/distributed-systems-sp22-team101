SELECT * FROM Consumer.LiftRides;

SELECT COUNT(recordId)
FROM Consumer.LiftRides;

SELECT skierId, COUNT(recordId), AVG(time), AVG(waitTime)
FROM Consumer.LiftRides
GROUP BY skierId
ORDER BY COUNT(recordId) DESC;