package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ListItemCafeteriaBinding;
import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;

public class CafeteriaAdapter extends RecyclerView.Adapter<CafeteriaAdapter.CafeteriaHolder> {
    public static final String EXTRA_MESSAGE = "pt.ulisboa.tecnico.cmov.foodist.CAFETERIA";

    private List<? extends Cafeteria> cafeteriaList;

    @NonNull
    @Override
    public CafeteriaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemCafeteriaBinding cafeteriaListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.list_item_cafeteria, parent, false);
        return new CafeteriaHolder(cafeteriaListItemBinding);
    }

    @Override
    public void onBindViewHolder(final CafeteriaHolder holder, int position) {
        Cafeteria cafeteria = cafeteriaList.get(position);
        holder.listItemCafeteriaBinding.setCafeteria(cafeteria);
    }

    @Override
    public int getItemCount() {
        if (cafeteriaList != null)
            return cafeteriaList.size();
        else return 0;
    }


    public void setCafeteriaList(List<? extends Cafeteria> cafeteriasList) {
        this.cafeteriaList = cafeteriasList;
        notifyDataSetChanged();
    }


    class CafeteriaHolder extends RecyclerView.ViewHolder {
        private ListItemCafeteriaBinding listItemCafeteriaBinding;

        public CafeteriaHolder(@NonNull ListItemCafeteriaBinding listItemCafeteriaBinding) {
            super(listItemCafeteriaBinding.getRoot());
            this.listItemCafeteriaBinding = listItemCafeteriaBinding;
            itemView.setOnClickListener(view1 -> {
                Intent intent = new Intent(view1.getContext(), CafeteriaActivity.class);
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(listItemCafeteriaBinding.getCafeteria().getId()));
                listItemCafeteriaBinding.getRoot().getContext().startActivity(intent);
                // Check if we're running on Android 5.0 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Apply activity transition
                    // TODO: https://developer.android.com/training/transitions/start-activity https://medium.com/@jim.zack.hu/android-inbox-material-transitions-for-recyclerview-71fc7326bcb5
                } else {
                    // Swap without transition
                }
            });
        }
    }
}
