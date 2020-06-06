package com.example.envirometalist.fragments.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.envirometalist.ManagerActivity;
import com.example.envirometalist.R;
import com.example.envirometalist.fragments.manager.ManagerFragmentMap;
import com.example.envirometalist.fragments.player.PlayerFragmentMap;
import com.example.envirometalist.model.Element;
import com.example.envirometalist.model.User;
import com.example.envirometalist.model.UserRole;
import com.example.envirometalist.services.ElementService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private List<Element> elementList;
    private EditText searchElementEditText;
    private ProgressBar progressBar;
    private Context mActivity;
    private static String filter;
    private static int page = 0;

    private User user;

    public SearchFragment(User user) {
        this.user = user;
    }

    // TODO GET THE MANAGER EMAIL FROM THE LOGIN ACTIVITY
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_elements_search, container, false);
        searchElementEditText = root.findViewById(R.id.searchElementEditText);
        Spinner spinner = root.findViewById(R.id.searchCategorySpinner);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        progressBar = root.findViewById(R.id.progressbar);
        elementList = new ArrayList<>();


        ArrayAdapter<CharSequence> filterAdapter;

        if (user.getRole() == UserRole.PLAYER) {
            filterAdapter = ArrayAdapter.createFromResource(
                   requireActivity(),
                    R.array.player_filter_spinner,
                    android.R.layout.simple_spinner_dropdown_item);
        } else {
            filterAdapter = ArrayAdapter.createFromResource(
                    requireActivity(),
                    R.array.manager_filter_spinner,
                    android.R.layout.simple_spinner_dropdown_item);
        }

        spinner.setAdapter(filterAdapter);
        spinner.setOnItemSelectedListener(this);

        // Recycler View Configuration
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ElementAdapter(elementList, SearchFragment.this);
        recyclerView.setAdapter(adapter);
        initRetrofit();
        searchElementOnTextChanged();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && !filter.equals(SearchFilter.Fault.name())) {
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(() -> progressBar.setVisibility(View.GONE), 3000);
                    getDataFromServerByFilter(user.getEmail(), filter, ++page);
                }
            }
        });


        return root;
    }

    private void searchElementOnTextChanged() {
        searchElementEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                clearCurrentResults();
                if (filter.equals(SearchFilter.Name.name()))
                    getElementsByName(user.getEmail(), s.toString() + "%", 10, page);
                else if (filter.equals(SearchFilter.Type.name()))
                    getElementByType(user.getEmail(), s.toString() + "%",10, page);
            }
        });
    }

    private void initRetrofit() {
        // Init retrofit for async call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        // Inject instance to element service
        elementService = retrofit.create(ElementService.class);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        searchElementEditText.getText().clear();
        clearCurrentResults();
        filter = configFilterOptions(parent.getItemAtPosition(position).toString());
        getDataFromServerByFilter(user.getEmail(), filter, page);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(int position) {
        Fragment fragment = null;
        if (user.getRole() == UserRole.MANAGER) {
            fragment = new ManagerFragmentMap(user, elementList.get(position).getLocation());

        } else if (user.getRole() == UserRole.PLAYER) {
            fragment = new PlayerFragmentMap(user, elementList.get(position).getLocation());
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment, "fragmentMap")
                .addToBackStack(null)
                .commit();

    }


    /**
     * This function calling to callback function to get data from the server
     */
    private void getDataFromServerByFilter(String userEmail, String filter, int page) {
        if (SearchFilter.All.name().equals(filter))
            getAllElements(userEmail, 10, page);
        else if (SearchFilter.Name.name().equals(filter))
            getElementsByName(userEmail, "", 10, page);
        else if (SearchFilter.Type.name().equals(filter))
            getElementByType(userEmail, "", 10, page);
        else if (SearchFilter.Fault.name().equals(filter) && user.getRole() == UserRole.MANAGER) {
            getAllFaultElements();
        }
    }


    /**
     * Get the current filter which the user selected
     */
    private String configFilterOptions(String filter) {
        String updateFilter = null;
        if (SearchFilter.All.name().equals(filter)) {
            updateFilter = filter;
            searchElementEditText.setEnabled(false);
        } else if (SearchFilter.Name.name().equals(filter)) {
            updateFilter = filter;
            searchElementEditText.setEnabled(true);
        } else if (SearchFilter.Type.name().equals(filter)) {
            updateFilter = filter;
            searchElementEditText.setEnabled(true);
        } else if (SearchFilter.Fault.name().equals(filter)) {
            updateFilter = filter;
            searchElementEditText.setEnabled(false);
        }
        searchElementEditText.setInputType(InputType.TYPE_NULL);
        return updateFilter;
    }

    /**
     * Clear all the results which displayed on the recycler view
     */
    private void clearCurrentResults() {
        if (adapter != null) {
            adapter.clearRecyclerView();
            page = 0;
        }
    }

    /**
     * This function display error alert
     */
    private void sweetAlert(String title, String content) {
        new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .show();
    }

    /**
     * ====================== CALLBACK FUNCTION TO THE SERVER =================================
     */

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
                    public void onFailure(@NotNull Call<Element[]> call, @NotNull Throwable t) {
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
                Collections.addAll((List<Element>) elementList, Objects.requireNonNull(response.body()));
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<Element[]> call, @NotNull Throwable t) {
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
            public void onFailure(@NotNull Call<Element[]> call, @NotNull Throwable t) {
                sweetAlert("Fatal error", "Something went wrong \n " + t.getMessage());
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    /**
     * Callback function to get all element which has faults
     */
    private void getAllFaultElements() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.child("photos").listAll().addOnSuccessListener(listResult -> {

            for (StorageReference prefix : listResult.getPrefixes()) {
                Log.i("prefix: ", prefix + "");
                prefix.listAll().addOnSuccessListener(listResult1 -> {
                    for (StorageReference item : listResult1.getItems()) {
                        // All the items under listRef.
                        String[] split = item.getName().split("-");
                        elementService.getElement(user.getEmail(), split[0]).enqueue(new Callback<Element>() {
                            @Override
                            public void onResponse(@NotNull Call<Element> call, @NotNull Response<Element> response) {
                                if (!response.isSuccessful() && getActivity() != null) {
                                    sweetAlert("Oops...", "Something went wrong \n " + response.code());
                                    return;
                                }
                                Log.i("TAG", "onResponse: " + response.body());
                                elementList.add(response.body());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(@NotNull Call<Element> call, @NotNull Throwable t) {

                            }
                        });

                    }
                    Log.i("TAG", "getAllFaultElements: ");


                }).addOnFailureListener(e -> {

                });
            }
        }).addOnFailureListener(e -> Toast.makeText(mActivity, "getAllFault failed", Toast.LENGTH_SHORT).show());
    }

    /**
     * ======================= END OF CALLBACK FUNCTIONS =========================
     */

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        if (context instanceof ManagerActivity) {
            mActivity = (ManagerActivity) context;
        }
    }

}

