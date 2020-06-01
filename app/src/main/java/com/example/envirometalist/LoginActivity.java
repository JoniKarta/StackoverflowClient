package com.example.envirometalist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.envirometalist.logic.Validator;
import com.example.envirometalist.model.User;
import com.example.envirometalist.model.UserRole;
import com.example.envirometalist.services.UserService;
import com.example.envirometalist.utility.LoadingBarDialog;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private UserService userService;
    private EditText emailLoginEditText;
    private LoadingBarDialog loadingBarDialog;
    private Button loginButton;
    private Button signUpButton;
    private String selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initLoginUI();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
        signUpListener();
        loginListener();
    }

    private void loginListener() {
        loginButton.setOnClickListener(v -> {
            String email = emailLoginEditText.getText().toString().trim();

            if (!Validator.isValidEmail(email)) {
                emailLoginEditText.setError("Not valid email");
                return;
            }
            signInWithUserEmail(email);
        });
    }

    private void signUpListener() {
        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void initLoginUI() {
        loadingBarDialog = new LoadingBarDialog(this);
        emailLoginEditText = findViewById(R.id.emailLoginEditText);
        Spinner loginSpinnerList = findViewById(R.id.loginRoleSpinnerList);

        ArrayAdapter<UserRole> roleArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, UserRole.values());
        roleArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loginSpinnerList.setAdapter(roleArrayAdapter);
        loginSpinnerList.setOnItemSelectedListener(this);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signButton);

    }

    private void signInWithUserEmail(String email) {
        loadingBarDialog.showDialog();
        userService.getUser(email).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loadingBarDialog.dismissDialog();
                if (!response.isSuccessful()) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong!\n" + response.code())
                            .show();
                    return;
                }
                User user = response.body();
                if (user != null && user.getRole().name().equals(selectedRole)) {
                    // Login successfully
                    switch (user.getRole()) {
                        case PLAYER:
                            startActivity(new Intent(LoginActivity.this, PlayerActivity.class).putExtra("User",user));
                            break;
                        case MANAGER:
                            startActivity(new Intent(LoginActivity.this, ManagerActivity.class).putExtra("User",user));
                            break;
                        case ADMIN:
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class).putExtra("User",user));
                            break;
                    }
                    // TODO Move to the chosen role activity
                    finish();
                } else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("You don't have those permissions!\n" + response.code())
                            .show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loadingBarDialog.dismissDialog();
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Fatal error")
                        .setContentText("Something went wrong!\n" + t.getMessage())
                        .show();
            }
        });
    }

    /**
     * Hide the keyboard with pressing on the screen
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedRole = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
