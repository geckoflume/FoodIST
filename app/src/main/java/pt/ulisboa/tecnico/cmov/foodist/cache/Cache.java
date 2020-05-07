package pt.ulisboa.tecnico.cmov.foodist.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.LruCache;

import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;

public class Cache {
    private final static int CACHE_SIZE_MB = 1024 * 1024 * 100;
    private static Cache instance;
    private LruCache<String, Bitmap> lruCache;
    private PictureDiskCache pictureDiskCache;

    private Cache(Context context) {
        final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Use 1/4th of the available memory for the cache
        int lruCacheSize = 1024 * 1024 * memClass / 4;
        if (CACHE_SIZE_MB < lruCacheSize) // to avoid OOM
            lruCacheSize = CACHE_SIZE_MB;

        lruCache = new LruCache<>(lruCacheSize);
        pictureDiskCache = new PictureDiskCache(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), CACHE_SIZE_MB);
    }

    public static Cache getInstance(Context context) {
        // Singleton
        if (instance == null)
            instance = new Cache(context);
        return instance;
    }

    public void saveBitmapToCache(String filename, Bitmap bitmap) {
        if (retrieveBitmapFromCache(filename) == null) { // add it only if not present
            lruCache.put(filename, bitmap);
            pictureDiskCache.put(filename, bitmap);
        }
    }

    public Bitmap retrieveBitmapFromCache(String filename) {
        Bitmap bitmap = lruCache.get(filename); // the fastest in-memory
        if (bitmap == null)
            bitmap = pictureDiskCache.get(filename); // slower, persistent on-disk fallback
        return bitmap;
    }

    public Bitmap downloadPictureIfNeeded(String filename) {
        Bitmap bitmap = retrieveBitmapFromCache(filename);
        if (bitmap == null) {
            bitmap = ServerFetcher.downloadPicture(filename);
            if (bitmap != null) // in case the remote file was deleted, or the user has connectivity issues
                saveBitmapToCache(filename, bitmap);
        }
        return bitmap;
    }
}