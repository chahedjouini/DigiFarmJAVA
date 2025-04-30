package esprit.tn.demo.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.json.JSONObject;

public class MaintenancePredictionClient {
    private static final String API_URL = "http://127.0.0.1:5000/predict";
    private final HttpClient httpClient;

    public MaintenancePredictionClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String predictMaintenance(double cost, double temperature, double humidity,
                                     double fuelConsumption, double energyConsumption) throws Exception {

        JSONObject data = new JSONObject();
        data.put("cout", cost);
        data.put("temperature", temperature);
        data.put("humidite", humidity);
        data.put("consoCarburant", fuelConsumption);
        data.put("consoEnergie", energyConsumption);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        JSONObject jsonResponse = new JSONObject(response.body());
        return jsonResponse.getString("prediction");
    }
}