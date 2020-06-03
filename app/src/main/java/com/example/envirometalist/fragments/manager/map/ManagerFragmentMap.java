package com.example.envirometalist.fragments.manager.map;

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
import com.example.envirometalist.utility.ElementCreationDialog;
import com.example.envirometalist.utility.ElementManagementDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;



public class ManagerFragmentMap extends Fragment implements ElementCreationDialog.DialogListener, ElementManagementDialog.OnManagerManagementCallback {
    private GoogleMap googleMaps;
    private ClusterManager<RecycleBinClusterMarker> clusterManager;
    private ClusterManagerRender clusterManagerRender;
    private ElementService elementService;
    private User userManager;
    private MapView mMapView;
    private RecycleBinClusterMarker  currentItemView;
    // TODO GET THE USER LOCATION AND DISPLAY IT ON THE VIEW


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        userManager = new User("Jonathan@gmail.com", UserRole.MANAGER, "Joni", ";)");

        initRetrofit();
        getMapAsync();


        return root;

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
                loadElementsFromServer();
                onMapClicked();
                onClusterItemClick();
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

    private void onMapClicked() {
        googleMaps.setOnMapClickListener(latLng -> {
            Element element = new Element();
            element.setLocation(new Location(latLng.latitude, latLng.longitude));
            ElementCreationDialog elementCreationDialog = new ElementCreationDialog(getActivity(), ManagerFragmentMap.this, element);
            elementCreationDialog.show();
        });
    }


    private void loadElementsFromServer(){
        elementService.getAllElements(userManager.getEmail(),20,0).enqueue(new Callback<Element[]>() {
            @Override
            public void onResponse(Call<Element[]> call, Response<Element[]> response) {
                if(!response.isSuccessful()){
                    // Throw unsuccessful operation
                }
                Element[] elements = response.body();
                clusterManager.clearItems();
                for(Element element : elements){
                    RecycleBinClusterMarker recycleBinClusterMarker = new RecycleBinClusterMarker("Snippet", element);
                    clusterManager.addItem(recycleBinClusterMarker);
                    clusterManager.cluster();
                }
            }
            @Override
            public void onFailure(Call<Element[]> call, Throwable t) {

            }
        });
    }

    private void onClusterItemClick(){
      clusterManager.setOnClusterItemClickListener(item -> {
          currentItemView = item;
          ElementManagementDialog elementManagementDialog = new ElementManagementDialog(getActivity(),item.getElement(), ManagerFragmentMap.this);
          elementManagementDialog.show();
          return false;
      });
    }

    // onFinish is a callback function which gets called when the manager finished create element.
    @Override
    public void onFinish(Element element) {
        if (element != null) {
            elementService.createElement(userManager.getEmail(), element).enqueue(new Callback<Element>() {
                @Override
                public void onResponse(Call<Element> call, Response<Element> response) {
                    if (!response.isSuccessful()) {
                        // Throw Unsuccessful operation
                    }
                    RecycleBinClusterMarker recycleBinClusterMarker = new RecycleBinClusterMarker("Snippet", element);
                    clusterManager.addItem(recycleBinClusterMarker);
                    clusterManager.cluster();
                }

                @Override
                public void onFailure(Call<Element> call, Throwable t) {
                }
            });
        }
    }


    @Override
    public void onUpdate(Element updatedElement) {
        elementService.updateElement(userManager.getEmail(), updatedElement.getElementId(),updatedElement).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    // TODO THROW EXCEPTION
                    Log.i("onUpdate","response failed");
                }
                googleMaps.clear();
                loadElementsFromServer();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
        Toast.makeText(getActivity(), "onUpdate", Toast.LENGTH_SHORT).show();
    }
}

