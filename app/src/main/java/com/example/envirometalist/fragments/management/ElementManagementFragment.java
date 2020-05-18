package com.example.envirometalist.fragments.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.envirometalist.R;
import com.example.envirometalist.model.Element;
import com.example.envirometalist.services.ElementService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// TODO GET THE USERNAME FROM THE MANAGER ACTIVITY

public class ElementManagementFragment extends Fragment implements AdapterView.OnItemSelectedListener, ElementAdapter.OnElementClickListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ElementService elementService;
    private Spinner spinner;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_elements_manager, container, false);
        // Spinner view configuration
        spinner = root.findViewById(R.id.searchCategorySpinner);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.search_filter_spinner,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterAdapter);
        spinner.setOnItemSelectedListener(this);

        // Recycler view configuration
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        // Init retrofit for async call
        Retrofit  retrofit = new Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Inject instance to element service
        elementService = retrofit.create(ElementService.class);

        // Get the elements from the server
        getAllElements("Jonathan@gmail.com");
        return root;
    }


       @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(int position) {
        Toast.makeText(getActivity(), "Item at pos: " + position, Toast.LENGTH_SHORT).show();

        //        NextFragment nextFrag= new NextFragment();
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.Layout_container, nextFrag, "findThisFragment")
//                .addToBackStack(null)
//                .commit();
    }

    private void getAllElements(String managerEmail){
        elementService.getAllElements(managerEmail)
                .enqueue(new Callback<Element[]>() {
                    @Override
                    public void onResponse(@NotNull Call<Element[]> call, @NotNull Response<Element[]> response) {
                        if (!response.isSuccessful() && getActivity() != null) {
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Something went wrong!\n" + response.code())
                                    .show();
                            return;
                        }
                        ArrayList<Element> arrayList = new ArrayList<>();
                        Collections.addAll(arrayList, Objects.requireNonNull(response.body()));
                        adapter = new ElementAdapter(arrayList,ElementManagementFragment.this);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(@NotNull Call<Element[]> call, Throwable t) {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Fatal error")
                                .setContentText("Something went wrong!\n" + t.getMessage())
                                .show();
                    }
                });
    }

    private void getElementsByName(String name){

    }
    private void getElementsByActive(boolean active){

    }

}
