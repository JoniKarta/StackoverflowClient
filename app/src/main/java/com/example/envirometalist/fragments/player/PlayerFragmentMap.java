package com.example.envirometalist.fragments.player;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.envirometalist.LoginActivity;
import com.example.envirometalist.PlayerActivity;
import com.example.envirometalist.R;
import com.example.envirometalist.RegisterActivity;
import com.example.envirometalist.clustermap.ClusterManagerRender;
import com.example.envirometalist.clustermap.RecycleBinClusterMarker;
import com.example.envirometalist.model.Action;
import com.example.envirometalist.model.Element;
import com.example.envirometalist.model.User;
import com.example.envirometalist.model.UserRole;
import com.example.envirometalist.services.ActionService;
import com.example.envirometalist.services.ElementService;
import com.example.envirometalist.services.UserService;
import com.example.envirometalist.utility.UserReportDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
    private ElementService elementService;
    private User userManager;
    private MapView mMapView;
    private RecycleBinClusterMarker currentItemView;
    private ActionService actionService;
    // TODO GET THE USER LOCATION AND DISPLAY IT ON THE VIEW


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        userManager = LoginActivity.user;

        initRetrofit();
        initActionRetrofit();
        getMapAsync();


     /*   googleMaps.setOnCameraMoveListener(() -> {
            float zoom = googleMaps.getCameraPosition().zoom;
            Log.i("zoom", "Zoom = " + zoom);
        });*/
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
            currentItemView = item;
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

    private void getMapAsync() {
        if (getActivity() != null) {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
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
                onClusterItemClick();
                loadElementsFromServer();
            });
        });
    }

    private void initClusterManager() {
        if (googleMaps != null) {
            if (clusterManager == null) {
                clusterManager = new ClusterManager<>(getActivity().getApplicationContext(), googleMaps);
            }
            if (clusterManagerRender == null) {
                clusterManagerRender = new ClusterManagerRender(getActivity(), googleMaps, clusterManager);
            }
            googleMaps.setOnCameraIdleListener(clusterManager);
            clusterManager.setRenderer(clusterManagerRender);
        }
    }

    private void loadElementsFromServer() {
        elementService.getAllElements(userManager.getEmail(), 20, 0).enqueue(new Callback<Element[]>() {
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

    @Override
    public void onFinishReport(Action action) {
        actionService.invokeAction(action).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Log.i("TAG", "SUCCESS!");
                } else
                    Log.i("TAG", "FAIL!");
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }
}

