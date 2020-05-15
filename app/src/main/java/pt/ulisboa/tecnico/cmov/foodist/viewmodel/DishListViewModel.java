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
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishWithPictures;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.PictureEntity;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerParser;


public class DishListViewModel extends AndroidViewModel {
    private static final String TAG = DishListViewModel.class.getSimpleName();

    private final DataRepository mRepository;
    private final LiveData<List<DishWithPictures>> mDishesWithPictures;
    private final int mCafeteriaId;

    private DishListViewModel(@NonNull Application application, DataRepository repository,
                              final int cafeteriaId) {
        super(application);
        this.mCafeteriaId = cafeteriaId;
        mRepository = repository;

        mDishesWithPictures = mRepository.getDishesWithPicturesByCafeteriaId(cafeteriaId);
    }

    public LiveData<List<DishWithPictures>> getDishesWithPictures() {
        return mDishesWithPictures;
    }

    public void updateDishes() {
        List<DishEntity> mDishes = mRepository.getDishesByCafeteriaIdEntities(mCafeteriaId);
        String responseDishes = ServerFetcher.fetchDishes(mCafeteriaId);
        if (responseDishes != null) {
            ServerParser serverParser = new ServerParser();
            List<DishEntity> fetchedDishes = serverParser.parseDishes(responseDishes);

            // Check if the distant dishes are the same as the local dishes
            mDishes.removeAll(fetchedDishes);
            if (!mDishes.isEmpty()) {
                mRepository.deleteDishes(mDishes);
                Log.d(TAG, "Deleted obsolete dishes for cafeteria " + mCafeteriaId);
            }

            mRepository.insertDishes(fetchedDishes);
            Log.d(TAG, "Updated dishes for cafeteria " + mCafeteriaId);
        } else
            Log.e(TAG, "Unable to update dishes for cafeteria " + mCafeteriaId);
    }

    public void updateFirstPicture() {
        List<DishEntity> mDishes = mRepository.getDishesByCafeteriaIdEntities(mCafeteriaId);

        ((BasicApp) getApplication()).networkIO().execute(() -> {
            for (DishEntity d : mDishes) {
                List<PictureEntity> pictures = mRepository.getPicturesByDishId(d.getId());
                String responsePictures = ServerFetcher.fetchPictures(d.getId());
                if (responsePictures != null) {
                    ServerParser serverParser = new ServerParser();
                    List<PictureEntity> fetchedPictures = serverParser.parsePictures(responsePictures);

                    if (!fetchedPictures.isEmpty()) {
                        PictureEntity fetchedPicture = fetchedPictures.get(0);
                        if (!pictures.isEmpty()) {
                            PictureEntity mPicture = pictures.get(0);
                            if (!mPicture.equals(fetchedPicture)) {
                                mRepository.deletePicture(mPicture);
                                Log.d(TAG, "Deleted obsolete first picture for dish " + d.getId());
                                mRepository.insertPicture(fetchedPicture);
                                Log.d(TAG, "Updated first picture for dish " + d.getId());
                            }
                        } else {
                            mRepository.insertPicture(fetchedPicture);
                            Log.d(TAG, "Updated first picture for dish " + d.getId());
                        }
                    } else {
                        mRepository.deletePictures(pictures);
                        Log.d(TAG, "Deleted all obsolete pictures for dish " + d.getId());
                    }
                } else
                    Log.e(TAG, "Unable to update first picture for dish " + d.getId());
            }
        });
    }

    /**
     * A creator is used to inject the cafeteriaId into the ViewModel.
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

