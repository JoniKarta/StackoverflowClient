package com.example.envirometalist.fragments.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.envirometalist.LoginActivity;
import com.example.envirometalist.ManagerActivity;
import com.example.envirometalist.R;
import com.example.envirometalist.fragments.player.PlayerFragmentMap;
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
import retrofit2.converter.jackson.JacksonConverterFactory;

// TODO GET THE USERNAME FROM THE MANAGER ACTIVITY

public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener, ElementAdapter.OnElementClickListener {
    private ElementAdapter adapter;
    private ElementService elementService;
    private ArrayList<Element> elementList;
    private EditText searchElementEditText;
    private ProgressBar progressBar;
    private Context mActivity;
    private static String filter;
    private static int page = 0;
    private static final int SIZE = 10;

    // TODO GET THE MANAGER EMAIL FROM THE LOGIN ACTIVITY
    private String managerEmail = LoginActivity.user.getEmail();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_elements_search, container, false);

        searchElementEditText = root.findViewById(R.id.searchElementEditText);
        Spinner spinner = root.findViewById(R.id.searchCategorySpinner);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        progressBar = root.findViewById(R.id.progressbar);

        // ArrayList of elements which will be passed to the recycler view
        elementList = new ArrayList<>();

        // Spinner Configuration
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.search_filter_spinner,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterAdapter);
        spinner.setOnItemSelectedListener(this);

        // Recycler View Configuration
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ElementAdapter(elementList, SearchFragment.this);
        recyclerView.setAdapter(adapter);

        // Init retrofit for async call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        // Inject instance to element service
        elementService = retrofit.create(ElementService.class);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (!filter.equals(SearchFilter.All.name())) {
                        progressBar.setVisibility(View.VISIBLE);
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBar.setVisibility(View.GONE), 3000);
                    }
                    getDataFromServerByFilter(managerEmail, filter, ++page);
                }
            }
        });

        searchElementEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                clearCurrentResults();
                if (filter.equals(SearchFilter.Name.name()))
                    getElementsByName(managerEmail, cs.toString() + "%", SIZE, page);
                else if (filter.equals(SearchFilter.Type.name()))
                    getElementByType(managerEmail, cs.toString() + "%", SIZE, page);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return root;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        searchElementEditText.getText().clear();
        clearCurrentResults();
        filter = getCurrentFilter(parent.getItemAtPosition(position).toString());
        getDataFromServerByFilter(managerEmail, filter, page);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(int position) {
        Toast.makeText(getActivity(), "Item at pos: " + position, Toast.LENGTH_SHORT).show();

                PlayerFragmentMap nextFrag= new PlayerFragmentMap(elementList.get(position).getLocation());
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    // Async task

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
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NotNull Call<Element[]> call, Throwable t) {
                        sweetAlert("Fatal error", "Something went wrong \n " + t.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void getElementsByName(String managerEmail, String name, int size, int page) {
        if (name.isEmpty() || name.equals("%"))
            return;
        elementService.searchElementByName(managerEmail, name, size, page).enqueue(new Callback<Element[]>() {
            @Override
            public void onResponse(@NotNull Call<Element[]> call, @NotNull Response<Element[]> response) {
                if (!response.isSuccessful() && getActivity() != null) {
                    sweetAlert("Oops...", "Something went wrong \n " + response.code());
                    return;
                }
                Collections.addAll(elementList, Objects.requireNonNull(response.body()));
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<Element[]> call, Throwable t) {
                sweetAlert("Fatal error", "Something went wrong \n " + t.getMessage());
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void getElementByType(String managerEmail, String type, int size, int page) {
        if (type.isEmpty() || type.equals("%"))
            return;
        elementService.searchElementByType(managerEmail, type, size, page).enqueue(new Callback<Element[]>() {
            @Override
            public void onResponse(@NotNull Call<Element[]> call, @NotNull Response<Element[]> response) {
                if (!response.isSuccessful() && getActivity() != null) {
                    sweetAlert("Oops...", "Something went wrong \n " + response.code());
                    return;
                }
                Collections.addAll(elementList, Objects.requireNonNull(response.body()));
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<Element[]> call, Throwable t) {
                sweetAlert("Fatal error", "Something went wrong \n " + t.getMessage());
                progressBar.setVisibility(View.GONE);

            }
        });
    }


    private void sweetAlert(String title, String content) {
        new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .show();
    }

    private void getDataFromServerByFilter(String managerEmail, String filter, int page) {
        if (SearchFilter.All.name().equals(filter))
            getAllElements(managerEmail, SIZE, page);
        else if (SearchFilter.Name.name().equals(filter))
            getElementsByName(managerEmail, "", SIZE, page);
        else if (SearchFilter.Type.name().equals(filter))
            getElementByType(managerEmail, "", SIZE, page);

    }

    private String getCurrentFilter(String filter) {
        String updateFilter = null;
        if (SearchFilter.All.name().equals(filter)) {
            updateFilter = filter;
            searchElementEditText.setInputType(InputType.TYPE_NULL);
            searchElementEditText.setEnabled(false);
        }
        else if (SearchFilter.Name.name().equals(filter)) {
            updateFilter = filter;
            searchElementEditText.setEnabled(true);
        }
        else if (SearchFilter.Type.name().equals(filter)) {
            updateFilter = filter;
            searchElementEditText.setEnabled(true);
        }
        return updateFilter;
    }


    private void clearCurrentResults() {
        if (adapter != null) {
            adapter.clearRecyclerView();
            page = 0;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ManagerActivity) {
            mActivity = (ManagerActivity) context;
        }
    }

}

