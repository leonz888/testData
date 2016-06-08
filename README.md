Welcome to the "Smart Weather" test data generation project
This project is a practice for simulating the weather changes in major cities around Australia. The code can be used for generating the test data for a weather forecast program.


Prerequisites:
1. Scala v2.11 installed
2. Sbt v0.13 installed


Steps to run the code:
1. Clone or Download this project
2. Unzip (if downloaded) the files
3. In a shell windows, go into the project directory, type "sbt run" and press Enter to launch the data generation application  
4. The "testData.csv" generated in the project directory is the result of this test


Config file:
"cityinfo.csv" is the configuration file for this test
Each line in that file represents the information of a weather station (I picked 10 major cities along the Australian coastal line for this demo)
For each weather station, 9 columns of information are pre-typed in this config file
1. Station: Station/city code in IATA format
2. Lat: Latitude of the station
3. Lon: Longitude of the station
4. Temperature_Upper: Temperature upper bound that will be generated for the following 7 days * 24 hours
5. Temperature_Lower: Temperature lower bound that will be generated for the following 7 days * 24 hours
6. Pressure_Upper: Atmospheric Pressure upper bound that will be generated for the following 7 days * 24 hours
7. Pressure_Lower: Atmospheric Pressure lower bound that will be generated for the following 7 days * 24 hours
8. Humidity_Upper: Humidity upper bound that will be generated for the following 7 days * 24 hours
9. Humidity_Lower: Humidity lower bound that will be generated for the following 7 days * 24 hours
Notes: 
This weather station information is summarized from major weather forecast websites and needs to be kept update-to-date in order to generate better simulation of the current weather.
This config file is maintained manually for the current stage.


Assumptions:
1. This test is just a simple simulation practice; The logic of the models may not be precise enough to simulate the actual weather change. 
2. The simulation data is designed to be generated randomly between the upper bound and the lower bound (e.g. the upper and lower bound of Temperature/Pressure/Humidity ) 
3. The upper and lower bounds in the config file are validated against the rules:
	a) Latitude should falls between -90 and +90
	b) Longitude should falls between -180 and +180
	c) The temperature within Australian territory should falls between -20°C and 60°C
	d) The pressure within Australian territory should falls between 980 hPa and 1050 hPa, which is suitable for living
	e) The humidity within Australian territory should falls between 0% and 100%
4. The "Temperature" data is a little bit well-deviced, which applies a "Sine" math function to the hours of a day:
	a) We assume 14:00(2pm) has the highest temperature within a day and 2:00(2am) has the lowest telmperature within a day
	b) The temperature climbs up to the highest temperature around 2pm and then drops down to the lowest around 2am, then climbs up again in the next 12 hours
	c) This cyclic phenomenon can be illustrated by a "Sine" function (e.g. "math.sin" in scala)
	d) Randomization is also applied so that the temperature fluctuation does not "strictly" following the "Sine" function pattern, which makes it more sensible
5. The "Weather Condition" data is created on top of several rules:
	a) If humidity is greater than 90% and the temperature is no greater than 0°C, then it will be "Snowy"
	b) If humidity is greater than 90% but the temperature is greater than 0°C, then it will be "Rainy" 
	c) If humidity is between 40% and 90%, then it will be "Cloudy"
	d) If humidity is less than 40% and the hour of the day is between 6pm and 6am (night time), then it will be "Fair"
	e) If humidity is less than 40% and the hour of the day is between 6am and 6pm (day time), then it will be "Sunny"

	
Files:
Here are two important files which are located in the root of the project directory
1. "cityinfo.csv": the config file for city information
2. "testData.csv": the output file of the test data generated


Have fun!!!