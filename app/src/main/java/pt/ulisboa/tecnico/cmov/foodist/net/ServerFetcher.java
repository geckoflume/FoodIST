package pt.ulisboa.tecnico.cmov.foodist.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;

public class ServerFetcher {
    private final String baseUrl = "https://data.florianmornet.fr/api";

    public String fetchCafeterias() {
        String urlString = baseUrl + "/cafeterias";
        return NetUtils.get(urlString);
    }

    public String fetchCafeteria(int cafeteriaId) {
        String urlString = baseUrl + "/cafeterias/" + cafeteriaId;
        return NetUtils.get(urlString);
    }

    public String insertDish(DishEntity dish) {
        String urlString = baseUrl + "/dishes";

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(dish);
        return NetUtils.postJson(urlString, json, 201);
    }
}
