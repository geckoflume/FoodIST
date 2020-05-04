package pt.ulisboa.tecnico.cmov.foodist.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.model.Beacon;

public class ServerFetcher {
    private final String baseUrl = "https://data.florianmornet.fr/api";
    private final String cafeteriasEndpoint = "/cafeterias";
    private final String dishesEndpoint = "/dishes";
    private final String beaconsEndpoint = "/beacons";

    public String fetchCafeterias() {
        String urlString = baseUrl + cafeteriasEndpoint;
        return NetUtils.get(urlString, 200);
    }

    public String fetchCafeteria(int cafeteriaId) {
        String urlString = baseUrl + cafeteriasEndpoint + "/" + cafeteriaId;
        return NetUtils.get(urlString, 200);
    }

    public String insertDish(DishEntity dish) {
        String urlString = baseUrl + dishesEndpoint;

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(dish);
        return NetUtils.postJson(urlString, json, 201);
    }

    public String fetchDishes(int cafeteriaId) {
        String urlString = baseUrl + cafeteriasEndpoint + "/" + cafeteriaId + dishesEndpoint;
        return NetUtils.get(urlString, 200);
    }

    public String insertBeacon(Beacon beacon) {
        String urlString = baseUrl + beaconsEndpoint;

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(beacon);
        return NetUtils.postJson(urlString, json, 201);
    }

    public String updateBeacon(Beacon beacon) {
        String urlString = baseUrl + beaconsEndpoint + "/" + beacon.getId();

        String json = new Gson().toJson(beacon);
        return NetUtils.putJson(urlString, json, 200);
    }
}
