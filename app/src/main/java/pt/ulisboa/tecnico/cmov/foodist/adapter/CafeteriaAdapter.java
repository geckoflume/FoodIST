package pt.ulisboa.tecnico.cmov.foodist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;

public class CafeteriaAdapter extends RecyclerView.Adapter<CafeteriaAdapter.CafeteriaHolder> {
    private Context context;
    private List<Cafeteria> cafeterias;

    public CafeteriaAdapter(Context context, List<Cafeteria> cafeterias) {
        this.context = context;
        this.cafeterias = cafeterias;
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
        return cafeterias.size();
    }

    class CafeteriaHolder extends RecyclerView.ViewHolder {
        private TextView name;

        CafeteriaHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textView_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, name.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        void setDetails(Cafeteria cafeteria) {
            name.setText(cafeteria.getName());
        }
    }

}
