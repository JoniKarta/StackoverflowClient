package com.example.envirometalist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.envirometalist.services.ElementService;
import com.example.envirometalist.services.UserService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ElementService elementService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
        //TODO SPLASH SCREEN

        // Init map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        // Init element services
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        elementService = retrofit.create(ElementService.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //When Map Loads Successfully
        mMap.setOnMapLoadedCallback(() -> {

            LatLng customMarkerLocationOne = new LatLng(28.583911, 77.319116);
            LatLng customMarkerLocationTwo = new LatLng(28.583078, 77.313744);
            LatLng customMarkerLocationThree = new LatLng(28.580903, 77.317408);
            LatLng customMarkerLocationFour = new LatLng(28.580108, 77.315271);
            mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne).
                    icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(MainActivity.this,R.drawable.trash,"Manish")))).setTitle("iPragmatech Solutions Pvt Lmt");
            mMap.addMarker(new MarkerOptions().position(customMarkerLocationTwo).
                    icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(MainActivity.this,R.drawable.trash,"Narender")))).setTitle("Hotel Nirulas Noida");

            mMap.addMarker(new MarkerOptions().position(customMarkerLocationThree).
                    icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(MainActivity.this,R.drawable.trash,"Neha")))).setTitle("Acha Khao Acha Khilao");
            mMap.addMarker(new MarkerOptions().position(customMarkerLocationFour).
                    icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(MainActivity.this,R.drawable.trash,"Nupur")))).setTitle("Subway Sector 16 Noida");

            //LatLngBound will cover all your marker on Google Maps
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(customMarkerLocationOne); //Taking Point A (First LatLng)
            builder.include(customMarkerLocationThree); //Taking Point B (Second LatLng)
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
            mMap.moveCamera(cu);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        });
    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        TextView txt_name = marker.findViewById(R.id.name);
        txt_name.setText(name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
}
