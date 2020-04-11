package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;

public class CafeteriaAdapter extends RecyclerView.Adapter<CafeteriaAdapter.CafeteriaHolder> {
    public static final String EXTRA_MESSAGE = "pt.ulisboa.tecnico.cmov.foodist.CAFETERIA";

    private Context context;
    private List<? extends Cafeteria> cafeterias;

    public CafeteriaAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CafeteriaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cafeteria, parent, false);
        return new CafeteriaHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CafeteriaHolder holder, int position) {
        Cafeteria cafeteria = cafeterias.get(position);
        holder.setDetails(cafeteria);
    }

    @Override
    public int getItemCount() {
        if (cafeterias != null)
            return cafeterias.size();
        else return 0;
    }


    public void setCafeterias(List<? extends Cafeteria> cafeteriasList) {
        this.cafeterias = cafeteriasList;
        notifyDataSetChanged();
    }


    class CafeteriaHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private Cafeteria cafeteria;

        CafeteriaHolder(View view) {
            super(view);
            this.name = view.findViewById(R.id.textView_name);
            itemView.setOnClickListener(view1 -> {
                Intent intent = new Intent(view1.getContext(), CafeteriaActivity.class);
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(this.cafeteria.getId()));
                context.startActivity(intent);
            });
        }

        void setDetails(Cafeteria cafeteria) {
            this.cafeteria = cafeteria;
            this.name.setText(cafeteria.getName());
        }
    }
}
