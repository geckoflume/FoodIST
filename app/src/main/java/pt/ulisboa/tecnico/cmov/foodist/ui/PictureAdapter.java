package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ListItemPictureBinding;
import pt.ulisboa.tecnico.cmov.foodist.model.Picture;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureHolder> {
    public static final String EXTRA_MESSAGE = "pt.ulisboa.tecnico.cmov.foodist.PICTUREID";

    private List<? extends Picture> picturesList;

    @NonNull
    @Override
    public PictureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemPictureBinding pictureListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.list_item_picture, parent, false);
        return new PictureHolder(pictureListItemBinding);
    }

    @Override
    public void onBindViewHolder(final PictureHolder holder, int position) {
        Picture picture = picturesList.get(position);
        holder.listItemPictureBinding.setPicture(picture);
    }

    @Override
    public int getItemCount() {
        if (picturesList != null)
            return picturesList.size();
        else return 0;
    }


    public void setPicturesList(List<? extends Picture> picturesList) {
        this.picturesList = picturesList;
        notifyDataSetChanged();
    }


    class PictureHolder extends RecyclerView.ViewHolder {
        private ListItemPictureBinding listItemPictureBinding;

        public PictureHolder(@NonNull ListItemPictureBinding listItemPictureBinding) {
            super(listItemPictureBinding.getRoot());
            this.listItemPictureBinding = listItemPictureBinding;
            ImageView image = itemView.findViewById(R.id.imageView_thumbnail);

            //image.setImageURI(FileProvider.getUriForFile(itemView.getContext(), "pt.ulisboa.tecnico.cmov.foodist.fileprovider", new File("dish_1005243876705297712.jpg")));

            /*
            itemView.setOnClickListener(view1 -> {
                Intent intent = new Intent(view1.getContext(), DishActivity.class);
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(listItemDishBinding.getDish().getId()));
                listItemDishBinding.getRoot().getContext().startActivity(intent);
            });
             */

        }
    }
}
