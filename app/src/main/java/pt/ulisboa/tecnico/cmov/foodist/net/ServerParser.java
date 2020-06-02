package pt.ulisboa.tecnico.cmov.foodist.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaPartialEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.PictureEntity;
import pt.ulisboa.tecnico.cmov.foodist.model.Beacon;

/**
 * Helper class to parse JSON responses and build instances of corresponding classes
 * using Google's Gson.
 */
public class ServerParser {
    private static final String TAG = ServerParser.class.getSimpleName();

    public List<CafeteriaPartialEntity> parseCafeterias(String response) {
        List<CafeteriaPartialEntity> cafeteriaEntities = null;

        if (response != null) {
            Type cafeteriaListType = new TypeToken<List<CafeteriaPartialEntity>>() {
            }.getType();

            cafeteriaEntities = new Gson().fromJson(response, cafeteriaListType);
        }
        return cafeteriaEntities;
    }

    public CafeteriaPartialEntity parseCafeteria(String response) {
        CafeteriaPartialEntity cafeteriaEntity = null;

        if (response != null)
            cafeteriaEntity = new Gson().fromJson(response, CafeteriaPartialEntity.class);
        return cafeteriaEntity;
    }

    public DishEntity parseDish(String response) {
        DishEntity ret = null;
        if (response != null)
            ret = new Gson().fromJson(response, DishEntity.class);
        return ret;
    }

    public List<DishEntity> parseDishes(String response) {
        List<DishEntity> dishEntities = new ArrayList<>();

        if (response != null && !response.isEmpty()) {
            Type dishListType = new TypeToken<List<DishEntity>>() {
            }.getType();

            dishEntities = new Gson().fromJson(response, dishListType);
        }
        return dishEntities;
    }

    public Beacon parseBeacon(String response) {
        Beacon beacon = null;
        if (response != null)
            beacon = new Gson().fromJson(response, Beacon.class);
        return beacon;
    }

    public List<PictureEntity> parsePictures(String response) {
        List<PictureEntity> pictures = new ArrayList<>();

        if (response != null && !response.equals("")) {
            Type pictureListType = new TypeToken<List<PictureEntity>>() {
            }.getType();

            pictures = new Gson().fromJson(response, pictureListType);
        }
        return pictures;
    }

    public PictureEntity parsePicture(String response) {
        PictureEntity picture = null;
        if (response != null)
            picture = new Gson().fromJson(response, PictureEntity.class);
        return picture;
    }

    public String parseTranslation(String response) {
        String translation = "";
        try {
            JSONArray jsonArray = new JSONObject(response).getJSONObject("data").getJSONArray("translations");
            if (jsonArray.length() > 0)
                translation = jsonArray.getJSONObject(0).getString("translatedText");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return translation;
    }
}