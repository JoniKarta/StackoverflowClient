package com.example.envirometalist;

import android.app.Activity;
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

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private UserService userService;
    private EditText email;
    private EditText userName;
    private EditText avatar;
    private UserRole role;
    private LoadingBarDialog loadingBarDialog;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initRegisterUI();
        initUserRetrofit();
        register();



    }

    /**
     * Init retrofit for making http requests for the server
     */
    private void initUserRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
    }

    /**
     * Init all the ui of the register activity
     */
    private void initRegisterUI() {
        email = findViewById(R.id.emailRegEditText);
        userName = findViewById(R.id.userNameRegEditText);
        avatar = findViewById(R.id.avatarRegEditText);
        register = findViewById(R.id.registerButton);
        loadingBarDialog = new LoadingBarDialog(this);
        String[] strings = {"PLAYER", "MANAGER"};
        Spinner spinnerRoleList = findViewById(R.id.roleSpinnerList);
        ArrayAdapter<String> roleListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        roleListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoleList.setAdapter(roleListAdapter);
        spinnerRoleList.setOnItemSelectedListener(this);
    }

    /** OnClickListener while the user pressed on the register button */
    private void register(){
        register.setOnClickListener(v -> {

            boolean dirty = false;

            if (!Validator.isValidEmail(email.getText().toString())) {
                dirty = true;
                email.setError("Not valid email");
            }
            if (!Validator.isValidUserName(userName.getText().toString())) {
                dirty = true;
                userName.setError("Not valid userName");
            }

            if (!Validator.isValidAvatarUrl(avatar.getText().toString())) {
                dirty = true;
                avatar.setError("Not valid avatar");
            }
            if (dirty)
                return;

            loadingBarDialog.showDialog();
            User newUser = new User(email.getText().toString(), role, userName.getText().toString(), avatar.getText().toString());
            createUser(newUser);
        });
    }

    /**
     * Async method for which send http POST request for create new user
     */
    private void createUser(User newUser) {
        userService.createUser(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                loadingBarDialog.dismissDialog();
                if (!response.isSuccessful()) {
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Fatal error")
                            .setContentText("Something went wrong!\n" + response.code())
                            .show();
                    return;
                }
                finish();
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                loadingBarDialog.dismissDialog();
                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Fatal error")
                        .setContentText("Something went wrong!\n" + t.getMessage())
                        .show();
            }
        });
    }


    /** OnItemSelectedListener which gets called while the user select role {PLAYER, MANAGER} */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String itemSelected = parent.getItemAtPosition(position).toString();
        role = UserRole.valueOf(itemSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
}