package pt.ulisboa.tecnico.cmov.foodist.model;

import android.content.Context;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.foodist.R;

public abstract class Status {
    public static final int DEFAULT = 4;

    public static ArrayList<String> getInstance(final Context context) {
        ArrayList<String> statusesList = new ArrayList<>();
        statusesList.add(context.getString(R.string.student));          // 0
        statusesList.add(context.getString(R.string.professor));        // 1
        statusesList.add(context.getString(R.string.researcher));       // 2
        statusesList.add(context.getString(R.string.staff));            // 3
        statusesList.add(context.getString(R.string.general_public));   // 4
        return statusesList;
    }
}
