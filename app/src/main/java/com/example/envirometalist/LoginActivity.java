package com.example.envirometalist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.envirometalist.logic.Validator;
import com.example.envirometalist.model.User;
import com.example.envirometalist.model.UserRole;
import com.example.envirometalist.services.UserService;
import com.example.envirometalist.utility.LoadingBarDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private UserService userService;
    private EditText emailLoginEditText;
    public static User user;
    private LoadingBarDialog loadingBarDialog;
    private Button loginButton;
    private Button signUpButton;
    private String selectedRole;

    public static double latitude;
    public static double longitude;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        initLoginUI();
        initUserRetrofit();
        signUpListener();
        loginListener();
    }

    private void initUserRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
    }

    private void loginListener() {
        loginButton.setOnClickListener(v -> {
            String email = emailLoginEditText.getText().toString().trim();

            if (!Validator.isValidEmail(email)) {
                emailLoginEditText.setError("Not valid email");
                return;
            }
            signInWithUserEmail(email);
        });
    }

    private void signUpListener() {
        signUpButton.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void initLoginUI() {
        loadingBarDialog = new LoadingBarDialog(this);
        emailLoginEditText = findViewById(R.id.emailLoginEditText);
        Spinner loginSpinnerList = findViewById(R.id.loginRoleSpinnerList);
        ArrayAdapter<UserRole> roleArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, UserRole.values());
        roleArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loginSpinnerList.setAdapter(roleArrayAdapter);
        loginSpinnerList.setOnItemSelectedListener(this);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signButton);

    }

    private void signInWithUserEmail(String email) {
        loadingBarDialog.showDialog();
        userService.getUser(email).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                loadingBarDialog.dismissDialog();
                if (!response.isSuccessful()) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong!\n" + response.code())
                            .show();
                    return;
                }
                user = response.body();
                if (user != null && user.getRole().name().equals(selectedRole)) {
                    // Login successfully
                    switch (user.getRole()) {
                        case PLAYER:
                            startActivity(new Intent(LoginActivity.this, PlayerActivity.class).putExtra("User", user));
                            break;
                        case MANAGER:
                            startActivity(new Intent(LoginActivity.this, ManagerActivity.class).putExtra("User", user));
                            break;
                    }
                    finish();
                } else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("You don't have those permissions!\n" + response.code())
                            .show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                loadingBarDialog.dismissDialog();
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Fatal error")
                        .setContentText("Something went wrong!\n" + t.getMessage())
                        .show();
            }
        });
    }

    /**
     * Hide the keyboard with pressing on the screen
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedRole = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null || location.getLatitude() == 0 || location.getLongitude() == 0) {
                                requestNewLocationData();
                            } else {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    };

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                44
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
}
