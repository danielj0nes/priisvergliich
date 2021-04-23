package com.danielj.priisvergliich;
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
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.r0adkll.slidr.Slidr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    /*Class instantiations*/
    UserModel userModel = new UserModel();
    DataParseController dpc = new DataParseController();
    DatabaseController dbc = new DatabaseController(MainActivity.this);
    RequestsController rc = new RequestsController();
    FusedLocationProviderClient fusedLocationProviderClient;
    /*Const variables*/
    int SEARCH_THRESHOLD = 3;
    List<ProductModel> TEMP_LIST = new ArrayList<>();
    ProductModel TEMP_PRODUCT = new ProductModel();

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
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
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Permissions handling
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1]
        == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(MainActivity.this, "Permission denied", LENGTH_SHORT).show();
        }
    }
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
                        boolean t = dbc.modify(userModel);
                        double longitude = userModel.getLongitude();
                        double latitude = userModel.getLatitude();
                        Toast.makeText(
                                MainActivity.this,
                                longitude + " " + latitude + " " + t,
                                LENGTH_SHORT).show();
                        System.out.println("Success");
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
    public void openActivityComparison() {
        Intent intent = new Intent(this, ComparisonActivity.class);
        startActivity(intent);
    }
    public void comparisonMenuShow(View v) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.ComparisonMenuTheme);
        PopupMenu comparisonMenu = new PopupMenu(ctw, v);
        comparisonMenu.setOnMenuItemClickListener(this);
        comparisonMenu.inflate(R.menu.comparison_menu);
        comparisonMenu.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_comparison:
                System.out.println(TEMP_PRODUCT);
                findViewById(R.id.btn_goToComparisonList).setVisibility(View.VISIBLE);
                return true;
            default:
                return false;
        }
    }
    class ProductAdapter extends ArrayAdapter<ProductModel> {
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
            TextView tvProductName = convertView.findViewById(R.id.tv_productName);
            TextView tvProductPrice = convertView.findViewById(R.id.tv_productPrice);
            TextView tvProductInfo = convertView.findViewById(R.id.tv_productInfo);
            TextView tvProductOrigin = convertView.findViewById(R.id.tv_productOrigin);
            ImageView ivImageSrc = convertView.findViewById(R.id.iv_productImage);
            LoadImage loadImage = new LoadImage(ivImageSrc);
            loadImage.execute(product.getImageSrc());
            tvProductName.setText(product.getProductName());
            tvProductPrice.setText(product.getProductPrice());
            tvProductInfo.setText(product.getProductInfo());
            tvProductOrigin.setText(product.getProductOrigin());
            return convertView;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                MainActivity.this
        );
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menubar, menu); // Toolbar
        MenuItem locationButton = menu.findItem(R.id.cur_location);
        Button receiptButton = findViewById(R.id.btn_receiptScanner);
        Button savedComparisonsButton = findViewById(R.id.btn_savedComparisons);
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
                    comparisonButton.setVisibility(View.GONE);
                    savedComparisonsButton.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    receiptButton.setVisibility(View.GONE);
                    savedComparisonsButton.setVisibility(View.GONE);

                }
                return true;
            }
            public void callSearch(String query) {
                List<ProductModel> products = new ArrayList<>();
                loadingBar.setVisibility(View.VISIBLE);
                rc.getMigrosProducts(query, resultMigros -> {
                    products.addAll(resultMigros);
                    rc.getCoopProducts(query, resultCoop -> {
                        products.addAll(resultCoop);
                        ProductAdapter adapter = new ProductAdapter(MainActivity.this, dpc.sortRelevance(products, SEARCH_THRESHOLD));
                        MainActivity.this.runOnUiThread(() -> {
                            listView.setAdapter(adapter);
                            loadingBar.setVisibility(View.GONE);
                        });
                    });
                });
            }
        });
        // Location functionality
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
        return true;
    }
}