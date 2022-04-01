-- get ski day vertical for a skier
-- GET/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
-- a skier can accumulate (liftID*10) vertical
SELECT 
    SUM(liftId * 10) AS dayVertical
FROM
    Consumer.LiftRides
WHERE
    resortId = 1 AND seasonId = 2019
        AND dayId = 3
        AND skierId = 1;


-- get the total vertical for the skier the specified resort. 
-- If no season is specified, return all seasons
-- GET/skiers/{skierID}/vertical
SELECT 
    seasonId, SUM(liftId * 10) AS totalVert
FROM
    Consumer.LiftRides
WHERE
    skierId = 3
GROUP BY seasonId
ORDER BY seasonId ASC;
