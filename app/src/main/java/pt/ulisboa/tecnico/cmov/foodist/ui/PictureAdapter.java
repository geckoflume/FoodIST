package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.cache.Cache;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ListItemPictureBinding;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.PictureEntity;
import pt.ulisboa.tecnico.cmov.foodist.model.Picture;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureHolder> {
    public static final String EXTRA_MESSAGE = "pt.ulisboa.tecnico.cmov.foodist.PICTUREID";

    private List<? extends Picture> picturesList;

    @NonNull
    @Override
    public PictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemPictureBinding pictureListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.list_item_picture, parent, false);
        return new PictureHolder(pictureListItemBinding);
    }

    @Override
    public void onBindViewHolder(final PictureHolder holder, int position) {
        Picture picture = picturesList.get(position);
        holder.listItemPictureBinding.setPicture(picture);
        holder.updateWithPicture(picturesList.get(position));
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

            itemView.findViewById(R.id.button_delete_picture).setOnClickListener(view1 -> {
                DishActivity host = (DishActivity) itemView.getContext();
                host.deletePicture((PictureEntity) listItemPictureBinding.getPicture());
            });
        }

        public void updateWithPicture(Picture picture) {
            ImageView imageView = itemView.findViewById(R.id.imageView_thumbnail);
            Context context = imageView.getContext();

            ((BasicApp) context.getApplicationContext()).networkIO().execute(() -> {
                Bitmap bitmap = Cache.getInstance(context).downloadPictureIfNeeded(picture.getFilename());
                imageView.post(() -> imageView.setImageBitmap(bitmap));
            });
        }
    }
}
