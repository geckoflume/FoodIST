package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DishWithPictures {
    @Embedded
    public DishEntity dish;
    @Relation(
            parentColumn = "id",
            entityColumn = "dish_id"
    )
    public List<PictureEntity> pictures;
}
