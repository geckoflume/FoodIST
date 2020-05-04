package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ActivityDishBinding;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.net.ServerFetcher;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.DishViewModel;

public class DishActivity extends AppCompatActivity {
    private static final String TAG = DishActivity.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 1;

    private Toolbar toolbar;
    private int dishId;
    private DishEntity currentDish;
    private DishViewModel dishViewModel;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDishBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_dish);

        dishId = Integer.parseInt(getIntent().getStringExtra(DishAdapter.EXTRA_MESSAGE));

        DishViewModel.Factory factory = new DishViewModel.Factory(getApplication(), dishId);
        dishViewModel = new ViewModelProvider(this, factory).get(DishViewModel.class);

        binding.setLifecycleOwner(this);                        // important to observe LiveData!!
        binding.setDishViewModel(dishViewModel);
        initActionBar();

        dishViewModel.getDish().observe(this, dishEntity -> {
            currentDish = dishEntity;
            String t = currentDish.getName();
            Toast.makeText(this, t, Toast.LENGTH_SHORT).show(); // to show if we have the good dish
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "dish_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void newPicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "pt.ulisboa.tecnico.cmov.foodist.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG, "Successfully took picture, saved in " + currentPhotoPath);
            ((BasicApp) getApplication()).networkIO().execute(() -> {
                ServerFetcher serverFetcher = new ServerFetcher();
                String response = serverFetcher.insertPicture(dishId, currentPhotoPath);
                Log.d(TAG, response);
            });
        }
    }
}
