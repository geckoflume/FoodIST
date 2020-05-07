package pt.ulisboa.tecnico.cmov.foodist.net;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
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

    private static final String GOOGLE_TRANSLATE_URL = "https://translation.googleapis.com/language/translate/v2?key=%s";
    private static final String MAPS_DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&mode=walking&key=%s";

    public static String fetchCafeterias() {
        return NetUtils.get(CAFETERIAS_ENDPOINT, HttpsURLConnection.HTTP_OK);
    }

    public static String fetchCafeteria(final int cafeteriaId) {
        String urlString = String.format(Locale.US, CAFETERIA_ENDPOINT, cafeteriaId);
        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String insertDish(final DishEntity dish) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(dish);
        return NetUtils.postJson(DISHES_ENDPOINT, json, HttpsURLConnection.HTTP_CREATED);
    }

    public static String fetchDishes(final int cafeteriaId) {
        String urlString = String.format(Locale.US, CAFETERIA_DISHES_ENDPOINT, cafeteriaId);
        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String deleteDish(final int dishId) {
        String urlString = String.format(Locale.US, DISH_ENDPOINT, dishId);
        return NetUtils.delete(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String insertBeacon(final Beacon beacon) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(beacon);
        return NetUtils.postJson(BEACONS_ENDPOINT, json, HttpsURLConnection.HTTP_CREATED);
    }

    public static String updateBeacon(Beacon beacon) {
        String urlString = String.format(Locale.US, BEACON_ENDPOINT, beacon.getId());

        String json = new Gson().toJson(beacon);
        return NetUtils.putJson(urlString, json, HttpsURLConnection.HTTP_OK);
    }

    public static String insertPicture(final int dishId, String pictureUri) {
        return NetUtils.postMultipartPicture(PICTURES_ENDPOINT, dishId, new File(pictureUri), HttpsURLConnection.HTTP_CREATED);
    }

    public static String fetchPictures(final int dishId) {
        String urlString = String.format(Locale.US, DISH_PICTURES_ENDPOINT, dishId);

        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String deletePicture(final int pictureId) {
        String urlString = String.format(Locale.US, PICTURE_ENDPOINT, pictureId);

        return NetUtils.delete(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static String getPictureUrl(final String filename) {
        return String.format(Locale.US, PICTURES_LOCATION, filename);
    }

    public static String fetchTranslation(final String value, final String locale, final String apiKey) {
        String urlString = String.format(Locale.US, GOOGLE_TRANSLATE_URL, apiKey);

        JsonObject object = new JsonObject();
        object.addProperty("q", value);
        object.addProperty("target", locale);
        return NetUtils.postJson(urlString, object.toString(), HttpsURLConnection.HTTP_OK);
    }

    public static String fetchDirections(final String apiKey, final CafeteriaEntity cafeteria, final Location location) {
        String urlString = String.format(Locale.US, MAPS_DIRECTIONS_URL, location.getLatitude(), location.getLongitude(), cafeteria.getLatitude(), cafeteria.getLongitude(), apiKey);

        return NetUtils.get(urlString, HttpsURLConnection.HTTP_OK);
    }

    public static Bitmap downloadPicture(String filename) {
        String urlString = getPictureUrl(filename);

        return NetUtils.downloadBitmap(urlString, HttpsURLConnection.HTTP_OK);
    }
}
