-- get ski day vertical for a skier
-- GET/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
-- a skier can accumulate (liftID*10) vertical
SELECT 
    SUM(liftId * 10) AS dayVertical
FROM
    Consumer.LiftRides
WHERE
    resortId = 56 AND seasonId = 56
        AND dayId = 56
        AND skierId = 100;


-- get the total vertical for the skier for specified seasons at the specified resort
-- GET/skiers/{skierID}/vertical
SELECT 
    seasonId, SUM(liftId * 10) AS totalVert
FROM
    Consumer.LiftRides
WHERE
    resortId = 56 AND dayId = 56
        AND skierId = 100
GROUP BY seasonId
ORDER BY seasonId ASC;
