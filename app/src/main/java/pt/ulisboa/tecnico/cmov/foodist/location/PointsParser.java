package pt.ulisboa.tecnico.cmov.foodist.location;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.List;

public class PointsParser extends AsyncTask<String, Integer, List<LatLng>> {
    private RouteFetchedCallback taskCallback;

    public PointsParser(Context mContext) {
        this.taskCallback = (RouteFetchedCallback) mContext;
    }

    // Parses the data in a background thread
    @Override
    protected List<LatLng> doInBackground(String... jsonData) {
        JSONObject jObject;
        List<LatLng> route = null;
        try {
            jObject = new JSONObject(jsonData[0]);
            DataParser parser = new DataParser();
            // Starts parsing data
            route = parser.parse(jObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return route;
    }

    // Executes drawing in the UI thread after the parsing
    @Override
    protected void onPostExecute(List<LatLng> points) {
        PolylineOptions polyline = new PolylineOptions()
                .addAll(points)
                .width(20)
                .color(Color.BLUE);
        taskCallback.onRouteFetched(polyline);
    }
}
