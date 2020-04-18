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
import pt.ulisboa.tecnico.cmov.foodist.location.DirectionsFetcher;
import pt.ulisboa.tecnico.cmov.foodist.location.DirectionsParser;

public class CafeteriaViewModel extends AndroidViewModel {
    private static final String TAG = CafeteriaViewModel.class.getSimpleName();

    private final LiveData<CafeteriaEntity> mObservableCafeteria;
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
    private final DataRepository mRepository;
    private final int mCafeteriaId;

    public CafeteriaViewModel(@NonNull Application application, DataRepository repository,
                              final int cafeteriaId) {
        super(application);
        this.mCafeteriaId = cafeteriaId;
        mRepository = repository;

        mObservableCafeteria = mRepository.loadCafeteria(this.mCafeteriaId);
    }


    public LiveData<CafeteriaEntity> getCafeteria() {
        return mObservableCafeteria;
    }

    public List<LatLng> updateCafeteriaDistance(CafeteriaEntity currentCafeteria, final Location mCurrentLocation, final String apiKey) {
        DirectionsFetcher directionsFetcher = new DirectionsFetcher(apiKey, currentCafeteria, mCurrentLocation);
        DirectionsParser directionsParser = directionsFetcher.parse();
        currentCafeteria.setDistance(directionsParser.getDistance());
        currentCafeteria.setTimeWalk(directionsParser.getDuration());

        Log.i(TAG, "Updating distance and walk time for cafeteria " + currentCafeteria.getName());
        mRepository.updateCafeteria(currentCafeteria);

        return directionsParser.getPath();
    }

    public LiveData<Boolean> isUpdating() {
        return updating;
    }

    public void setUpdating(boolean b) {
        updating.postValue(b);
    }

    /**
     * A creator is used to inject the cafeteriaId into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final int mCafeteriaId;

        private final DataRepository mRepository;

        public Factory(@NonNull Application application, int cafeteriaId) {
            mApplication = application;
            mCafeteriaId = cafeteriaId;
            mRepository = ((BasicApp) application).getRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CafeteriaViewModel(mApplication, mRepository, mCafeteriaId);
        }
    }
}
