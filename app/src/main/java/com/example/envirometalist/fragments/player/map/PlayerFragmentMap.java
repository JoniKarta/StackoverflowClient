package com.example.envirometalist.fragments.player.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.envirometalist.R;
import com.example.envirometalist.clustermap.ClusterManagerRender;
import com.example.envirometalist.clustermap.RecycleBinClusterMarker;
import com.example.envirometalist.model.Element;
import com.example.envirometalist.model.Location;
import com.example.envirometalist.model.User;
import com.example.envirometalist.model.UserRole;
import com.example.envirometalist.services.ElementService;
import com.example.envirometalist.services.UserService;
import com.example.envirometalist.utility.ElementCreationDialog;
import com.example.envirometalist.utility.ElementManagementDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class PlayerFragmentMap extends Fragment {
    private GoogleMap googleMaps;
    private ClusterManager<RecycleBinClusterMarker> clusterManager;
    private ClusterManagerRender clusterManagerRender;
    private UserService userService;
    private ElementService elementService;
    private User userManager;
    private MapView mMapView;
    // TODO GET THE USER LOCATION AND DISPLAY IT ON THE VIEW


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        userManager = new User("Jonathan@gmail.com", UserRole.MANAGER, "Joni", ";)");
        if (getActivity() != null) {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }

        // Init retrofit for http request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);

        Retrofit elementRetrofit = new Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        elementService = elementRetrofit.create(ElementService.class);

        // Set google maps
        mMapView.getMapAsync(mMap -> {
            googleMaps = mMap;
            googleMaps.setOnMapLoadedCallback(() -> {
                initClusterManager();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(28.580903, 77.317408)); //Taking Point A (First LatLng)
                builder.include(new LatLng(28.583911, 77.319116)); //Taking Point B (Second LatLng)
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                googleMaps.moveCamera(cu);
                googleMaps.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

            });
        });
        return root;
    }

    private void initClusterManager() {
        if (googleMaps != null) {
            if (clusterManager == null) {
                clusterManager = new ClusterManager<>(getActivity().getApplicationContext(), googleMaps);
            }
            if (clusterManagerRender == null) {
                clusterManagerRender = new ClusterManagerRender(getActivity(), googleMaps, clusterManager);
            }
            clusterManager.setRenderer(clusterManagerRender);
        }
    }

}

