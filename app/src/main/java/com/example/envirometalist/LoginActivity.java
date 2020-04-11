package com.example.envirometalist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.envirometalist.logic.Validator;
import com.example.envirometalist.model.UserEntity;
import com.example.envirometalist.services.UserService;
import com.example.envirometalist.utility.AlertDialog;
import com.example.envirometalist.utility.CustomDialog;
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
    private CustomDialog loadingBarDialog;
    private CustomDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadingBarDialog = new LoadingBarDialog(this);
        alertDialog = new AlertDialog(this);
        emailLoginEditText = findViewById(R.id.emailLoginEditText);
        passLoginEditText = findViewById(R.id.passLoginEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signButton);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);

        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(this,RegisterActivity.class));
        });

        loginButton.setOnClickListener(v -> {
            boolean dirty = false;
            String email = emailLoginEditText.getText().toString().trim();
            //String password = passLoginEditText.getText().toString();

            if(!Validator.isValidEmail(email)){
                dirty = true;
                emailLoginEditText.setError("Not valid email");
            }
//            if(!Validator.isValidPassword(password)){
//                dirty = true;
//                passLoginEditText.setError("Not valid password");
//            }
            if(dirty)
                return;
            loadingBarDialog.showDialog();
            userService.getUser(email).enqueue(new Callback<UserEntity>() {
                @Override
                public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                    if(!response.isSuccessful()){
                        loadingBarDialog.dismissDialog();
                        alertDialog.setNewConfiguration()
                                .setTitleText("Oops...")
                                .setContentText("Something went wrong!\n" + response.code());
                        alertDialog.showDialog();
                        return;
                    }
                    loadingBarDialog.dismissDialog();
                    Log.i("TAG", "onResponse: " + response.body());
                    // TODO Move to the chosen role activity
                    finish();
                }

                @Override
                public void onFailure(Call<UserEntity> call, Throwable t) {
                    loadingBarDialog.dismissDialog();
                    alertDialog.setNewConfiguration()
                            .setTitleText("Fatal error")
                            .setContentText("Something went wrong!\n" + t.getMessage());
                    alertDialog.showDialog();
                }
            });
        });


    }


}
