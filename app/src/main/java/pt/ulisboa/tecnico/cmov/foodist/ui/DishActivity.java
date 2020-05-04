package pt.ulisboa.tecnico.cmov.foodist.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;

public class DishActivity extends AppCompatActivity {
    private DishEntity currentDish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);

    }
}
