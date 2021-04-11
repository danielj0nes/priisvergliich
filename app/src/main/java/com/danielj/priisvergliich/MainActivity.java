package com.danielj.priisvergliich;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    UserModel userModel = new UserModel();
    DatabaseController dbc = new DatabaseController(MainActivity.this);
    FusedLocationProviderClient fusedLocationProviderClient;
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
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            userModel.setLatitude(location.getLatitude());
                            userModel.setLongitude(location.getLongitude());
                            boolean t = dbc.modify(userModel);
                            double longitude = userModel.getLongitude();
                            double latitude = userModel.getLatitude();
                            Toast.makeText(
                                    MainActivity.this,
                                    String.valueOf(longitude) + " " + String.valueOf(latitude) + " " + String.valueOf(t),
                                    LENGTH_SHORT).show();
                            System.out.println("Success");
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Could not get location, please try again",
                                    LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Toast.makeText(MainActivity.this,
                    "Please enable location settings",
                    LENGTH_SHORT).show();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pvg_menu0, menu); // Toolbar
        MenuItem locationButton = menu.findItem(R.id.cur_location);
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

        // Location functionality
        MenuItem.OnMenuItemClickListener locationBtnListener = new MenuItem.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
            }
        };
        menu.findItem(R.id.cur_location).setOnMenuItemClickListener(locationBtnListener);
        return true;
    }
}