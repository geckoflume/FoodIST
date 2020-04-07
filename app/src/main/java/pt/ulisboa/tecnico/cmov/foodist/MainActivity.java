package pt.ulisboa.tecnico.cmov.foodist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.adapter.CafeteriaAdapter;
import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCafeterias;
    private CafeteriaAdapter adapterCafeterias;
    private List<Cafeteria> cafeteriaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner_campus);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterCampus = ArrayAdapter.createFromResource(this, R.array.campuses_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterCampus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapterCampus);

        recyclerViewCafeterias = findViewById(R.id.recyclerView_cafeterias);
        cafeteriaList = new ArrayList<>();
        //recyclerViewCafeterias.setLayoutManager(new LinearLayoutManager(this));
        //recyclerViewCafeterias.setItemAnimator(new DefaultItemAnimator());
        adapterCafeterias = new CafeteriaAdapter(this, cafeteriaList);
        recyclerViewCafeterias.setAdapter(adapterCafeterias);
        populateCafeterias();
    }

    private void populateCafeterias() {
        Cafeteria c = new Cafeteria("Main Buliding Bar", 38.736616, -9.139603, 1);
        cafeteriaList.add(c);
        c = new Cafeteria("Civil Building Bar", 38.737071, -9.140010, 1);
        cafeteriaList.add(c);
        c = new Cafeteria("Civil Building Canteen", 38.737725, -9.140466, 1);
        cafeteriaList.add(c);
        c = new Cafeteria("Sena Bar and Restaurant – North Tower", 38.737712, -9.138635, 1);
        cafeteriaList.add(c);
        c = new Cafeteria("Mechanics Building II Bar", 38.737333, -9.137252, 1);
        cafeteriaList.add(c);
        c = new Cafeteria("Canteen", 38.736377, -9.136980, 1);
        cafeteriaList.add(c);
        adapterCafeterias.notifyDataSetChanged();
    }
}
