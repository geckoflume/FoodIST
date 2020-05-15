package pt.ulisboa.tecnico.cmov.foodist.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * DiskCache implementation, limited to cacheSize bytes, to allow persistence.
 * Least recently used bitmaps are evicted when the max size is reached.
 */
public class PictureDiskCache {
    private static final String TAG = PictureDiskCache.class.getSimpleName();
    private final long maxSize;
    private final LinkedHashSet<String> cache;
    private File storageDir;
    private long size = 0;

    PictureDiskCache(final File storageDir, final long cacheSize) {
        this.maxSize = cacheSize;
        this.storageDir = storageDir;
        this.cache = new LinkedHashSet<>();
        buildCacheFromDisk();
    }

    private void buildCacheFromDisk() {
        File[] pictures = storageDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpeg")); // files are downloaded as .jpeg from the server
        for (File file : pictures) {
            if (file.isFile()) {
                size += file.length();
                cache.add(file.getName());
            }
        }
    }

    private void removeLru() {
        String firstFilename = cache.iterator().next();
        long firstFileSize = new File(storageDir, firstFilename).length();
        if (cache.remove(firstFilename))
            size -= firstFileSize;
    }

    private void trimToSize() {
        while (size > maxSize)
            removeLru();
    }

    public Bitmap get(String filename) {
        Bitmap bitmap = null;
        if (cache.contains(filename)) {
            File image = new File(storageDir, filename);

            try (FileInputStream in = new FileInputStream(image)) {
                bitmap = BitmapFactory.decodeStream(in);
                // moves the key to the front
                cache.remove(filename);
                cache.add(filename);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File " + filename + " not found"); // should not happen
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void put(String filename, Bitmap bitmap) {
        // Evict older data
        trimToSize();

        // Write new file
        File image = new File(storageDir, filename);
        try (FileOutputStream out = new FileOutputStream(image)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // PNG is a lossless format, the compression factor 100 is ignored
            cache.add(filename);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File " + filename + " not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        size += image.length();
    }
}