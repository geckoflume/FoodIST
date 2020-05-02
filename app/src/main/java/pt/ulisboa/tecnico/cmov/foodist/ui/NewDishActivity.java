package pt.ulisboa.tecnico.cmov.foodist.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;

import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class NewDishActivity extends AppCompatActivity {

    private DataRepository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dish);
        mRepository = ((BasicApp)getApplication()).getRepository();
    }

    public void createDish(View view) {
        EditText editTextName = findViewById(R.id.set_name);
        String name = editTextName.getText().toString();
        EditText editTextPrice = findViewById(R.id.set_price);
        String price = editTextPrice.getText().toString();

        Intent intent = getIntent();
        int id = intent.getIntExtra("IdCafet", 0);

        DishEntity dish = new DishEntity(name, price, id);
        ((BasicApp) getApplication()).diskIO().execute(() -> mRepository.insertDish(dish));

        Toast.makeText(this, "Add the dish" + dish.getName() + " at " + dish.getPrice() , Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
