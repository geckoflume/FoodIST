package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Language;

public class LanguageAdapter extends ArrayAdapter<Language> {

    public LanguageAdapter(Context context, ArrayList<Language> items) {
        super(context, 0, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Language item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.layout_drop_status, parent, false);
        }

        if (item != null) {
            TextView tvName = convertView.findViewById(R.id.textview);
            tvName.setText(item.toString());
        }

        return convertView;
    }
}