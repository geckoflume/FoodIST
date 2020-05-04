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
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.OpeningHoursEntity;
import pt.ulisboa.tecnico.cmov.foodist.net.DirectionsFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.DirectionsParser;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerParser;

public class DishViewModel extends AndroidViewModel {
    private static final String TAG = DishViewModel.class.getSimpleName();

    private final LiveData<DishEntity> mObservableDish;
    private final LiveData<List<OpeningHoursEntity>> mObservableOpeningHours;
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
    private final DataRepository mRepository;
    private final int mDishId;
    private MutableLiveData<String> openHoursText = new MutableLiveData<>("");

    public DishViewModel(@NonNull Application application, DataRepository repository,
                         final int dishId, final int statusId) {
        super(application);
        this.mDishId = dishId;
        mRepository = repository;

        mObservableDish = mRepository.loadDish(this.mDishId);
        mObservableOpeningHours = mRepository.loadOpeningHours(this.mDishId, statusId);
    }


    public LiveData<DishEntity> getDish() {
        return mObservableDish;
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


    /**
     * A creator is used to inject the dishId and a statusId into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private final int mDishId;
        private final DataRepository mRepository;
        private final int mStatusId;

        public Factory(@NonNull Application application, int dishId, int statusId) {
            mApplication = application;
            mDishId = dishId;
            mStatusId = statusId;
            mRepository = ((BasicApp) application).getRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new DishViewModel(mApplication, mRepository, mDishId, mStatusId);
        }
    }
}
