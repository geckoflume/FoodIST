package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;

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
    public static void showSnackbar(View v, final int mainTextStringId, final int actionStringId,
                                    final int duration, View.OnClickListener listener) {
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

    public static boolean isOpen(Cafeteria cafeteria) {
        // TODO
        return false;
    }
}
