package pt.ulisboa.tecnico.cmov.foodist.viewmodel;

import android.app.Application;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.location.DirectionsFetcher;
import pt.ulisboa.tecnico.cmov.foodist.location.DirectionsParser;

public class CafeteriaListViewModel extends AndroidViewModel {
    private static final String QUERY_KEY = "QUERY";

    private final SavedStateHandle mSavedStateHandler;
    private final DataRepository mRepository;
    private final LiveData<List<CafeteriaEntity>> mCafeterias;

    public CafeteriaListViewModel(@NonNull Application application,
                                  @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        mSavedStateHandler = savedStateHandle;
        mRepository = ((BasicApp) application).getRepository();

        // Use the savedStateHandle.getLiveData() as the input to switchMap,
        // allowing us to recalculate what LiveData to get from the DataRepository
        // based on what query the user has entered
        mCafeterias = Transformations.switchMap(
                savedStateHandle.getLiveData(QUERY_KEY, null),
                (Function<CharSequence, LiveData<List<CafeteriaEntity>>>) query -> {
                    if (TextUtils.isEmpty(query)) {
                        return mRepository.getCafeterias();
                    } else {
                        return mRepository.getCafeteriasByCampus(Integer.parseInt((String) query));
                    }
                });
    }

    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(QUERY_KEY, query);
    }

    /**
     * Expose the LiveData Cafeterias query so the UI can observe it.
     */
    public LiveData<List<CafeteriaEntity>> getCafeterias() {
        return mCafeterias;
    }

    public void updateCafeteriasDistances(List<CafeteriaEntity> currentCafeterias, Location mCurrentLocation, String apiKey) {
        for (CafeteriaEntity cafeteria : currentCafeterias) {
            DirectionsFetcher directionsFetcher =
                    new DirectionsFetcher(apiKey, cafeteria, mCurrentLocation);
            String response = directionsFetcher.fetchDirections();

            DirectionsParser directionsParser = new DirectionsParser(response);
            cafeteria.setDistance(directionsParser.getDistance());
            cafeteria.setTimeWalk(directionsParser.getDuration());

            Log.i("updater", "updating cafeteria " + cafeteria.getName());
        }
        mRepository.updateCafeterias(currentCafeterias);
    }
}
