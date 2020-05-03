package pt.ulisboa.tecnico.cmov.foodist.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public abstract class NetUtils {
    private static final String TAG = NetUtils.class.getSimpleName();

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

    public static String get(String urlString) {
        String response = "";
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response = readStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
    }

    public static String postJson(String urlString, String json, int expectedResponseCode) {
        String response = null;
        HttpsURLConnection urlConnection = null;

        try {
            json = convertStringToUTF8(json);
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            if (urlConnection.getResponseCode() == expectedResponseCode)
                response = readStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
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
