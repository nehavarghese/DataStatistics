# DataStatistics


# A Java spring boot application having 2 services for data processing

## About this project

### Overview

1. The Java web application:

   - has 2 services, tested using swagger using URL: http://localhost:8081/data/swagger-ui/index.html#
   
   1. http://localhost:8081/data/action/event
   2. http://localhost:8081/data/action/stats" 
   
   - A scheduled task is running every 1 minute to refresh the data , keeping only valid data which is either in the past 60 seconds or in the future.
   - POST API, helps in feeding data to the system: It accepts list of strings of the format :1710030681000,0.0015917313,2138210833
   - POST API creates a file out.txt which gets updated every 60 seconds 
   - GET API, fetches data from this out.txt by filtering out the data that is only having the timestamp falling under past 60 seconds.
   - Application is tested in local environment in swagger, by starting the same through IntelliJ IDE
