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
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.threeten.bp.LocalTime;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.AppExecutors;
import pt.ulisboa.tecnico.cmov.foodist.db.dao.CafeteriaDao;
import pt.ulisboa.tecnico.cmov.foodist.db.dao.OpeningHoursDao;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.OpeningHoursEntity;

@Database(entities = {CafeteriaEntity.class, OpeningHoursEntity.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
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

        JsonSerializer<LocalTime> localTimeJsonSerializer = (src, typeOfSrc, context1) -> src == null ? null : new JsonPrimitive(src.toString());
        JsonDeserializer<LocalTime> localTimeJsonDeserializer = (json, typeOfT, context12) -> json == null ? null : LocalTime.parse(json.getAsString());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, localTimeJsonSerializer)
                .registerTypeAdapter(LocalTime.class, localTimeJsonDeserializer)
                .create();
        String json;
        Type cafeteriaType = new TypeToken<List<CafeteriaEntity>>() {
        }.getType();
        Type openingHoursType = new TypeToken<List<OpeningHoursEntity>>() {
        }.getType();

        try {
            InputStream inputStream = context.getAssets().open("cafeterias.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

            List<CafeteriaEntity> cafeterias = gson.fromJson(json, cafeteriaType);
            database.runInTransaction(() -> database.cafeteriaDao().insertAll(cafeterias));

            inputStream = context.getAssets().open("opening_hours.json");
            size = inputStream.available();
            buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

            List<OpeningHoursEntity> openingHours = gson.fromJson(json, openingHoursType);
            database.runInTransaction(() -> database.openingHoursDao().insertAll(openingHours));
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "Error seeding database, ", e);
        }
    }

    public abstract CafeteriaDao cafeteriaDao();

    public abstract OpeningHoursDao openingHoursDao();

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

    LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}
