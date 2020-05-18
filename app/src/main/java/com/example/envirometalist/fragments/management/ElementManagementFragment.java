package com.example.envirometalist.fragments.management;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import static androidx.constraintlayout.widget.Constraints.TAG;

// TODO GET THE USERNAME FROM THE MANAGER ACTIVITY

public class ElementManagementFragment extends Fragment implements AdapterView.OnItemSelectedListener, ElementAdapter.OnElementClickListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ElementService elementService;
    private ArrayList<Element> elementList;
    private String filter;
    private static int page = 0;
    private static final int SIZE = 10;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_elements_manager, container, false);
        // Spinner view configuration
        Spinner spinner = root.findViewById(R.id.searchCategorySpinner);
        EditText searchElementEditText = root.findViewById(R.id.searchElementEditText);
        elementList = new ArrayList<>();
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_filter_spinner, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterAdapter);
        spinner.setOnItemSelectedListener(this);

        // Recycler view configuration
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        // Init retrofit for async call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Inject instance to element service
        elementService = retrofit.create(ElementService.class);
        getAllElements("Jonathan@gmail.com", SIZE, page++);

        // ===================================================================================///
        // Adding items to listview
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(getActivity(), "Last", Toast.LENGTH_LONG).show();
                    //TODO CHECK THE FILTER
                    getAllElements("Jonathan@gmail.com", SIZE, page++);
                }
            }
        });

        // ===================================================================================///
        searchElementEditText.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                Log.i(TAG, "beforeTextChanged: " + cs.toString());
                getElementsByName("Jonathan@gmail.com", cs.toString(), SIZE, 0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // Get the elements from the server
        //  getAllElements("Jonathan@gmail.com",10, 0);
        return root;
    }

    // ===================================================================================///

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        filter = parent.getItemAtPosition(position).toString();
        Toast.makeText(getActivity(), filter, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // ===================================================================================///
    @Override
    public void onClick(int position) {
        Toast.makeText(getActivity(), "Item at pos: " + position, Toast.LENGTH_SHORT).show();

        //        NextFragment nextFrag= new NextFragment();
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.Layout_container, nextFrag, "findThisFragment")
//                .addToBackStack(null)
//                .commit();
    }


    //======================================================================================//
    private void getAllElements(String managerEmail, int size, int page) {
        elementService.getAllElements(managerEmail, size, page)
                .enqueue(new Callback<Element[]>() {
                    @Override
                    public void onResponse(@NotNull Call<Element[]> call, @NotNull Response<Element[]> response) {
                        if (!response.isSuccessful() && getActivity() != null) {
                            sweetAlert("Oops...", "Something went wrong \n " + response.code());
                            return;
                        }
                        Collections.addAll(elementList, Objects.requireNonNull(response.body()));
                        adapter = new ElementAdapter(elementList, ElementManagementFragment.this);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(@NotNull Call<Element[]> call, Throwable t) {
                        sweetAlert("Fatal error", "Something went wrong \n " + t.getMessage());
                    }
                });
    }

    private void getElementsByName(String managerEmail, String name, int size, int page) {
        elementService.searchElementByName(managerEmail, name, size, page).enqueue(new Callback<Element[]>() {
            @Override
            public void onResponse(@NotNull Call<Element[]> call, @NotNull Response<Element[]> response) {
                if (!response.isSuccessful() && getActivity() != null) {
                    sweetAlert("Oops...", "Something went wrong \n " + response.code());

                    return;
                }
                Collections.addAll(elementList, Objects.requireNonNull(response.body()));
                adapter = new ElementAdapter(elementList, ElementManagementFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NotNull Call<Element[]> call, Throwable t) {
                sweetAlert("Fatal error", "Something went wrong \n " + t.getMessage());

            }
        });
    }

    private void getElementByType(String managerEmail, String type, int size, int page) {
        elementService.searchElementByName(managerEmail, type, size, page).enqueue(new Callback<Element[]>() {
            @Override
            public void onResponse(@NotNull Call<Element[]> call, @NotNull Response<Element[]> response) {
                if (!response.isSuccessful() && getActivity() != null) {
                    sweetAlert("Oops...", "Something went wrong \n " + response.code());
                    return;
                }
                Collections.addAll(elementList, Objects.requireNonNull(response.body()));
                adapter = new ElementAdapter(elementList, ElementManagementFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NotNull Call<Element[]> call, Throwable t) {
                sweetAlert("Fatal error", "Something went wrong \n " + t.getMessage());
            }
        });
    }

    private void sweetAlert(String title, String content) {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .show();
    }
}

