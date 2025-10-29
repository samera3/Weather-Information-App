

Weather Information App

Overview

The Weather Information App is a JavaFX application designed to provide real-time weather information for any city. The app uses the OpenWeatherMap API to fetch live weather data, including temperature, humidity, wind speed, and weather conditions. The application also offers a short-term forecast, dynamic weather icons, background color changes based on weather, and a search history feature.

Features
	•	Real-time Weather Data: Displays current temperature, humidity, wind speed, and weather conditions for the selected city.
	•	Unit Conversion: Switch between Celsius and Fahrenheit.
	•	Weather History: Maintains a list of previous searches with timestamps.
	•	Dynamic Weather Icons: Icons represent current weather conditions (e.g., sunny, cloudy, rainy, snowy).
	•	Forecast Display: Shows a short-term forecast for the next five time periods.
	•	Dynamic Backgrounds: Changes background colors depending on weather conditions.
	•	Error Handling: Displays alerts when a city is invalid or the API request fails.

Getting Started

Prerequisites
	•	Java Development Kit (JDK) 8 or higher.
	•	JavaFX library.
	•	Internet connection to access the OpenWeatherMap API.

Installation and Running the Application
	1.	Clone the repository or download the project files.
	2.	Place the images folder in the project directory for weather icons.
	3.	Insert your OpenWeatherMap API key in the WeatherApp.java file.
	4.	Compile and run the application:

javac WeatherApp.java
java WeatherApp

Usage
	1.	Launch the application.
	2.	Enter a city name in the text field.
	3.	Click Get Weather to fetch current weather data.
	4.	Click Toggle °C/°F to switch temperature units.
	5.	View the short-term forecast in the expandable section.
	6.	Click on search history items to quickly view previous searches.
	7.	Observe dynamic background changes based on weather conditions.

Implementation Details
	•	WeatherApp.java: Main application file; handles GUI, API calls, weather display, icons, backgrounds, and history.
	•	OpenWeatherMap API: Provides live weather data in JSON format.
	•	JavaFX: GUI framework for creating the application interface.
	•	Images Folder: Contains icons representing different weather conditions.

Notes
	•	Ensure a valid OpenWeatherMap API key is provided.
	•	Internet connection is required to fetch weather data.
	•	Images for weather icons must match the names used in the code (e.g., sunny.png, partly_sunny.png, rain.png, snow.png).

License

This project can be shared or adapted for educational purposes.

Acknowledgements
	•	OpenWeatherMap API for weather data.
	•	JavaFX for building the graphical user interface.

Code Structure




WeatherApp.java
images/
├── sunny.png
├── partly_sunny.png
├── partly_cloudy.png
├── rain.png
└── snow.pngow.png
