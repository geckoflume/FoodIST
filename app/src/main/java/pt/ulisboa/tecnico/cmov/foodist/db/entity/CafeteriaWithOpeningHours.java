package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CafeteriaWithOpeningHours {
    @Embedded
    public CafeteriaEntity cafeteria;
    @Relation(
            parentColumn = "id",
            entityColumn = "cafeteria_id"
    )
    public List<OpeningHoursEntity> openingHours;
}
