package pt.ulisboa.tecnico.cmov.foodist.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;


public class DishViewModel extends AndroidViewModel {
    private static final String TAG = DishViewModel.class.getSimpleName();
    private static final String QUERY_KEY = "QUERY";

    private final SavedStateHandle mSavedStateHandler;
    private final DataRepository mRepository;
    private final LiveData<List<DishEntity>> mDish;
    private MutableLiveData<Boolean> updating = new MutableLiveData<>(false);

    public DishViewModel(@NonNull Application application,
                         @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        mSavedStateHandler = savedStateHandle;
        mRepository = ((BasicApp) application).getRepository();

        // Use the savedStateHandle.getLiveData() as the input to switchMap,
        // allowing us to recalculate what LiveData to get from the DataRepository
        // based on what query the user has entered
        mDish = Transformations.switchMap(
                savedStateHandle.getLiveData(QUERY_KEY, null),
                (Function<CharSequence, LiveData<List<DishEntity>>>) query -> {
                    if (TextUtils.isEmpty(query)) {
                        return mRepository.getDish();
                    } else {
                        return mRepository.getDish();
                        //return mRepository.getCafeteriasByCampus(Integer.parseInt((String) query));
                    }
                });
    }

    public LiveData<List<DishEntity>> getDish() {
        return mDish;
    }

    public LiveData<Boolean> isUpdating() {
        return updating;
    }

    public void setUpdating(boolean b) {
        updating.postValue(b);
    }

    //public void add(DishEntity)

}

