package pt.ulisboa.tecnico.cmov.foodist.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaPartialEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaWithOpeningHours;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishWithPictures;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.OpeningHoursEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.PictureEntity;

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

    // Cafeterias
    public LiveData<List<CafeteriaEntity>> getCafeterias() {
        return mObservableCafeterias;
    }

    public LiveData<List<CafeteriaEntity>> getCafeteriasByCampusId(final int campus) {
        return mDatabase.cafeteriaDao().getAllByCampusId(campus);
    }

    public LiveData<CafeteriaEntity> getCafeteria(final int cafeteriaId) {
        return mDatabase.cafeteriaDao().getById(cafeteriaId);
    }

    public void updateCafeterias(List<CafeteriaEntity> currentCafeterias) {
        mDatabase.cafeteriaDao().updateAll(currentCafeterias);
    }

    public void updateCafeteriasPartial(List<CafeteriaPartialEntity> cafeteriaPartialEntities) {
        mDatabase.cafeteriaDao().updateAllPartial(cafeteriaPartialEntities);
    }

    public void updateCafeteria(CafeteriaEntity currentCafeteria) {
        mDatabase.cafeteriaDao().update(currentCafeteria);
    }

    public void updateCafeteriaPartial(CafeteriaPartialEntity cafeteriaPartialEntity) {
        mDatabase.cafeteriaDao().updatePartial(cafeteriaPartialEntity);
    }

    public LiveData<List<OpeningHoursEntity>> getOpeningHours(final int cafeteriaId, final int status) {
        return mDatabase.openingHoursDao().getAllByCafeteriaStatus(cafeteriaId, status);
    }

    public LiveData<List<CafeteriaWithOpeningHours>> getCafeteriasWithOpeningHours(final int status) {
        return mDatabase.cafeteriaDao().getCafeteriasWithOpeningHours(status);
    }

    public LiveData<List<CafeteriaWithOpeningHours>> getCafeteriasWithOpeningHoursByCampusId(final int status, final int campus) {
        return mDatabase.cafeteriaDao().getCafeteriasWithOpeningHoursByCampusId(status, campus);
    }

    //Cafeteria and Dish
    public LiveData<CafeteriaEntity> getCafeteriaByIdDish(int idDish) {
        return mDatabase.cafeteriaDao().getByIdDish(idDish);
    }

    //Dishes
    public List<DishEntity> getDishesByCafeteriaIdEntities(int cafeteriaId) {
        return mDatabase.dishDao().getAllByCafeteriaIdEntities(cafeteriaId);
    }

    public void insertDish(DishEntity dish) {
        mDatabase.dishDao().insert(dish);
    }

    public void insertDishes(List<DishEntity> dishes) {
        mDatabase.dishDao().insert(dishes);
    }

    public void deleteDishes(List<DishEntity> dishes) {
        mDatabase.dishDao().deleteAll(dishes);
    }

    public void deleteDishById(int dishId) {
        mDatabase.dishDao().deleteById(dishId);
    }

    //Pictures
    public LiveData<List<DishWithPictures>> getDishesWithPicturesByCafeteriaId(final int cafeteriaId) {
        return mDatabase.dishDao().getDishesWithPicturesByCafeteriaId(cafeteriaId);
    }

    public LiveData<DishWithPictures> getDishWithPictures(final int dishId) {
        return mDatabase.dishDao().getDishWithPictures(dishId);
    }

    public void insertPicture(PictureEntity picture) {
        mDatabase.pictureDao().insert(picture);
    }

    public void deletePictures(List<PictureEntity> pictures) {
        mDatabase.pictureDao().deleteAll(pictures);
    }

    public void deletePicture(PictureEntity picture) {
        mDatabase.pictureDao().delete(picture);
    }

    public void insertPictures(List<PictureEntity> pictures) {
        mDatabase.pictureDao().insertAll(pictures);
    }

    public List<PictureEntity> getPicturesByDishId(int dishId) {
        return mDatabase.pictureDao().getAllByDishIdEntities(dishId);
    }
}
