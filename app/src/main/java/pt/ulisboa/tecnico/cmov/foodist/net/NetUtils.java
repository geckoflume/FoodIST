package pt.ulisboa.tecnico.cmov.foodist.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public abstract class NetUtils {
    private static final String TAG = NetUtils.class.getSimpleName();
    private static final int TIMEOUT = 5000;                        // 5 seconds
    private static final int FILESIZE_LIMIT = 1024 * 1024 * 2;      // 2 MB

    private static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    private static String send(String method, String urlString, int expectedResponseCode) {
        String response = null;
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(TIMEOUT);

            if (urlConnection.getResponseCode() == expectedResponseCode) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                response = readStream(in);
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "UnknownHostException, cannot reach " + urlString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
    }

    public static String get(String urlString, int expectedResponseCode) {
        return send("GET", urlString, expectedResponseCode);
    }

    public static String delete(String urlString, int expectedResponseCode) {
        return send("DELETE", urlString, expectedResponseCode);
    }

    private static String sendJson(String method, String urlString, String json, int expectedResponseCode) {
        String response = null;
        HttpsURLConnection urlConnection = null;

        try {
            json = convertStringToUTF8(json);
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();

            if (urlConnection.getResponseCode() == expectedResponseCode) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                response = readStream(in);
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "UnknownHostException, cannot reach " + urlString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
    }

    public static String postJson(String urlString, String json, int expectedResponseCode) {
        return sendJson("POST", urlString, json, expectedResponseCode);
    }

    public static String putJson(String urlString, String json, int expectedResponseCode) {
        return sendJson("PUT", urlString, json, expectedResponseCode);
    }

    public static String postMultipartPicture(String urlString, int dishId, File picture, int expectedResponseCode) {
        String response = null;
        HttpsURLConnection urlConnection = null;
        String boundary = "---" + System.currentTimeMillis();

        // Compress picture to comply with php default "upload_max_filesize = 2M" (defined in FILESIZE_LIMIT_MB)
        Bitmap bmp = BitmapFactory.decodeFile(picture.getAbsolutePath());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int quality = 70;
        do {
            bos.reset();
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            Log.d(TAG, "Compressed picture from " + (bmp.getByteCount() / 1024) + "KB to " + bos.size() / 1024 + "KB (" + quality + "% quality)");
            if (quality >= 10)
                quality -= 10;
            else
                break;
        } while (bos.size() > FILESIZE_LIMIT); // while the size of the bitmap is more than FILESIZE_LIMIT_MB

        try {
            // Create a unique boundary based on timestamp
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = urlConnection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

            // Add a form field "dish_id" to the request
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"dish_id\"\r\n");
            writer.append("Content-Type: text/plain; charset=UTF-8\r\n\r\n");
            writer.append(Integer.toString(dishId)).append("\r\n");
            writer.flush();

            // Add a upload file section "picture", a JPEG file to the request
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"picture\"; filename=\"").append(picture.getName()).append("\"\r\n");
            writer.append("Content-Type: image/jpeg\r\n\r\n");
            writer.flush();

            InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);
            outputStream.flush();
            inputStream.close();
            writer.append("\r\n");
            writer.flush();

            // End of the multipart
            writer.append("\r\n").flush();
            writer.append("--" + boundary + "--\r\n");
            writer.close();

            if (urlConnection.getResponseCode() == expectedResponseCode) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                response = readStream(in);
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "UnknownHostException, cannot reach " + urlString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
    }

    public static Bitmap downloadBitmap(String urlString, int expectedResponseCode) {
        Bitmap response = null;
        try {
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == expectedResponseCode) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                response = BitmapFactory.decodeStream(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    // convert UTF-8 to internal Java String format
    public static String convertUTF8ToString(String s) throws UnsupportedEncodingException {
        return new String(s.getBytes("ISO-8859-1"), "UTF-8");
    }

    // convert internal Java String format to UTF-8
    public static String convertStringToUTF8(String s) throws UnsupportedEncodingException {
        return new String(s.getBytes("UTF-8"), "ISO-8859-1");
    }
}
