package pt.ulisboa.tecnico.cmov.foodist.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.AppExecutors;
import pt.ulisboa.tecnico.cmov.foodist.db.dao.CafeteriaDao;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;

@Database(entities = {CafeteriaEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    @VisibleForTesting
    private static final String DATABASE_NAME = "database.db";
    private static final String TAG = AppDatabase.class.getSimpleName();
    private static AppDatabase sInstance;
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it is accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            // Populate the database
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);
                            seedDatabase(database, appContext);
                            // Notify that the database was created and is ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                })
                .build();
    }

    private static void seedDatabase(final AppDatabase database, Context context) {
        Log.w(TAG, "Seeding db should only happen on first run");

        String json;
        Type cafeteriaType = new TypeToken<List<CafeteriaEntity>>() {
        }.getType();
        try {
            InputStream inputStream = context.getAssets().open("cafeterias.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

            Gson gson = new Gson();
            List<CafeteriaEntity> cafeterias = gson.fromJson(json, cafeteriaType);
            database.runInTransaction(() -> {
                database.cafeteriaDao().insertAll(cafeterias);
            });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "Error seeding database, ", e);
        }
    }

    public abstract CafeteriaDao cafeteriaDao();

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}
