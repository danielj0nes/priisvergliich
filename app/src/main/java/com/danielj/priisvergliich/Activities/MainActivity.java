package com.danielj.priisvergliich.Activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.location.Location;
import android.location.LocationManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danielj.priisvergliich.Controllers.DataParseController;
import com.danielj.priisvergliich.Controllers.UserDBController;
import com.danielj.priisvergliich.Controllers.RequestsController;
import com.danielj.priisvergliich.Models.ProductModel;
import com.danielj.priisvergliich.R;
import com.danielj.priisvergliich.Models.UserModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    /*Class instantiations*/
    UserModel userModel = new UserModel();
    DataParseController dpc = new DataParseController();
    UserDBController dbc = new UserDBController(MainActivity.this);
    RequestsController rc = new RequestsController();
    FusedLocationProviderClient fusedLocationProviderClient;
    /*Const variables*/
    int SEARCH_THRESHOLD = 3;
    public static List<ProductModel> TEMP_PRODUCT_LIST = new ArrayList<>();
    ProductModel TEMP_PRODUCT = new ProductModel();
    /*Helper class to extract bitmaps from image URLs in order to display them in the app*/
    static class LoadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public LoadImage (ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String src = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(src).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
    /*Customised permission handling functionality - particularly needed for precise location*/
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1]
        == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(MainActivity.this, "Permission denied", LENGTH_SHORT).show();
        }
    }
    /*Helper function to get and update the current user location (uses lat/long values).
    * This is called on the click of the location button on the main menu bar.*/
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void getCurrentLocation() {
        LocationManager lm = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && lm.isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location != null) {
                        userModel.setLatitude(location.getLatitude());
                        userModel.setLongitude(location.getLongitude());
                        boolean modified = dbc.modifyUser(userModel);
                        double longitude = userModel.getLongitude();
                        double latitude = userModel.getLatitude();
                        Toast.makeText(
                                MainActivity.this,
                                "Location enabled.\nLatitude: " + latitude + " Longitude: " + longitude
                                        + "\nData saved to database: " + modified,
                                LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Could not get location, please try again",
                                LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(MainActivity.this,
                    "Please enable location settings",
                    LENGTH_SHORT).show();
        }
    }
    /*Helper function to transition to the comparison list activity*/
    public void openActivityComparison() {
        Intent intent = new Intent(this, ComparisonActivity.class);
        startActivity(intent);
    }
    /*Helper function to transition to the saved comparisons list activity*/
    public void openSavedComparisons() {
        Intent intent = new Intent(this, SavedComparisonsActivity.class);
        startActivity(intent);
    }
    /*Given a view, will display the popout menu for a given product in the product list*/
    public void comparisonMenuShow(View v) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.ComparisonMenuTheme);
        PopupMenu comparisonMenu = new PopupMenu(ctw, v);
        comparisonMenu.setOnMenuItemClickListener(this);
        comparisonMenu.inflate(R.menu.comparison_menu);
        comparisonMenu.show();
    }
    /*Helper function that utilises functions from the Requests Controller in order to
     * obtain and display results. As the name suggests, this is called on search submit.*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void callSearch(String query) {
        List<ProductModel> products = new ArrayList<>();
        ProgressBar loadingBar = findViewById(R.id.pb_progressBar);
        loadingBar.setVisibility(View.VISIBLE);
        rc.getMigrosProducts(query, resultMigros -> {
            products.addAll(resultMigros);
            rc.getCoopProducts(query, resultCoop -> {
                products.addAll(resultCoop);
                ProductAdapter adapter = new ProductAdapter(MainActivity.this,
                        dpc.sortRelevance(products, SEARCH_THRESHOLD));
                MainActivity.this.runOnUiThread(() -> {
                    ListView listView = findViewById(R.id.lv_productList);
                    listView.setAdapter(adapter);
                    loadingBar.setVisibility(View.GONE);
                });
            });
        });
    }
    /*Simple listener for when a menu item in the comparison popout is selected*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addComparison:
                TEMP_PRODUCT_LIST.add(TEMP_PRODUCT);
                findViewById(R.id.btn_goToComparisonList).setVisibility(View.VISIBLE);
                Toast.makeText(
                        MainActivity.this,
                        "Product successfully added to unsaved comparison list",
                        LENGTH_SHORT).show();
                return true;
            case R.id.specificSearch:
                callSearch(TEMP_PRODUCT.getProductName() + " " + TEMP_PRODUCT.getProductInfo());
                Toast.makeText(
                        MainActivity.this,
                        "Searching...",
                        LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
    /*Adapter class that translates an item of class ProductModel into an item of the list view
    * This is used each time a search is made to display the correctly search items returned
    * from the parsed request.*/
    static class ProductAdapter extends ArrayAdapter<ProductModel> {
        public ProductAdapter(Context context, List<ProductModel> products) {
            super(context, 0, products);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ProductModel product = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
            }
            ImageView ivImageSrc = convertView.findViewById(R.id.iv_productImage);
            if (product.getImageAsBitmap() != null) {
                // If we already have the bitmap, we can save time by rendering this rather than converting
                ivImageSrc.setImageBitmap(product.getImageAsBitmap());
            } else {
                // Otherwise create bitmaps from URLs asynchronously
                LoadImage loadImage = new LoadImage(ivImageSrc);
                try {
                    Bitmap result = loadImage.execute(product.getImageSrc()).get();
                    product.setImageBitmap(result);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            TextView tvProductName = convertView.findViewById(R.id.tv_productName);
            TextView tvProductPrice = convertView.findViewById(R.id.tv_productPrice);
            TextView tvProductInfo = convertView.findViewById(R.id.tv_productInfo);
            TextView tvProductOrigin = convertView.findViewById(R.id.tv_productOrigin);
            tvProductName.setText(product.getProductName());
            tvProductPrice.setText(product.getProductPrice());
            tvProductInfo.setText(product.getProductInfo());
            tvProductOrigin.setText(product.getProductOrigin());
            return convertView;
        }
    }
    /*Standard onCreate method, the fused location provider client is set and initialised here
    * with the main activity thread.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                MainActivity.this
        );
    }
    /*Simple helper function to build store locator URL and then access it via intents*/
    Boolean storeFinder(String store, double latitude, double longitude) {
        String url = "https://www.google.com/maps/search/";
        switch (store) {
            case "Migros":
                url = url + "migros/@";
                break;
            case "Coop":
                url = url + "coop/@";
                break;
            default:
                return false;
        }
        url = url + latitude + "," + longitude + ",13z/data=!3m1!4b1";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
        return true;
    }
    /*Initialisations, method calls, and listeners for the majority of the core functionality.*/
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menubar, menu); // Toolbar
        // UI element initialisations
        MenuItem locationButton = menu.findItem(R.id.cur_location);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        Button receiptButton = findViewById(R.id.btn_receiptScanner);
        Button savedComparisonsButton = findViewById(R.id.btn_savedComparisons);
        Button migrosFinderButton = findViewById(R.id.btn_migrosLocator);
        Button coopFinderButton = findViewById(R.id.btn_coopLocator);
        ListView listView = findViewById(R.id.lv_productList);
        ProgressBar loadingBar = findViewById(R.id.pb_progressBar);
        loadingBar.setVisibility(View.GONE);
        // Comparison UI functionality
        Button comparisonButton = findViewById(R.id.btn_goToComparisonList);
        comparisonButton.setVisibility(View.GONE);
        comparisonButton.setOnClickListener(v -> openActivityComparison());
        // Search functionality
        MenuItem.OnActionExpandListener searchListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                locationButton.setVisible(false);
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                locationButton.setVisible(true);
                // Once the search is cleared, clear the product list and hide the compare button
                comparisonButton.setVisibility(View.GONE);
                TEMP_PRODUCT_LIST = new ArrayList<>();
                return true;
            }
        };
        menu.findItem(R.id.search).setOnActionExpandListener(searchListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search for a product...");
        listView.setOnItemClickListener((parent, view, position, id) -> {
            TEMP_PRODUCT = (ProductModel) parent.getAdapter().getItem(position);
            comparisonMenuShow(parent.getAdapter().getView(position, view, parent));
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                callSearch(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    listView.setVisibility(View.GONE);
                    receiptButton.setVisibility(View.VISIBLE);
                    savedComparisonsButton.setVisibility(View.VISIBLE);
                    migrosFinderButton.setVisibility(View.VISIBLE);
                    coopFinderButton.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    receiptButton.setVisibility(View.GONE);
                    savedComparisonsButton.setVisibility(View.GONE);
                    migrosFinderButton.setVisibility(View.GONE);
                    coopFinderButton.setVisibility(View.GONE);
                }
                return true;
            }
        });
        /*Location button listener - ensures that the right permissions are enabled and handles
        * the cases where they are not. If permissions are granted, the getCurrentLocation() method
        * is called to handle the additional functionality.*/
        MenuItem.OnMenuItemClickListener locationBtnListener = item -> {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Automatic location is not supported",
                            LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
            }
            return true;
        };
        menu.findItem(R.id.cur_location).setOnMenuItemClickListener(locationBtnListener);
        // Saved comparison lists
        savedComparisonsButton.setOnClickListener(v -> {
            openSavedComparisons();
        });
        // Nearest store finder functionality
        migrosFinderButton.setOnClickListener(v -> {
            double latitude = userModel.getLatitude();
            double longitude = userModel.getLongitude();
            storeFinder("Migros", latitude, longitude);
        });
        coopFinderButton.setOnClickListener(v -> {
            double latitude = userModel.getLatitude();
            double longitude = userModel.getLongitude();
            storeFinder("Coop", latitude, longitude);
        });
        return true;
    }
}