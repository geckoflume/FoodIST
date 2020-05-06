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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

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
import pt.ulisboa.tecnico.cmov.foodist.ui.LocationUtils;

public class CafeteriaListViewModel extends AndroidViewModel {
    private static final String TAG = CafeteriaListViewModel.class.getSimpleName();
    private static final String CAMPUS_KEY = "CAMPUS";
    private static final String STATUS_KEY = "STATUS";

    private final SavedStateHandle mSavedStateHandler;
    private final DataRepository mRepository;
    private final LiveData<List<CafeteriaWithOpeningHours>> mCafeteriasWithOpeningHours;
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

    public LiveData<List<CafeteriaWithOpeningHours>> getCafeteriasWithOpeningHours() {
        return mCafeteriasWithOpeningHours;
    }

    private List<CafeteriaEntity> getCafeteriasEntities() {
        List<CafeteriaEntity> mCafeterias;
        Integer campus = mSavedStateHandler.get(CAMPUS_KEY);
        if (campus == null || campus == Campus.ALL)
            mCafeterias = mRepository.getCafeteriasEntities();
        else
            mCafeterias = mRepository.getCafeteriasByCampusIdEntities(campus - 1);

        return mCafeterias;
    }

    public void updateCafeteriasDistances(final Location mCurrentLocation, final String apiKey) {
        List<CafeteriaEntity> mCafeterias = getCafeteriasEntities();
        for (CafeteriaEntity cafeteria : mCafeterias) {
            DirectionsFetcher directionsFetcher = new DirectionsFetcher(apiKey, cafeteria, mCurrentLocation);
            DirectionsParser directionsParser = directionsFetcher.parse();
            if (directionsParser != null) {
                cafeteria.setDistance(directionsParser.getDistance());
                cafeteria.setTimeWalk(directionsParser.getDuration());
                mRepository.updateCafeterias(mCafeterias);
                Log.d(TAG, "Updated distance and walk time for cafeteria " + cafeteria.getName());
            } else
                Log.e(TAG, "Unable to update distance and walk time for cafeteria " + cafeteria.getName());

        }
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

    public void updateMap(GoogleMap mMap, SupportMapFragment mapFragment) {
        ((BasicApp) getApplication()).networkIO().execute(() -> {
            List<CafeteriaEntity> mCafeterias = getCafeteriasEntities();
            mapFragment.getView().post(() -> LocationUtils.updateMap(mMap, mapFragment, mCafeterias));
        });
    }
}

class CustomLiveData extends MediatorLiveData<Pair<Integer, Integer>> {
    CustomLiveData(LiveData<Integer> campus, LiveData<Integer> status) {
        addSource(campus, first -> setValue(Pair.create(first, status.getValue())));
        addSource(status, second -> setValue(Pair.create(campus.getValue(), second)));
    }
}
