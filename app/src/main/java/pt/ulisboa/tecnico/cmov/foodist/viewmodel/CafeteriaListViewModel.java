package pt.ulisboa.tecnico.cmov.foodist.viewmodel;

import android.app.Application;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaWithOpeningHours;
import pt.ulisboa.tecnico.cmov.foodist.model.Campus;
import pt.ulisboa.tecnico.cmov.foodist.model.Status;
import pt.ulisboa.tecnico.cmov.foodist.net.DirectionsFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.DirectionsParser;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerParser;

public class CafeteriaListViewModel extends AndroidViewModel {
    private static final String TAG = CafeteriaListViewModel.class.getSimpleName();
    private static final String CAMPUS_KEY = "CAMPUS";
    private static final String STATUS_KEY = "STATUS";

    private final SavedStateHandle mSavedStateHandler;
    private final DataRepository mRepository;
    private final LiveData<List<CafeteriaWithOpeningHours>> mCafeteriasWithOpeningHours;
    private final LiveData<List<CafeteriaEntity>> mCafeterias;
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);

    public CafeteriaListViewModel(@NonNull Application application,
                                  @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        mSavedStateHandler = savedStateHandle;
        mRepository = ((BasicApp) application).getRepository();

        // Use the savedStateHandle.getLiveData() as inputs to switchMap,
        // allowing us to recalculate what LiveData to get from the DataRepository
        // based on the user campus and status
        CustomLiveData trigger = new CustomLiveData(
                mSavedStateHandler.getLiveData(CAMPUS_KEY, Campus.ALL),
                mSavedStateHandler.getLiveData(STATUS_KEY, Status.DEFAULT));
        mCafeterias = Transformations.switchMap(trigger, value -> {
            if (value.first == Campus.ALL) {
                return mRepository.getCafeterias();
            } else {
                return mRepository.getCafeteriasByCampusId(value.first - 1);
            }
        });
        mCafeteriasWithOpeningHours = Transformations.switchMap(trigger, value -> {
            if (value.first == Campus.ALL) {
                return mRepository.getCafeteriasWithOpeningHours(value.second);
            } else {
                return mRepository.getCafeteriasWithOpeningHoursByCampusId(value.second, value.first - 1);
            }
        });
    }

    public void setCampus(Integer campus) {
        // Save the user's campus into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(CAMPUS_KEY, campus);
    }

    public void setStatus(Integer status) {
        // Save the user's status into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(STATUS_KEY, status);
    }

    public LiveData<Integer> getStatus() {
        return mSavedStateHandler.getLiveData(STATUS_KEY, Status.DEFAULT);
    }

    /**
     * Expose the LiveData Cafeterias query so the UI can observe it.
     */
    public LiveData<List<CafeteriaEntity>> getCafeterias() {
        return mCafeterias;
    }

    public LiveData<List<CafeteriaWithOpeningHours>> getCafeteriasWithOpeningHours() {
        return mCafeteriasWithOpeningHours;
    }

    public void updateCafeteriasDistances(List<CafeteriaEntity> currentCafeterias, final Location mCurrentLocation, final String apiKey) {
        for (CafeteriaEntity cafeteria : currentCafeterias) {
            DirectionsFetcher directionsFetcher = new DirectionsFetcher(apiKey, cafeteria, mCurrentLocation);
            DirectionsParser directionsParser = directionsFetcher.parse();
            cafeteria.setDistance(directionsParser.getDistance());
            cafeteria.setTimeWalk(directionsParser.getDuration());

            Log.d(TAG, "Updating distance and walk time for cafeteria " + cafeteria.getName());
        }
        mRepository.updateCafeterias(currentCafeterias);
    }

    public void updateCafeteriasWaitTimes() {
        String responseCafeterias = ServerFetcher.fetchCafeterias();
        if (responseCafeterias != null) {
            ServerParser serverParser = new ServerParser();
            mRepository.updateCafeteriasPartial(serverParser.parseCafeterias(responseCafeterias));
        }
    }

    public LiveData<Boolean> isUpdating() {
        return updating;
    }

    public void setUpdating(boolean b) {
        updating.postValue(b);
    }
}

class CustomLiveData extends MediatorLiveData<Pair<Integer, Integer>> {
    CustomLiveData(LiveData<Integer> campus, LiveData<Integer> status) {
        addSource(campus, first -> setValue(Pair.create(first, status.getValue())));
        addSource(status, second -> setValue(Pair.create(campus.getValue(), second)));
    }
}
