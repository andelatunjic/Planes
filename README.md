## Planes
*June, 2022.*

The purpose of this solution is to:
* enable the service for calculating the distance between airports (multithreaded socket server) | Application 1
* load configuration data and download data on departures and arrivals of flights from OpenSky Network and enter them into tables in the database | Application 2
* create a central authentication, authorization and monitoring service for other applications based on RESTful (JAX-RS) web services on the endpoint "api". The application uses the tables users, groups and roles | Application 3
* create a user interface realized with Jakarta MVC that deals with administrative tasks. Each form sends data using the POST method. It bases its work on sending commands to application 1 and sending requests to the RESTful web service from application 3 | Application 4
* provide JAX-WS (SOAP) services on endpoints "airports" and "meteo", and provide WebSocket endpoint "info". Requests are sent to the RESTful web service from Application 3 and the OpenWeatherMap service | Application 5

### Installation and program architecture of the system

Application | IDE | Java | Server | EE features | UI | Database | Working with the database | Purpuse
----------- | --- | ---- | ------ | ----------- | -- | -------- | ------------------------- | -------
1 | Eclipse with Maven | 17 | | | | | | Socket server
2 | Eclipse with Maven | 17 | Payara Web | Jakarta EE9.1 Web | | MySQL | JDBC, SQL | Downloads data on departures and arrivals of planes from selected airports
3 | Eclipse with Maven | 17 | Payara Web | Jakarta EE9.1 Web | | MySQL | JDBC, SQL | RESTful/JAX-RS web service
4 | Eclipse with Maven | 17 | Payara Web | Jakarta EE9.1 Web | Jakarta MVC | | | Views for administration
5 | Eclipse with Maven | 17 | Glassfish EE Server | Jakarta EE9.1 | | | | JAX-WS web service WebSocket endpoint
6 | Eclipse with Maven | 17 | Glassfish EE Server | Jakarta EE9.1 | JSF/PrimeFaces, Ajax | HSQLDB | JPA Criteria API | Views for working with users, JMS messages, trips and trip creation
