-- Minimalno 20 aerodroma za preuzimanje 
SELECT COUNT(DISTINCT ident) AS aerodromi_za_preuzimanje FROM AERODROMI_PRACENI;

-- Minimalno 100.000 preuzetih polazaka s aerodroma
SELECT COUNT(*) AS polasci_aerodroma FROM AERODROMI_POLASCI;

-- Minimalno 100.000 preuzetih dolazaka na aerodrome
SELECT COUNT(*) AS dolasci_aerodroma FROM AERODROMI_DOLASCI;

-- Minimalno 15 dana u cijelosti za koje su preuzeti podaci polazaka i dolazaka
SELECT
  COUNT(datum) AS ukupan_broj_dana
FROM
  (
   SELECT DISTINCT FROM_UNIXTIME(firstseen, '%d-%m-%Y') AS datum FROM AERODROMI_POLASCI ap 
   UNION
   SELECT DISTINCT FROM_UNIXTIME(firstseen, '%d-%m-%Y') AS datum FROM AERODROMI_DOLASCI ad
  )
AS combined;

-- Broj preuzetih podataka po danima za sve aerodrome
-- SELECT DISTINCT FROM_UNIXTIME(firstseen, '%d-%m-%Y') AS datum, COUNT(*) from AERODROMI_DOLASCI GROUP BY datum order by STR_TO_DATE(datum, '%d-%m-%Y');
-- SELECT DISTINCT FROM_UNIXTIME(firstseen, '%d-%m-%Y') AS datum, COUNT(*) from AERODROMI_POLASCI GROUP BY datum order by STR_TO_DATE(datum, '%d-%m-%Y');
SELECT
  datum,
  SUM(ukupan_broj_podataka) AS ukupan_broj_podataka
FROM
  (
   SELECT DISTINCT FROM_UNIXTIME(firstseen, '%d-%m-%Y') AS datum, COUNT(*) AS ukupan_broj_podataka FROM AERODROMI_POLASCI GROUP BY datum
   UNION ALL
   SELECT DISTINCT FROM_UNIXTIME(firstseen, '%d-%m-%Y') AS datum, COUNT(*) AS ukupan_broj_podataka FROM AERODROMI_DOLASCI GROUP BY datum
  )
  AS combined
GROUP BY
  datum
ORDER BY STR_TO_DATE(datum, '%d-%m-%Y');

-- Broj preuzetih podataka po danima za sve aerodrome pojedinačno
/*
SELECT   DISTINCT estarrivalairport as icao, FROM_UNIXTIME(firstseen, '%d-%m-%Y') as datum, COUNT(*) AS broj_dolazaka
FROM     AERODROMI_DOLASCI
GROUP BY icao, datum
ORDER BY icao, STR_TO_DATE(datum, '%d-%m-%Y');

SELECT   DISTINCT estdepartureairport as icao, FROM_UNIXTIME(firstseen, '%d-%m-%Y') as datum, COUNT(*) AS broj_polazaka
FROM     AERODROMI_POLASCI
GROUP BY icao, datum
ORDER BY icao, STR_TO_DATE(datum, '%d-%m-%Y');
*/
SELECT
  icao, datum,
  SUM(broj_podataka) AS ukupan_broj_preuzetih_podataka
FROM
  (
   SELECT DISTINCT estdepartureairport as icao, FROM_UNIXTIME(firstseen, '%d-%m-%Y') as datum, COUNT(*) AS broj_podataka FROM AERODROMI_POLASCI GROUP BY icao, datum
   UNION ALL
   SELECT DISTINCT estarrivalairport as icao, FROM_UNIXTIME(firstseen, '%d-%m-%Y') as datum, COUNT(*) AS broj_podataka FROM AERODROMI_DOLASCI GROUP BY icao, datum
  )
  AS combined
GROUP BY
  icao, datum
ORDER BY icao, STR_TO_DATE(datum, '%d-%m-%Y');

-- Broj preuzetih podataka po danima za odabrani aerodrom
/*
SELECT   DISTINCT estarrivalairport as icao, FROM_UNIXTIME(firstseen, '%d-%m-%Y') as datum, COUNT(*) AS broj_dolazaka
FROM     AERODROMI_DOLASCI
WHERE
    estarrivalairport = 'EBBR'
GROUP BY icao, datum
ORDER BY icao, STR_TO_DATE(datum, '%d-%m-%Y');

SELECT   DISTINCT estdepartureairport as icao, FROM_UNIXTIME(firstseen, '%d-%m-%Y') as datum, COUNT(*) AS broj_polazaka
FROM     AERODROMI_POLASCI
WHERE
    estdepartureairport = 'EBBR'
GROUP BY icao, datum
ORDER BY icao, STR_TO_DATE(datum, '%d-%m-%Y');
*/
SELECT
  icao, datum,
  SUM(broj_podataka) AS ukupan_broj_preuzetih_podataka
FROM
  (
   SELECT DISTINCT estdepartureairport as icao, FROM_UNIXTIME(firstseen, '%d-%m-%Y') as datum, COUNT(*) AS broj_podataka FROM AERODROMI_POLASCI GROUP BY icao, datum
   UNION ALL
   SELECT DISTINCT estarrivalairport as icao, FROM_UNIXTIME(firstseen, '%d-%m-%Y') as datum, COUNT(*) AS broj_podataka FROM AERODROMI_DOLASCI GROUP BY icao, datum
  )
  AS combined
WHERE
    icao = 'EBBR'
GROUP BY
  icao, datum
ORDER BY icao, STR_TO_DATE(datum, '%d-%m-%Y');
