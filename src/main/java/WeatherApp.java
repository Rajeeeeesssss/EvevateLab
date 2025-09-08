import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp extends Application {
    private Label cityLabel = new Label("Enter a city and click search");
    private Label tempLabel = new Label();
    private Label descLabel = new Label();
    private ImageView weatherIcon = new ImageView();

    private static final String API_KEY = "031a1f42ae50352a0dc364458dba0be3"; // replace with your OpenWeatherMap key

    @Override
    public void start(Stage stage) {
        TextField cityField = new TextField();
        cityField.setPromptText("Enter city name");

        Button searchBtn = new Button("Search");

        searchBtn.setOnAction(e -> {
            String city = cityField.getText().trim();
            if (!city.isEmpty()) {
                fetchWeather(city);
            } else {
                cityLabel.setText("Please enter a city name.");
            }
        });

        HBox searchBox = new HBox(10, cityField, searchBtn);
        searchBox.setAlignment(Pos.CENTER);

        VBox weatherBox = new VBox(10, cityLabel, tempLabel, descLabel, weatherIcon);
        weatherBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, searchBox, weatherBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("Weather Forecast App");
        stage.setScene(scene);
        stage.show();
    }

    private void fetchWeather(String city) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                    "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream()))
                    .getAsJsonObject();

            String cityName = json.get("name").getAsString();
            double temp = json.getAsJsonObject("main").get("temp").getAsDouble();
            String desc = json.getAsJsonArray("weather").get(0).getAsJsonObject()
                    .get("description").getAsString();
            String icon = json.getAsJsonArray("weather").get(0).getAsJsonObject()
                    .get("icon").getAsString();

            cityLabel.setText("City: " + cityName);
            tempLabel.setText("Temperature: " + temp + "°C");
            descLabel.setText("Condition: " + desc);

            String iconUrl = "http://openweathermap.org/img/wn/" + icon + "@2x.png";
            weatherIcon.setImage(new Image(iconUrl));
        } catch (Exception ex) {
            cityLabel.setText("Error: Could not fetch weather.");
            tempLabel.setText("");
            descLabel.setText("");
            weatherIcon.setImage(null);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
