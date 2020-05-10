package pt.ulisboa.tecnico.cmov.foodist.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.LruCache;

import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;

/**
 * Dual cache class, using LruCache and PictureDiskCache as a fallback.
 * Downloads the needed picture from an URL in case it is nowhere to be found.
 */
public class DualCache {
    private final static int CACHE_SIZE_MB = 1024 * 1024 * 100;
    private static DualCache instance;
    private LruCache<String, Bitmap> lruCache;
    private PictureDiskCache pictureDiskCache;

    private DualCache(final Context context) {
        final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/4th of the available memory for the cache
        int lruCacheSize = 1024 * 1024 * memClass / 4;
        if (CACHE_SIZE_MB < lruCacheSize) // to avoid OOM errors, on low performance devices
            lruCacheSize = CACHE_SIZE_MB;

        lruCache = new LruCache<>(lruCacheSize);
        pictureDiskCache = new PictureDiskCache(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), CACHE_SIZE_MB);
    }

    public static DualCache getInstance(final Context context) {
        // Singleton
        if (instance == null)
            instance = new DualCache(context);
        return instance;
    }

    private void saveBitmapToCache(String filename, Bitmap bitmap) {
        // no need to check if the file is not in any of the caches because downloadPictureIfNeeded() already did the check
        lruCache.put(filename, bitmap);
        pictureDiskCache.put(filename, bitmap);
    }

    private Bitmap retrieveBitmapFromCache(String filename) {
        Bitmap bitmap = lruCache.get(filename); // the fastest in-memory cache
        if (bitmap == null) {
            bitmap = pictureDiskCache.get(filename); // slower, persistent on-disk cache fallback
            if (bitmap != null)
                lruCache.put(filename, bitmap); // in case it is found on-disk, load it in the lruCache for faster access
        }
        return bitmap;
    }

    public Bitmap downloadPictureIfNeeded(String filename) {
        Bitmap bitmap = retrieveBitmapFromCache(filename);
        if (bitmap == null) { // the picture is not found in any of the caches
            bitmap = ServerFetcher.downloadPicture(filename);
            if (bitmap != null) // check just in case the remote file was deleted, or the user has connectivity issues
                saveBitmapToCache(filename, bitmap);
        }
        return bitmap;
    }
}