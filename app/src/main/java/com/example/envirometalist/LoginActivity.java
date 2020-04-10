package com.example.envirometalist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.envirometalist.model.UserEntity;
import com.example.envirometalist.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText emailLoginEditText = findViewById(R.id.emailLoginEditText);
        EditText passLoginEditText = findViewById(R.id.passLoginEditText);
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

            String email = emailLoginEditText.getText().toString().trim();
            String password = passLoginEditText.getText().toString();

            Call<UserEntity> call = userService.getUser(email);
            call.enqueue(new Callback<UserEntity>() {
                @Override
                public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                    if(!response.isSuccessful()){
                        Log.i("TAG", "onResponse: " + response.code());
                        return;
                    }
                    Log.i("TAG", "onResponse: " + response.body());
                }

                @Override
                public void onFailure(Call<UserEntity> call, Throwable t) {
                    Log.i("TAG", "onFailure: " + t.getMessage());
                }
            });
        });


    }
}
