package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class UiUtils {

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

    public static String formatTime(double time, String ltOne, String gtOne) {
        return (time < 1 ? ltOne : gtOne);
    }
}
