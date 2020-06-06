package com.example.envirometalist.fragments.player;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.envirometalist.LoginActivity;
import com.example.envirometalist.R;
import com.example.envirometalist.clustermap.ClusterManagerRender;
import com.example.envirometalist.clustermap.RecycleBinClusterMarker;
import com.example.envirometalist.model.Action;
import com.example.envirometalist.model.Element;
import com.example.envirometalist.model.Invoker;
import com.example.envirometalist.model.Location;
import com.example.envirometalist.model.User;
import com.example.envirometalist.services.ActionService;
import com.example.envirometalist.services.ElementService;
import com.example.envirometalist.utility.UserReportDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.ClusterManager;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class PlayerFragmentMap extends Fragment implements UserReportDialog.OnReportReadyListener {
    private GoogleMap googleMaps;
    private ClusterManager<RecycleBinClusterMarker> clusterManager;
    private ClusterManagerRender clusterManagerRender;
    private MapView mMapView;
    private ActionService actionService;
    private ElementService elementService;
    private User player;
    private Location elementLocation;

    public PlayerFragmentMap(User player, Location elementLocation){
        this.player = player;
        this.elementLocation = elementLocation;
    }
    public PlayerFragmentMap(User player){
        this.player = player;
    }
    public PlayerFragmentMap(){

    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        player = LoginActivity.user;

        initRetrofit();
        initActionRetrofit();
        getMapAsync();

        Log.i("myLocation:", "LAT= " + LoginActivity.latitude + " LNG= " + LoginActivity.longitude);
        return root;

    }

    private void initActionRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ActionService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        actionService = retrofit.create(ActionService.class);
    }

    private void onClusterItemClick() {
        clusterManager.setOnClusterItemClickListener(item -> {
            UserReportDialog userReportDialog = new
                    UserReportDialog(getActivity(), item.getElement(),
                    this);
            userReportDialog.show();
            return false;
        });

    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
         elementService = retrofit.create(ElementService.class);
    }

    @SuppressLint("MissingPermission")
    private void getMapAsync() {
        if (getActivity() != null) {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        // Set google maps
        mMapView.getMapAsync(mMap -> {
            googleMaps = mMap;
            googleMaps.setOnMapLoadedCallback(() -> {
                initClusterManager();
                googleMaps.setMyLocationEnabled(true);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                builder.include(new LatLng(LoginActivity.latitude - 0.003708, LoginActivity.longitude- 0.003008));
                builder.include(new LatLng(LoginActivity.latitude + 0.003708, LoginActivity.longitude+ 0.003008));

                LatLngBounds bounds = builder.build();

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                googleMaps.animateCamera(cu);

                onClusterItemClick();
                loadElementsFromServer();
                cameraMoveLoadElementsListener();
            });
        });
    }

    private void cameraMoveLoadElementsListener() {
        googleMaps.setOnCameraIdleListener(() -> {
            Log.i("MOVE", "CAMERA MOVE!");
            loadElementsFromServer();
        });
    }

    private void initClusterManager() {
        if (googleMaps != null) {
            if (clusterManager == null) {
                clusterManager = new ClusterManager<>(requireActivity(), googleMaps);
            }
            if (clusterManagerRender == null) {
                clusterManagerRender = new ClusterManagerRender(getActivity(), googleMaps, clusterManager);
            }
            googleMaps.setOnCameraIdleListener(clusterManager);
            clusterManager.setRenderer(clusterManagerRender);
        }
    }

    private void loadElementsFromServer() {
        Map<String, Object> attributes = new HashMap<>();
        VisibleRegion visibleRegion = googleMaps.getProjection().getVisibleRegion();
        LatLng northeast = visibleRegion.latLngBounds.northeast;
        LatLng southwest = visibleRegion.latLngBounds.southwest;

        attributes.put("minLat", southwest.latitude);
        attributes.put("maxLat", northeast.latitude );
        attributes.put("minLng", southwest.longitude);
        attributes.put("maxLng", northeast.longitude);

        actionService.getElementsInPerimeter(new Action("Perimeter",
                null, null,
                null, new Invoker(LoginActivity.user.getEmail()),
                attributes)).enqueue(new Callback<Element[]>() {
            @Override
            public void onResponse(@NotNull Call<Element[]> call, @NotNull Response<Element[]> response) {
                if (!response.isSuccessful()) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops..")
                            .setContentText("Something went wrong!\n" + response.code())
                            .show();
                }
                if (response.body() != null) {
                    Element[] elements = response.body();
                    clusterManager.clearItems();
                    for (Element element : elements) {
                        RecycleBinClusterMarker recycleBinClusterMarker = new RecycleBinClusterMarker("Snippet", element);
                        clusterManager.addItem(recycleBinClusterMarker);
                        clusterManager.cluster();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Element[]> call, @NotNull Throwable t) {

            }
        });
        elementService.getAllElements(player.getEmail(), 20, 0).enqueue(new Callback<Element[]>() {
            @Override
            public void onResponse(@NotNull Call<Element[]> call, @NotNull Response<Element[]> response) {
                if (!response.isSuccessful()) {
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops..")
                            .setContentText("Something went wrong!\n" + response.code())
                            .show();
                }
                if (response.body() != null) {
                    Element[] elements = response.body();
                    for (Element element : elements) {
                        RecycleBinClusterMarker recycleBinClusterMarker = new RecycleBinClusterMarker("Snippet", element);
                        clusterManager.addItem(recycleBinClusterMarker);
                        clusterManager.cluster();
                    }
                }

            }

            @Override
            public void onFailure(@NotNull Call<Element[]> call, @NotNull Throwable t) {
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Fatal error")
                        .setContentText("Something went wrong!\n" + t.getMessage())
                        .show();
            }
        });
    }

    // @ TODO - CHECK WHY GOES TO FAIL!
    @Override
    public void onFinishReport(Action action) {
        actionService.invokeAction(action).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                if (response.isSuccessful()) {
                    Log.i("onFinish", "SUCCESS!");
                } else
                    Log.i("onFinish", "FAIL!");
            }

            @Override
            public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {

            }
        });
    }
}

