package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerParser;

public class NewDishActivity extends AppCompatActivity {
    private static final String TAG = NewDishActivity.class.getSimpleName();

    private DataRepository mRepository;
    private TextInputLayout textInputLayout_name;
    private TextInputEditText textInputEditText_name;
    private TextInputLayout textInputLayout_price;
    private TextInputEditText textInputEditText_price;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dish);
        initActionBar();
        mRepository = ((BasicApp) getApplication()).getRepository();

        textInputLayout_name = findViewById(R.id.textInputLayout_name);
        textInputEditText_name = findViewById(R.id.textInputEditText_name);
        textInputLayout_price = findViewById(R.id.textInputLayout_price);
        textInputEditText_price = findViewById(R.id.textInputEditText_price);

        // Form validation
        textInputEditText_name.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (textInputLayout_name.getError() != null)
                    textInputLayout_name.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // Listeners
        textInputEditText_price.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (textInputLayout_price.getError() != null)
                    textInputLayout_price.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    public void createDish(View view) {
        // fetch name and check if it is valid
        String name = textInputEditText_name.getText().toString();
        if (!isValidName(name))
            textInputLayout_name.setError(getString(R.string.please_enter_valid_dish));

        // fetch price and check if it is valid
        String priceStr = textInputEditText_price.getText().toString();
        if (!isValidPrice(priceStr))
            textInputLayout_price.setError(getString(R.string.please_enter_valid_price));

        // save the new values if they are valid
        if (isValidName(name) && isValidPrice(priceStr)) {
            int id = getIntent().getIntExtra("cafeteriaId", 0);

            DishEntity dish = new DishEntity(name, Double.parseDouble(priceStr), id);
            ServerParser serverParser = new ServerParser();
            ((BasicApp) getApplication()).networkIO().execute(() -> {
                String responseDish = ServerFetcher.insertDish(dish);
                DishEntity completeDish = serverParser.parseDish(responseDish);
                if (completeDish != null) {
                    mRepository.insertDish(completeDish);
                    view.post(() -> { // run this on main thread
                        Toast.makeText(this, String.format(getString(R.string.toast_added_dish), dish.getName(), dish.getPrice()), Toast.LENGTH_SHORT).show();
                    });
                    setResult(RESULT_OK);
                } else {
                    Log.e(TAG, "Unable to add the dish");
                    setResult(RESULT_CANCELED);
                }
            });

            this.finish();
        }
    }

    private boolean isValidName(CharSequence target) {
        return target != null && target.length() > 0;
    }

    private boolean isValidPrice(CharSequence target) {
        return target != null && target.length() > 0 && Double.parseDouble(target.toString()) >= 0;
    }
}
