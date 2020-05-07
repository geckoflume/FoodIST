package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ActivityDishBinding;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.PictureEntity;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.DishViewModel;

public class DishActivity extends AppCompatActivity {
    private static final String TAG = DishActivity.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 1;

    private DishViewModel dishViewModel;
    private String picturePath;
    private Locale currentLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDishBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_dish);

        currentLocale = getResources().getConfiguration().locale;

        int dishId = Integer.parseInt(getIntent().getStringExtra(DishAdapter.EXTRA_MESSAGE));

        DishViewModel.Factory factory = new DishViewModel.Factory(getApplication(), dishId);
        dishViewModel = new ViewModelProvider(this, factory).get(DishViewModel.class);

        binding.setLifecycleOwner(this);                        // important to observe LiveData!!
        binding.setDishViewModel(dishViewModel);
        initActionBar();

        RecyclerView recyclerViewDishes = this.findViewById(R.id.recyclerView_pictures);
        PictureAdapter adapterPictures = new PictureAdapter(Glide.with(this));
        recyclerViewDishes.setHasFixedSize(true);
        recyclerViewDishes.addItemDecoration(new GridSpacingItemDecoration(((GridLayoutManager) recyclerViewDishes.getLayoutManager()).getSpanCount(), 50));
        recyclerViewDishes.setAdapter(adapterPictures);

        dishViewModel.getDish().observe(this, dishWithPictures -> {
            if (dishWithPictures != null) {
                adapterPictures.setPicturesList(dishWithPictures.pictures);
            } else {
                // In this case, the dish was deleted by deleteDish action
                Toast.makeText(this, dishViewModel.getDish().getValue().dish.getName() + " successfully deleted!", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        });
        dishViewModel.updatePictures();

        CheckBox translate = findViewById(R.id.checkBox_translation);
        translate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                dishViewModel.translate(currentLocale.toString(), getString(R.string.google_cloud_key));
            else
                dishViewModel.resetDishName();
        });
    }

    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        picturePath = image.getAbsolutePath();
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
            Log.d(TAG, "Successfully took picture, saved in " + picturePath);
            Toast.makeText(this, "Successfully took picture, sending it to the server...", Toast.LENGTH_SHORT).show();
            ((BasicApp) getApplication()).networkIO().execute(() ->
                    dishViewModel.insertPicture(picturePath));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dish_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_dish) {
            deleteDish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteDish() {
        dishViewModel.deleteDish();
    }

    public void deletePicture(PictureEntity picture) {
        dishViewModel.deletePicture(picture);
    }
}
