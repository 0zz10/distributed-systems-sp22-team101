-- get a list of ski resorts in the database
-- GET/resorts
SELECT DISTINCT
    resortId
FROM
    Consumer.ResortSeasons
ORDER BY resortId ASC;

-- get number of unique skiers at resort/season/day
-- GET/resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers
SELECT 
    COUNT(DISTINCT skierId)
FROM
    Consumer.LiftRides
WHERE
    resortId = 56 AND seasonId = 56
        AND dayId = 56;
    
-- get a list of seasons for the specified resort
-- GET/resorts/{resortID}/seasons
SELECT 
    DISTINCT seasonId
FROM
    Consumer.ResortSeasons
WHERE
	resortId = 56
ORDER BY
	seasonId ASC;