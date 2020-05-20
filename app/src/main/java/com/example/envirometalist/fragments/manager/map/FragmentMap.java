package com.example.envirometalist.fragments.manager.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.envirometalist.R;
import com.example.envirometalist.model.Element;
import com.example.envirometalist.services.ElementService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentMap extends Fragment {
    private GoogleMap googleMaps;
    private ClusterManager<RecycleBinClusterMarker> clusterManager;
    private ClusterManagerRender clusterManagerRender;
    private ElementService elementService;
    private ArrayList<RecycleBinClusterMarker> recycleBinClusterMarkerArrayList;

    // TODO GET THE USER LOCATION AND DISPLAY IT ON THE VIEW

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        MapView mMapView = root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        recycleBinClusterMarkerArrayList = new ArrayList<>();
        if (getActivity() != null) {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }

        // Init retrofit for http request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        elementService = retrofit.create(ElementService.class);

        // Set google maps
        mMapView.getMapAsync(mMap -> {
            googleMaps = mMap;
            googleMaps.setOnMapLoadedCallback(() -> {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(28.580903, 77.317408)); //Taking Point A (First LatLng)
                builder.include(new LatLng(28.583911, 77.319116)); //Taking Point B (Second LatLng)
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                addMapMarkers();
                googleMaps.moveCamera(cu);
                googleMaps.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
            });
        });
        return root;

    }

    private void addMapMarkers() {
        if (googleMaps != null) {
            if (clusterManager == null) {
                clusterManager = new ClusterManager<>(getActivity().getApplicationContext(), googleMaps);
            }
            if (clusterManagerRender == null) {
                clusterManagerRender = new ClusterManagerRender(getActivity(), googleMaps, clusterManager);
            }
            clusterManager.setRenderer(clusterManagerRender);
        }
        elementService.getAllElements("Jonathan@gmail.com", 10, 0).enqueue(
                new Callback<Element[]>() {
                    @Override
                    public void onResponse(Call<Element[]> call, Response<Element[]> response) {
                        if (!response.isSuccessful()) {
                        }
                        Element[] all = response.body();
                        for (Element element : all) {
                            RecycleBinClusterMarker rec = new RecycleBinClusterMarker("Snippet", element);
                            recycleBinClusterMarkerArrayList.add(rec);
                            clusterManager.addItem(rec);
                        }
                        clusterManager.cluster();
                    }

                    @Override
                    public void onFailure(Call<Element[]> call, Throwable t) {
                    }
                }
        );
    }

}

