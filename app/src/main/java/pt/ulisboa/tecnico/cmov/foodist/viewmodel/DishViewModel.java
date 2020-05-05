package pt.ulisboa.tecnico.cmov.foodist.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishWithPictures;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerParser;


public class DishViewModel extends AndroidViewModel {
    private static final String TAG = DishViewModel.class.getSimpleName();

    private final LiveData<DishWithPictures> mObservableDish;
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
    private final DataRepository mRepository;
    private final int mDishId;


    public DishViewModel(@NonNull Application application, DataRepository repository,
                         final int dishId) {
        super(application);
        this.mDishId = dishId;
        mRepository = repository;

        mObservableDish = mRepository.getDishWithPictures(this.mDishId);
    }


    public LiveData<DishWithPictures> getDish() {
        return mObservableDish;
    }

    public void insertPicture(String pictureUri) {
        String response = ServerFetcher.insertPicture(mDishId, pictureUri);
        if (response != null) {
            ServerParser serverParser = new ServerParser();
            mRepository.insertPicture(serverParser.parsePicture(response));
        }
        Log.d(TAG, "Inserting picture " + pictureUri);
    }

    /**
     * A creator is used to inject the dishId into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private final int mDishId;
        private final DataRepository mRepository;

        public Factory(@NonNull Application application, int dishId) {
            mApplication = application;
            mDishId = dishId;
            mRepository = ((BasicApp) application).getRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new DishViewModel(mApplication, mRepository, mDishId);
        }
    }
}
