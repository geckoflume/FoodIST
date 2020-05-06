package pt.ulisboa.tecnico.cmov.foodist;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.NonNull;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Locale;
import java.util.concurrent.Executor;

import pt.ulisboa.tecnico.cmov.foodist.db.AppDatabase;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;

/**
 * Android Application class. Used for accessing singletons.
 */
public class BasicApp extends Application {
    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        AndroidThreeTen.init(this);
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }

    public Executor networkIO() {
        return mAppExecutors.networkIO();
    }
}
