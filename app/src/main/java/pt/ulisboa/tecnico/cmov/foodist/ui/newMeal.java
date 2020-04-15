package pt.ulisboa.tecnico.cmov.foodist.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pt.ulisboa.tecnico.cmov.foodist.model.Meal;

import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class newMeal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meal);
    }

    public void createMeal(View view) {
        EditText editTextName = (EditText) findViewById(R.id.set_name);
        String name = editTextName.getText().toString();
        EditText editTextPrice = (EditText) findViewById(R.id.set_price);
        String price = editTextPrice.getText().toString();

        Intent intent = getIntent();
        int id = intent.getIntExtra("IdCafet", 0);

        Meal meal = new Meal(name, price, id);

        Toast.makeText(this, "Add the meal" + meal.getName() + " at " + meal.getPrice() , Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
