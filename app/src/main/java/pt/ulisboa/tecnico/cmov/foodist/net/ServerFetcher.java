package pt.ulisboa.tecnico.cmov.foodist.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import javax.net.ssl.HttpsURLConnection;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.model.Beacon;

public class ServerFetcher {
    private final String baseUrl = "https://data.florianmornet.fr/api";
    private final String cafeteriasEndpoint = "/cafeterias";
    private final String dishesEndpoint = "/dishes";
    private final String beaconsEndpoint = "/beacons";
    private final String picturesEndpoint = "/pictures";

    public String fetchCafeterias() {
        String urlString = baseUrl + cafeteriasEndpoint;
        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public String fetchCafeteria(int cafeteriaId) {
        String urlString = baseUrl + cafeteriasEndpoint + "/" + cafeteriaId;
        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public String insertDish(DishEntity dish) {
        String urlString = baseUrl + dishesEndpoint;

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(dish);
        return NetUtils.postJson(urlString, json, HttpsURLConnection.HTTP_CREATED);
    }

    public String fetchDishes(int cafeteriaId) {
        String urlString = baseUrl + cafeteriasEndpoint + "/" + cafeteriaId + dishesEndpoint;
        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public String deleteDish(int dishId) {
        String urlString = baseUrl + dishesEndpoint + "/" + dishId;
        return NetUtils.delete(urlString, HttpsURLConnection.HTTP_OK);
    }

    public String insertBeacon(Beacon beacon) {
        String urlString = baseUrl + beaconsEndpoint;

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(beacon);
        return NetUtils.postJson(urlString, json, HttpsURLConnection.HTTP_CREATED);
    }

    public String updateBeacon(Beacon beacon) {
        String urlString = baseUrl + beaconsEndpoint + "/" + beacon.getId();

        String json = new Gson().toJson(beacon);
        return NetUtils.putJson(urlString, json, HttpsURLConnection.HTTP_OK);
    }

    public String insertPicture(int dishId, String pictureUri) {
        String urlString = baseUrl + picturesEndpoint;

        return NetUtils.postMultipartPicture(urlString, dishId, new File(pictureUri), HttpsURLConnection.HTTP_CREATED);
    }

    public String fetchPictures(int dishId) {
        String urlString = baseUrl + dishesEndpoint + "/" + dishId + picturesEndpoint;

        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public String deletePicture(int pictureId) {
        String urlString = baseUrl + picturesEndpoint + "/" + pictureId;

        return NetUtils.delete(urlString, HttpsURLConnection.HTTP_OK);
    }
}
