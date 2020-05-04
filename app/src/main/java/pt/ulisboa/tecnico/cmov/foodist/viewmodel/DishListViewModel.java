package pt.ulisboa.tecnico.cmov.foodist.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerParser;


public class DishListViewModel extends AndroidViewModel {
    private static final String TAG = DishListViewModel.class.getSimpleName();

    private final DataRepository mRepository;
    private final LiveData<List<DishEntity>> mDishes;
    private final int mCafeteriaId;

    public DishListViewModel(@NonNull Application application, DataRepository repository,
                             final int cafeteriaId) {
        super(application);
        this.mCafeteriaId = cafeteriaId;
        mRepository = repository;

        mDishes = mRepository.getDishesByCafeteria(cafeteriaId);
    }

    public LiveData<List<DishEntity>> getDishes() {
        return mDishes;
    }

    public void updateDishes() {
        mRepository.deleteDishes(mCafeteriaId);
        Log.d(TAG, "Deleted dishes for cafeteria " + mCafeteriaId);

        ServerFetcher serverFetcher = new ServerFetcher();
        String responseDishes = serverFetcher.fetchDishes(mCafeteriaId);
        ServerParser serverParser = new ServerParser();
        List<DishEntity> fetchedDishes = serverParser.parseDishes(responseDishes);
        if (fetchedDishes != null) {
            mRepository.updateDishes(fetchedDishes);
        }
        Log.d(TAG, "Updated dishes for cafeteria " + mCafeteriaId);
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
            return (T) new DishListViewModel(mApplication, mRepository, mCafeteriaId);
        }
    }
}

