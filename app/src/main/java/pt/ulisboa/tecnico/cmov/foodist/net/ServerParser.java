package pt.ulisboa.tecnico.cmov.foodist.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaPartialEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;

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
        List<DishEntity> dishEntities = null;

        if (response != null) {
            Type dishListType = new TypeToken<List<DishEntity>>() {
            }.getType();

            dishEntities = new Gson().fromJson(response, dishListType);
        }
        return dishEntities;
    }
}