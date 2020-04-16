package pt.ulisboa.tecnico.cmov.foodist.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;

public class CafeteriaViewModel extends AndroidViewModel {
    private final LiveData<CafeteriaEntity> mObeservableCafeteria;

    private final DataRepository mRepository;
    private final int mCafeteriaId;


    public CafeteriaViewModel(@NonNull Application application, DataRepository repository,
                            final int cafeteriaId) {
        super(application);
        this.mCafeteriaId = cafeteriaId;
        mRepository = repository;

        mObeservableCafeteria = mRepository.loadCafeteria(this.mCafeteriaId);
    }


    public LiveData<CafeteriaEntity> getCafeteria() {
        return mObeservableCafeteria;
    }

    public void updateCafeteriaDirections(int distance, int duration) {
        mRepository.updateCafeteriaDirections(mCafeteriaId, distance, duration);
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
