package pt.ulisboa.tecnico.cmov.foodist.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public abstract class NetUtils {
    public static String download(String urlString) {
        String response = "";
        InputStream iStream = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            // Creating an http connection
            urlConnection = (HttpsURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading response from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            response = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (iStream != null)
                    iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return response;
    }
}
