package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ActivityCafeteriaBinding;
import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaListViewModel;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaViewModel;

public class CafeteriaActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Cafeteria cafeteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Intent that started this activity and extract the string
        String message = getIntent().getStringExtra(CafeteriaAdapter.EXTRA_MESSAGE);

        CafeteriaViewModel.Factory factory = new CafeteriaViewModel.Factory(getApplication(), Integer.parseInt(message));
        final CafeteriaViewModel model = new ViewModelProvider(this, factory).get(CafeteriaViewModel.class);

        ActivityCafeteriaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_cafeteria);
        binding.setLifecycleOwner(this);                        // important to observe LiveData!!
        binding.setCafeteriaViewModel(model);
        initActionBar();
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
