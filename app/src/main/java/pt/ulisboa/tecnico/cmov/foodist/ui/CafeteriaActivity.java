package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaListViewModel;

public class CafeteriaActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Cafeteria cafeteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafeteria);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(CafeteriaAdapter.EXTRA_MESSAGE);

        toolbar = findViewById(R.id.toolbar);
        initActionBar(message);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(message);

        CafeteriaAdapter adapterCafeterias = new CafeteriaAdapter(this);
        CafeteriaListViewModel mCafeteriaListViewModel = new ViewModelProvider(this).get(CafeteriaListViewModel.class);
        mCafeteriaListViewModel.getCafeterias().observe(this, adapterCafeterias::setCafeterias);
    }

    private void initActionBar(String title) {
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
