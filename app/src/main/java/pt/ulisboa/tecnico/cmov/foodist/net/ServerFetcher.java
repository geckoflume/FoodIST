package pt.ulisboa.tecnico.cmov.foodist.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import javax.net.ssl.HttpsURLConnection;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.model.Beacon;

public abstract class ServerFetcher {
    private static final String BASE_URL = "https://data.florianmornet.fr/api";

    private static final String CAFETERIAS_ENDPOINT = BASE_URL + "/cafeterias";
    private static final String BEACONS_ENDPOINT = BASE_URL + "/beacons";
    private static final String DISHES_ENDPOINT = BASE_URL + "/dishes";
    private static final String PICTURES_ENDPOINT = BASE_URL + "/pictures";
    private static final String PICTURES_LOCATION = BASE_URL + "/uploads/%s";

    private static final String CAFETERIA_ENDPOINT = CAFETERIAS_ENDPOINT + "/%d";
    private static final String BEACON_ENDPOINT = BEACONS_ENDPOINT + "/%d";
    private static final String CAFETERIA_DISHES_ENDPOINT = CAFETERIAS_ENDPOINT + "/%d/dishes";
    private static final String DISH_ENDPOINT = DISHES_ENDPOINT + "/%d";
    private static final String DISH_PICTURES_ENDPOINT = DISHES_ENDPOINT + "/%d/pictures";
    private static final String PICTURE_ENDPOINT = PICTURES_ENDPOINT + "/%d";

    public static String fetchCafeterias() {
        return NetUtils.get(CAFETERIAS_ENDPOINT, HttpsURLConnection.HTTP_OK);
    }

    public static String fetchCafeteria(int cafeteriaId) {
        String urlString = String.format(CAFETERIA_ENDPOINT, cafeteriaId);
        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String insertDish(DishEntity dish) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(dish);
        return NetUtils.postJson(DISHES_ENDPOINT, json, HttpsURLConnection.HTTP_CREATED);
    }

    public static String fetchDishes(int cafeteriaId) {
        String urlString = String.format(CAFETERIA_DISHES_ENDPOINT, cafeteriaId);
        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String deleteDish(int dishId) {
        String urlString = String.format(DISH_ENDPOINT, dishId);
        return NetUtils.delete(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String insertBeacon(Beacon beacon) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(beacon);
        return NetUtils.postJson(BEACONS_ENDPOINT, json, HttpsURLConnection.HTTP_CREATED);
    }

    public static String updateBeacon(Beacon beacon) {
        String urlString = String.format(BEACON_ENDPOINT, beacon.getId());

        String json = new Gson().toJson(beacon);
        return NetUtils.putJson(urlString, json, HttpsURLConnection.HTTP_OK);
    }

    public static String insertPicture(int dishId, String pictureUri) {
        return NetUtils.postMultipartPicture(PICTURES_ENDPOINT, dishId, new File(pictureUri), HttpsURLConnection.HTTP_CREATED);
    }

    public static String fetchPictures(int dishId) {
        String urlString = String.format(DISH_PICTURES_ENDPOINT, dishId);

        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String deletePicture(int pictureId) {
        String urlString = String.format(PICTURE_ENDPOINT, pictureId);

        return NetUtils.delete(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String getPictureUrl(String filename) {
        return String.format(PICTURES_LOCATION, filename);
    }
}
