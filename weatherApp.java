

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * WeatherApp
 *
 * JavaFX application to display real-time weather information using OpenWeatherMap API.
 * Features:
 * - Search weather by city
 * - Display temperature, humidity, wind speed, and condition
 * - Dynamic icons for weather
 * - Dynamic background colors
 * - Short-term forecast (next 5 periods)
 * - Unit conversion (°C / °F)
 * - Search history with timestamps
 */
public class WeatherApp extends Application {

    // Replace with your OpenWeatherMap API key
    private static final String API_KEY = "dbd440968c9bbb7ea9fb63867b9c4353";

    private Label weatherLabel;       // Shows temperature, humidity, wind speed
    private Label conditionLabel;     // Shows weather condition description
    private boolean isCelsius = true; // Flag to track current unit
    private List<String> searchHistory = new ArrayList<>(); // Stores recent searches
    private VBox historyBox;          // GUI element to show search history
    private VBox forecastBox;         // GUI element to show forecast
    private BorderPane root;          // Main layout container
    private ImageView weatherIcon = new ImageView(); // Shows weather icon

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather Information App");

        // --- UI components ---
        Label cityLabel = new Label("Enter City:");
        TextField cityInput = new TextField();
        Button getWeatherButton = new Button("Get Weather");
        Button toggleUnitButton = new Button("Toggle °C/°F");

        weatherLabel = new Label();
        conditionLabel = new Label();

        historyBox = new VBox(5);
        forecastBox = new VBox(5);

        TitledPane historyPane = new TitledPane("Search History", historyBox);
        TitledPane forecastPane = new TitledPane("Forecast", forecastBox);

        VBox centerBox = new VBox(10, cityLabel, cityInput, getWeatherButton, toggleUnitButton,
                weatherIcon, weatherLabel, conditionLabel, forecastPane, historyPane);
        centerBox.setPadding(new Insets(15));

        root = new BorderPane(centerBox);
        Scene scene = new Scene(root, 400, 550);

        // --- Button actions ---
        getWeatherButton.setOnAction(e -> {
            String city = cityInput.getText().trim();
            if (city.isEmpty()) {
                showAlert("Error", "Please enter a city name.");
            } else {
                fetchWeather(city);
            }
        });

        toggleUnitButton.setOnAction(e -> toggleUnits());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Fetches current weather data for the specified city using OpenWeatherMap API.
     * Updates labels, icon, background, history, and forecast.
     */
    private void fetchWeather(String city) {
        try {
            String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q="
                    + city + "&appid=" + API_KEY + "&units=metric";
            String response = getHttpResponse(weatherUrl);
            JSONObject json = new JSONObject(response);

            double temp = json.getJSONObject("main").getDouble("temp");
            int humidity = json.getJSONObject("main").getInt("humidity");
            double wind = json.getJSONObject("wind").getDouble("speed");
            String condition = json.getJSONArray("weather").getJSONObject(0).getString("description");

            String weatherInfo = String.format("Temperature: %.1f °C\nHumidity: %d%%\nWind: %.1f m/s",
                    temp, humidity, wind);

            weatherLabel.setText(weatherInfo);
            conditionLabel.setText("Condition: " + condition);

            updateWeatherIcon(condition);
            updateBackground(condition);

            addToHistory(city);
            showForecast(city);

        } catch (Exception e) {
            showAlert("Error", "Failed to fetch weather data. Please check the city name.");
        }
    }

    /**
     * Updates the weather icon according to the condition description.
     * Uses local images in the "images" folder.
     */
    private void updateWeatherIcon(String condition) {
        String iconPath = "";
        String cond = condition.toLowerCase();

        if (cond.contains("clear")) {
            iconPath = "images/sunny.png";
        } else if (cond.contains("few clouds") || cond.contains("partly sunny")) {
            iconPath = "images/partly_sunny.png";
        } else if (cond.contains("scattered clouds") || cond.contains("broken clouds") || cond.contains("overcast clouds")) {
            iconPath = "images/partly_cloudy.png";
        } else if (cond.contains("rain") || cond.contains("drizzle") || cond.contains("shower") || cond.contains("thunderstorm")) {
            iconPath = "images/rain.png";
        } else if (cond.contains("snow")) {
            iconPath = "images/snow.png";
        } else if (cond.contains("mist") || cond.contains("fog") || cond.contains("haze")) {
            iconPath = "images/partly_cloudy.png";
        } else {
            iconPath = "images/partly_cloudy.png";
        }

        if (!iconPath.isEmpty()) {
            try {
                File file = new File(iconPath);
                if (file.exists()) {
                    Image icon = new Image(file.toURI().toString());
                    weatherIcon.setImage(icon);
                    weatherIcon.setFitHeight(100);
                    weatherIcon.setFitWidth(100);
                    weatherIcon.setPreserveRatio(true);
                } else {
                    System.out.println("Image file not found: " + file.getAbsolutePath());
                    weatherIcon.setImage(null);
                }
            } catch (Exception e) {
                System.out.println("Error loading image: " + e.getMessage());
                weatherIcon.setImage(null);
            }
        }
    }

    /**
     * Updates the background color of the application based on the weather condition.
     */
    private void updateBackground(String condition) {
        String cond = condition.toLowerCase();
        Color bgColor;

        if (cond.contains("clear") || cond.contains("few clouds") || cond.contains("partly sunny")) {
            bgColor = Color.LIGHTSKYBLUE;
        } else if (cond.contains("scattered clouds") || cond.contains("broken clouds") || cond.contains("overcast clouds")) {
            bgColor = Color.LIGHTGRAY;
        } else if (cond.contains("rain") || cond.contains("drizzle") || cond.contains("shower") || cond.contains("thunderstorm")) {
            bgColor = Color.DARKGRAY;
        } else if (cond.contains("snow")) {
            bgColor = Color.WHITE;
        } else if (cond.contains("mist") || cond.contains("fog") || cond.contains("haze")) {
            bgColor = Color.LIGHTGRAY;
        } else {
            bgColor = Color.WHITE;
        }

        root.setBackground(new Background(new BackgroundFill(bgColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * Shows a short-term forecast for the next 5 periods.
     */
    private void showForecast(String city) {
        try {
            forecastBox.getChildren().clear();
            String forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?q="
                    + city + "&appid=" + API_KEY + "&units=metric";
            String response = getHttpResponse(forecastUrl);
            JSONObject json = new JSONObject(response);
            JSONArray list = json.getJSONArray("list");

            for (int i = 0; i < Math.min(5, list.length()); i++) {
                JSONObject obj = list.getJSONObject(i);
                double temp = obj.getJSONObject("main").getDouble("temp");
                String cond = obj.getJSONArray("weather").getJSONObject(0).getString("description");
                String time = obj.getString("dt_txt");

                Label forecastLabel = new Label(String.format("• %.1f°C - %s - %s", temp, cond, time));
                forecastBox.getChildren().add(forecastLabel);
            }
        } catch (Exception e) {
            forecastBox.getChildren().add(new Label("Unable to fetch forecast."));
            System.out.println("Forecast error: " + e.getMessage());
        }
    }

    /**
     * Toggles temperature units between Celsius and Fahrenheit.
     */
    private void toggleUnits() {
        isCelsius = !isCelsius;
        String current = weatherLabel.getText();

        if (current.contains("Temperature")) {
            String[] lines = current.split("\n");
            try {
                double temp = Double.parseDouble(lines[0].split(" ")[1]);
                if (!isCelsius) {
                    temp = (temp * 9/5) + 32;
                    lines[0] = String.format("Temperature: %.1f °F", temp);
                } else {
                    temp = (temp - 32) * 5/9;
                    lines[0] = String.format("Temperature: %.1f °C", temp);
                }
                weatherLabel.setText(String.join("\n", lines));
            } catch (NumberFormatException e) {
                System.out.println("Error converting temperature: " + e.getMessage());
            }
        }
    }

    /**
     * Adds a city search to the history with timestamp.
     * Allows up to 10 recent searches.
     */
    private void addToHistory(String city) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String entry = city + " (" + timestamp + ")";

        if (!searchHistory.contains(entry)) {
            searchHistory.add(0, entry);
            if (searchHistory.size() > 10) searchHistory.remove(searchHistory.size() - 1);
        }

        historyBox.getChildren().clear();
        for (String h : searchHistory) {
            Hyperlink historyLink = new Hyperlink("• " + h);
            historyLink.setOnAction(e -> {
                String selectedCity = h.split(" ")[0];
                fetchWeather(selectedCity);
            });
            historyBox.getChildren().add(historyLink);
        }
    }

    /**
     * Helper method to get HTTP response from a URL.
     */
    private String getHttpResponse(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();
            return response.toString();
        } else {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }

    /**
     * Shows an alert dialog with the specified title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
