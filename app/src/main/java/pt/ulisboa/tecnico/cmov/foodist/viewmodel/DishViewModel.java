package pt.ulisboa.tecnico.cmov.foodist.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishWithPictures;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.PictureEntity;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerParser;


public class DishViewModel extends AndroidViewModel {
    private static final String TAG = DishViewModel.class.getSimpleName();

    private final LiveData<DishWithPictures> mObservableDish;
    private final LiveData<CafeteriaEntity> associatedCafeteria;
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);
    private final DataRepository mRepository;
    private final int mDishId;


    private DishViewModel(@NonNull Application application, DataRepository repository,
                          final int dishId) {
        super(application);
        this.mDishId = dishId;
        mRepository = repository;

        mObservableDish = mRepository.getDishWithPictures(this.mDishId);
        associatedCafeteria = mRepository.getCafeteriaByIdDish(this.mDishId);
    }


    public LiveData<DishWithPictures> getDish() {
        return mObservableDish;
    }

    public LiveData<CafeteriaEntity> getCafeteriaOfDish() {
        return associatedCafeteria;
    }

    public void deleteDish() {
        ((BasicApp) getApplication()).networkIO().execute(() -> {
            if (ServerFetcher.deleteDish(mDishId) != null) {
                List<PictureEntity> mPictures = mRepository.getPicturesByDishId(mDishId);
                mRepository.deleteDishById(mDishId);
                mRepository.deletePictures(mPictures);
                Log.d(TAG, "Deleted dish " + mDishId);
            } else
                Log.d(TAG, "Unable to delete dish " + mDishId);
        });
    }

    public void insertPicture(String pictureUri) {
        Log.d(TAG, "Inserting picture " + pictureUri);
        String response = ServerFetcher.insertPicture(mDishId, pictureUri);
        if (response != null) {
            ServerParser serverParser = new ServerParser();
            mRepository.insertPicture(serverParser.parsePicture(response));
        }
    }

    public void updatePictures() {
        ((BasicApp) getApplication()).networkIO().execute(() -> {
            List<PictureEntity> mPictures = mRepository.getPicturesByDishId(mDishId);
            String responsePictures = ServerFetcher.fetchPictures(mDishId);
            if (responsePictures != null) {
                ServerParser serverParser = new ServerParser();
                List<PictureEntity> fetchedPictures = serverParser.parsePictures(responsePictures);

                // Check if the distant pictures are the same as the local pictures
                mPictures.removeAll(fetchedPictures);
                if (!mPictures.isEmpty()) {
                    mRepository.deletePictures(mPictures);
                    Log.d(TAG, "Deleted obsolete pictures for dish " + mDishId);
                }

                mRepository.insertPictures(fetchedPictures);
                Log.d(TAG, "Updated pictures for dish " + mDishId);
            } else
                Log.e(TAG, "Unable to update pictures for dish " + mDishId);
        });
    }

    public void deletePicture(PictureEntity picture) {
        ((BasicApp) getApplication()).networkIO().execute(() -> {
            if (ServerFetcher.deletePicture(picture.getId()) != null) {
                mRepository.deletePicture(picture);
                Log.d(TAG, "Deleted picture " + picture.getId());
            } else
                Log.d(TAG, "Unable to delete picture " + picture.getId());
        });
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
