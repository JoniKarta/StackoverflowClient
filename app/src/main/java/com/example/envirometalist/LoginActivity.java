package com.example.envirometalist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.envirometalist.logic.Validator;
import com.example.envirometalist.model.UserEntity;
import com.example.envirometalist.services.UserService;
import com.example.envirometalist.utility.LoadingBarDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private UserService userService;
    private EditText emailLoginEditText;
    private EditText passLoginEditText;
    private LoadingBarDialog loadingBarDialog;
    private Button loginButton;
    private Button signUpButton;

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
            boolean dirty = false;
            String email = emailLoginEditText.getText().toString().trim();
            //String password = passLoginEditText.getText().toString();

            if (!Validator.isValidEmail(email)) {
                dirty = true;
                emailLoginEditText.setError("Not valid email");
            }
/*            if(!Validator.isValidPassword(password)){
                dirty = true;
                passLoginEditText.setError("Not valid password");
            }*/
            if (dirty)
                return;

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
        passLoginEditText = findViewById(R.id.passLoginEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signButton);

    }

    private void signInWithUserEmail(String email) {
        loadingBarDialog.showDialog();
        userService.getUser(email).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                loadingBarDialog.dismissDialog();
                if (!response.isSuccessful()) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong!\n" + response.code())
                            .show();
                    return;
                }

                Log.i("TAG", "onResponse: " + response.body());
                // TODO Move to the chosen role activity
                finish();
            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
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
}
