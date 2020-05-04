package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ListItemDishBinding;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishHolder> {
    public static final String EXTRA_MESSAGE = "pt.ulisboa.tecnico.cmov.foodist.DISHID";

    private List<? extends Dish> dishList;

    @NonNull
    @Override
    public DishHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemDishBinding dishListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.list_item_dish, parent, false);
        return new DishHolder(dishListItemBinding);
    }

    @Override
    public void onBindViewHolder(final DishHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.listItemDishBinding.setDish(dish);
    }

    @Override
    public int getItemCount() {
        if (dishList != null)
            return dishList.size();
        else return 0;
    }


    public void setDishList(List<? extends Dish> dishesList) {
        this.dishList = dishesList;
        notifyDataSetChanged();
    }


    class DishHolder extends RecyclerView.ViewHolder {
        private ListItemDishBinding listItemDishBinding;

        public DishHolder(@NonNull ListItemDishBinding listItemDishBinding) {
            super(listItemDishBinding.getRoot());
            this.listItemDishBinding = listItemDishBinding;

            // TODO: onclick on a dish, display details, pictures
            itemView.setOnClickListener(view1 -> {
                Intent intent = new Intent(view1.getContext(), DishActivity.class); //Wait what ? --> Do the Dish Acti for the next
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(listItemDishBinding.getDish().getId()));
                listItemDishBinding.getRoot().getContext().startActivity(intent);
            });

        }
    }
}
