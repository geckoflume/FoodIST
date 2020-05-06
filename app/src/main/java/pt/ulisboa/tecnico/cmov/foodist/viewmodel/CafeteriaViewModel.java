package pt.ulisboa.tecnico.cmov.foodist.viewmodel;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.OpeningHoursEntity;
import pt.ulisboa.tecnico.cmov.foodist.net.DirectionsFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.DirectionsParser;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerParser;

public class CafeteriaViewModel extends AndroidViewModel {
    private static final String TAG = CafeteriaViewModel.class.getSimpleName();

    private final LiveData<CafeteriaEntity> mObservableCafeteria;
    private final LiveData<List<OpeningHoursEntity>> mObservableOpeningHours;
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
    private final DataRepository mRepository;
    private final int mCafeteriaId;
    private MutableLiveData<String> openHoursText = new MutableLiveData<>("");

    private CafeteriaViewModel(@NonNull Application application, DataRepository repository,
                               final int cafeteriaId, final int statusId) {
        super(application);
        this.mCafeteriaId = cafeteriaId;
        mRepository = repository;

        mObservableCafeteria = mRepository.getCafeteria(this.mCafeteriaId);
        mObservableOpeningHours = mRepository.getOpeningHours(this.mCafeteriaId, statusId);
    }

    public LiveData<CafeteriaEntity> getCafeteria() {
        return mObservableCafeteria;
    }

    public LiveData<List<OpeningHoursEntity>> getOpeningHours() {
        return mObservableOpeningHours;
    }

    public LiveData<String> getOpenHoursText() {
        return openHoursText;
    }

    public void updateOpenHoursText(String text) {
        openHoursText.postValue(text);
    }

    public List<LatLng> updateCafeteriaDistance(CafeteriaEntity currentCafeteria, final Location mCurrentLocation, final String apiKey) {
        DirectionsFetcher directionsFetcher = new DirectionsFetcher(apiKey, currentCafeteria, mCurrentLocation);
        if (directionsFetcher.getResponse() != null && !directionsFetcher.getResponse().isEmpty()) {
            DirectionsParser directionsParser = directionsFetcher.parse();
            currentCafeteria.setDistance(directionsParser.getDistance());
            currentCafeteria.setTimeWalk(directionsParser.getDuration());

            mRepository.updateCafeteria(currentCafeteria);
            Log.d(TAG, "Updated distance and walk time for cafeteria " + currentCafeteria.getName());

            return directionsParser.getPath();
        } else {
            Log.e(TAG, "Unable to update distance and walk time for cafeteria " + currentCafeteria.getName());
            return null;
        }
    }

    public void updateCafeteriaWaitTime() {
        String responseCafeteria = ServerFetcher.fetchCafeteria(mCafeteriaId);
        if (responseCafeteria != null) {
            ServerParser serverParser = new ServerParser();
            mRepository.updateCafeteriaPartial(serverParser.parseCafeteria(responseCafeteria));
            Log.d(TAG, "Updated wait time for cafeteria " + mCafeteriaId);
        } else {
            Log.e(TAG, "Unable to update time for cafeteria " + mCafeteriaId);
        }
    }

    public LiveData<Boolean> isUpdating() {
        return updating;
    }

    public void setUpdating(boolean b) {
        updating.postValue(b);
    }

    /**
     * A creator is used to inject the cafeteriaId and a statusId into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private final int mCafeteriaId;
        private final DataRepository mRepository;
        private final int mStatusId;

        public Factory(@NonNull Application application, int cafeteriaId, int statusId) {
            mApplication = application;
            mCafeteriaId = cafeteriaId;
            mStatusId = statusId;
            mRepository = ((BasicApp) application).getRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CafeteriaViewModel(mApplication, mRepository, mCafeteriaId, mStatusId);
        }
    }
}
