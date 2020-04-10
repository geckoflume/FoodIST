package pt.ulisboa.tecnico.cmov.foodist;

import android.app.Application;

import pt.ulisboa.tecnico.cmov.foodist.db.AppDatabase;

/**
 * Android Application class. Used for accessing singletons.
 */
public class BasicApp extends Application {
    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }
}
