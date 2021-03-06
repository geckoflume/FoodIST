package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.OpeningHoursEntity;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;

public abstract class UiUtils {
    /**
     * Shows a {@link Snackbar}.
     *
     * @param v                The root view to display the Snackbar in.
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param duration         The duration the Snackbar should be displayed.
     * @param listener         The listener associated with the Snackbar action.
     */
    public static void showSnackbar(View v, final int mainTextStringId, final int actionStringId, final int duration, View.OnClickListener listener) {
        Snackbar.make(v, mainTextStringId, duration).setAction(actionStringId, listener).show();
    }

    private static int ceilIntDivision(int a, int b) {
        return (a + b - 1) / b;
    }

    public static String formatTime(int time, String sec, String min, String hours, String days) {
        if (time < 60)
            return String.format(sec, time);
        else if (time < 3600)
            return String.format(min, ceilIntDivision(time, 60));
        else if (time < 86400)
            return String.format(hours, ceilIntDivision(time, 3600));
        else
            return String.format(days, ceilIntDivision(time, 86400));
    }

    public static String formatDistance(int distance, String m, String km) {
        if (distance < 1000)
            return String.format(m, distance);
        else
            return String.format(km, ceilIntDivision(distance, 1000));
    }

    public static boolean isOpen(List<OpeningHoursEntity> openingHours, int status) {
        for (OpeningHoursEntity hour : openingHours) {
            if (hour.getStatus() == status && hour.isOpen()) {
                return true;
            }
        }
        return false;
    }

    public static String formatPrice(double price, String currencyFormat) {
        return String.format(currencyFormat, price);
    }

    public static String formatShareDish(Dish dish, String cafeteriaName, String currencyFormat, String shareFormat) {
        String formattedPrice = formatPrice(dish.getPrice(), currencyFormat);
        return String.format(shareFormat, dish.getName(), formattedPrice, cafeteriaName);
    }

    public static String formatShareCafeteria(CafeteriaEntity cafeteria, String shareFormat) {
        String mapsUrl = "http://maps.google.com/maps?&daddr="
                + cafeteria.getLatitude() + ","
                + cafeteria.getLongitude();
        return String.format(shareFormat, cafeteria.getName(), mapsUrl);
    }
}
