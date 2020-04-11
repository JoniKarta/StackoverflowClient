package com.example.envirometalist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.envirometalist.logic.Validator;
import com.example.envirometalist.model.UserEntity;
import com.example.envirometalist.services.UserService;
import com.example.envirometalist.utility.LoadingBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private UserService userService;
    private EditText emailLoginEditText;
    private EditText passLoginEditText;
    private LoadingBar loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLoginEditText = findViewById(R.id.emailLoginEditText);
        passLoginEditText = findViewById(R.id.passLoginEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signButton);
        loading = new LoadingBar(this);

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
            loading.showLoadingDialog();
            Log.i("TAG", "onCreate: " + email);
            Call<UserEntity> call = userService.getUser(email);
            call.enqueue(new Callback<UserEntity>() {
                @Override
                public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                    if(!response.isSuccessful()){
                        Log.i("TAG", "onResponse: " + response.code());
                        loading.hidePDialog();
                        return;
                    }
                    Log.i("TAG", "onResponse: " + response.body());
                    loading.hidePDialog();
                    finish();
                }

                @Override
                public void onFailure(Call<UserEntity> call, Throwable t) {
                    Log.i("TAG", "onFailure: " + t.getMessage());
                }
            });
        });


    }
}
