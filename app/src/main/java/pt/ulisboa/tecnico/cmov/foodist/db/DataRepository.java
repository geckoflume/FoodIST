package pt.ulisboa.tecnico.cmov.foodist.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.OpeningHoursEntity;

/**
 * Repository handling the work with cafeterias.
 */
public class DataRepository {
    private static DataRepository sInstance;
    private final AppDatabase mDatabase;
    private MediatorLiveData<List<CafeteriaEntity>> mObservableCafeterias;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        mObservableCafeterias = new MediatorLiveData<>();

        mObservableCafeterias.addSource(mDatabase.cafeteriaDao().getAll(),
                cafeteriaEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null)
                        mObservableCafeterias.postValue(cafeteriaEntities);
                });
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null)
                    sInstance = new DataRepository(database);
            }
        }
        return sInstance;
    }

    /**
     * Get the list of cafeterias from the database and get notified when the data changes.
     */
    public LiveData<List<CafeteriaEntity>> getCafeterias() {
        return mObservableCafeterias;
    }

    public LiveData<List<CafeteriaEntity>> getCafeteriasByCampus(int campus) {
        return mDatabase.cafeteriaDao().getAllByCampusId(campus);
    }

    public LiveData<CafeteriaEntity> loadCafeteria(final int cafeteriaId) {
        return mDatabase.cafeteriaDao().findById(cafeteriaId);
    }

    public void updateCafeterias(List<CafeteriaEntity> currentCafeterias) {
        mDatabase.cafeteriaDao().updateAll(currentCafeterias);
    }

    public void updateCafeteria(CafeteriaEntity currentCafeteria) {
        mDatabase.cafeteriaDao().update(currentCafeteria);
    }

    public LiveData<List<OpeningHoursEntity>> loadOpeningHours(final int cafeteriaId, final int status) {
        return mDatabase.openingHoursDao().getAllByCafeteriaIdStatus(cafeteriaId, status);
    }


}
