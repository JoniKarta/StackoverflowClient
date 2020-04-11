package com.example.envirometalist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.envirometalist.model.UserEntity;
import com.example.envirometalist.model.UserRoleEntity;
import com.example.envirometalist.services.UserService;
import com.example.envirometalist.utility.LoadingBarDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private UserRoleEntity role;
    private LoadingBarDialog loadingBarDialog;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initRegisterUI();

        // For testing only
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient) // client for testing only
                .build();

        userService = retrofit.create(UserService.class);

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
            UserEntity newUser = new UserEntity(email.getText().toString(), role, userName.getText().toString(), avatar.getText().toString());
            // Async call - send the user to the server
            createUser(newUser);


        });

    }

    private void initRegisterUI(){
        email = findViewById(R.id.emailRegEditText);
        userName = findViewById(R.id.userNameRegEditText);
        avatar = findViewById(R.id.avatarRegEditText);
        register = findViewById(R.id.registerButton);
        loadingBarDialog = new LoadingBarDialog(this);
        String[] strings = {"PLAYER", "MANAGER", "ADMIN"};
        Spinner spinnerRoleList = findViewById(R.id.roleSpinnerList);
        ArrayAdapter<String> roleListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        roleListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoleList.setAdapter(roleListAdapter);
        spinnerRoleList.setOnItemSelectedListener(this);
    }

    private void createUser(UserEntity newUser) {
        userService.createUser(newUser).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                if (!response.isSuccessful()) {
                    Log.i("TAG", "onResponse: " + response.code());
                    loadingBarDialog.dismissDialog();
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Fatal error")
                            .setContentText("Something went wrong!\n" + response.code())
                            .show();
                    return;
                }
                Log.i("TAG", "onResponse: " + response.body());
                loadingBarDialog.dismissDialog();

            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                loadingBarDialog.dismissDialog();
                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Fatal error")
                        .setContentText("Something went wrong!\n" + t.getMessage())
                        .show();
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String itemSelected = parent.getItemAtPosition(position).toString();
        role = UserRoleEntity.valueOf(itemSelected);
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