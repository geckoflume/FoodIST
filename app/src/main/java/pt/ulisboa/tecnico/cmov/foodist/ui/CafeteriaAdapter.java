package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ListItemCafeteriaBinding;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaWithOpeningHours;

public class CafeteriaAdapter extends RecyclerView.Adapter<CafeteriaAdapter.CafeteriaHolder> {
    static final String EXTRA_MESSAGE = "pt.ulisboa.tecnico.cmov.foodist.CAFETERIAID";

    private List<CafeteriaWithOpeningHours> cafeteriaList;
    private int status;

    @NonNull
    @Override
    public CafeteriaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemCafeteriaBinding cafeteriaListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item_cafeteria, parent, false);
        return new CafeteriaHolder(cafeteriaListItemBinding);
    }

    @Override
    public void onBindViewHolder(final CafeteriaHolder holder, int position) {
        CafeteriaWithOpeningHours cafeteriaWithOpeningHours = cafeteriaList.get(position);
        holder.listItemCafeteriaBinding.setCafeteriaWithOpeningHours(cafeteriaWithOpeningHours);
        holder.listItemCafeteriaBinding.setIsOpen(UiUtils.isOpen(cafeteriaWithOpeningHours.openingHours, status));
    }

    @Override
    public int getItemCount() {
        if (cafeteriaList != null)
            return cafeteriaList.size();
        else return 0;
    }


    void setCafeteriaList(List<CafeteriaWithOpeningHours> cafeteriasList) {
        this.cafeteriaList = cafeteriasList;
        notifyDataSetChanged();
    }

    void setStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }


    static class CafeteriaHolder extends RecyclerView.ViewHolder {
        private ListItemCafeteriaBinding listItemCafeteriaBinding;

        CafeteriaHolder(@NonNull ListItemCafeteriaBinding listItemCafeteriaBinding) {
            super(listItemCafeteriaBinding.getRoot());
            this.listItemCafeteriaBinding = listItemCafeteriaBinding;
            itemView.setOnClickListener(view1 -> {
                Intent intent = new Intent(view1.getContext(), CafeteriaActivity.class);
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(listItemCafeteriaBinding.getCafeteriaWithOpeningHours().cafeteria.getId()));
                listItemCafeteriaBinding.getRoot().getContext().startActivity(intent);
            });
        }
    }
}
