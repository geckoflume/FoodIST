package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ListItemDishBinding;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishWithPictures;
import pt.ulisboa.tecnico.cmov.foodist.model.Dish;
import pt.ulisboa.tecnico.cmov.foodist.model.Picture;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishHolder> {
    static final String EXTRA_MESSAGE = "pt.ulisboa.tecnico.cmov.foodist.DISHID";

    private final RequestManager glide;
    private List<DishWithPictures> dishList;

    DishAdapter(final RequestManager glide) {
        this.glide = glide;
    }

    @NonNull
    @Override
    public DishHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemDishBinding dishListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.list_item_dish, parent, false);
        return new DishHolder(dishListItemBinding, this.glide);
    }

    @Override
    public void onBindViewHolder(final DishHolder holder, int position) {
        Dish dish = dishList.get(position).dish;
        holder.listItemDishBinding.setDish(dish);
        if (dishList.get(position).pictures.size() > 0)
            holder.updateWithPicture(dishList.get(position).pictures.get(0));
        else
            holder.updateNoPicture();
    }

    @Override
    public int getItemCount() {
        if (dishList != null)
            return dishList.size();
        else return 0;
    }


    void setDishList(List<DishWithPictures> dishesList) {
        this.dishList = dishesList;
        notifyDataSetChanged();
    }


    class DishHolder extends RecyclerView.ViewHolder {
        private ListItemDishBinding listItemDishBinding;
        private RequestManager glide;
        private ImageView imageView;

        DishHolder(@NonNull ListItemDishBinding listItemDishBinding, RequestManager glide) {
            super(listItemDishBinding.getRoot());
            this.listItemDishBinding = listItemDishBinding;
            this.glide = glide;
            imageView = itemView.findViewById(R.id.imageView_thumbnail);

            itemView.setOnClickListener(view1 -> {
                Intent intent = new Intent(view1.getContext(), DishActivity.class);
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(listItemDishBinding.getDish().getId()));
                listItemDishBinding.getRoot().getContext().startActivity(intent);
            });

        }

        void updateWithPicture(Picture picture) {
            glide.load(ServerFetcher.getPictureUrl(picture.getFilename())).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        public void updateNoPicture() {
            glide.clear(itemView);
            imageView.setVisibility(View.INVISIBLE);
        }
    }
}
