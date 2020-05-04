package pt.ulisboa.tecnico.cmov.foodist.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.DishViewModel;


public class DishActivity extends AppCompatActivity {
    private DishEntity currentDish;
    private DishViewModel dishViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dish);

        String message = getIntent().getStringExtra(DishAdapter.EXTRA_MESSAGE);

        DishViewModel.Factory factory = new DishViewModel.Factory(getApplication(), Integer.parseInt(message));
        dishViewModel = new ViewModelProvider(this, factory).get(DishViewModel.class);

        dishViewModel.getDish().observe(this, dishEntity -> {
            currentDish = dishEntity;
        });

        String t = currentDish.getName();

        Toast.makeText(this, t ,Toast.LENGTH_SHORT).show(); // to show if we have the good dish

    }
}
