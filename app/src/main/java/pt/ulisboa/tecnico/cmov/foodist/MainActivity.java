package pt.ulisboa.tecnico.cmov.foodist;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.ui.CafeteriaAdapter;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaListViewModel;

public class MainActivity extends AppCompatActivity {
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

        RecyclerView recyclerViewCafeterias = findViewById(R.id.recyclerView_cafeterias);
        //recyclerViewCafeterias.setLayoutManager(new LinearLayoutManager(this));
        //recyclerViewCafeterias.setItemAnimator(new DefaultItemAnimator());
        CafeteriaAdapter adapterCafeterias = new CafeteriaAdapter(this);
        recyclerViewCafeterias.setAdapter(adapterCafeterias);

        // Get a new or existing ViewModel from the ViewModelProvider.
        CafeteriaListViewModel mCafeteriaListViewModel = new ViewModelProvider(this).get(CafeteriaListViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mCafeteriaListViewModel.getCafeterias().observe(this, new Observer<List<CafeteriaEntity>>() {
            @Override
            public void onChanged(@Nullable final List<CafeteriaEntity> cafeterias) {
                // Update the cached copy of the cafeterias in the adapter.
                adapterCafeterias.setCafeterias(cafeterias);
            }
        });

    }

}
