# iRacing TeamTactics server
## Abstract
When doing team racing in iRacing it comes - beside the driving capabilities -
to various tactical considerations. These concern the amount of fuel left, 
how many laps to go in the stint, how much fuel to take on the last stint and 
so on. Planning aspects for longer team events are important too, each team driver
has his own average lap time, an individual fuel consumption per lap and will 
perform differently on different time of day. 
There are different tools available to help you make decisions on these considerations.    

This server provides the following team planning capabilities:

* Put together teams based on drivers iRacing-Ids
* Provide a stint plan based on individual driver performance
* Assign drivers to stints based on individual ability
* Full timezone support

During a race session this server aggregates the telemetry data provided by 
the current driver running [TeamTactics client](https://github.com/simracingtools/teamtactics) software.
So the server is able to always aggregate the most accurate telemetry data
provided by the driver currently in the car.

Live data and decision help provided by the server:

* Accurate fuel consumption based on the fuel level difference
* Stint average fuel consumption
* Stint average lap time
* Individual drivers best lap
* Adhoc fuel calculator using fuel level and per lap consumption as input
* Accurate measurement of pitstop, service and repair times
* Live recalculation of stint planning based on the current race situation

## Developers

The server is based on SpringBoot, Thymeleaf templating engine and Stomp WebSockets.
It is deploys on Google AppEngine (only works on flexible environment!) 
but will run standalone as well.

### Build

    mvn clean package

### Deploy on Google AppEngine

    mvn appengine:deploy -Dapp.deploy.version=...

### Run standalone

    mvn spring-boot:run