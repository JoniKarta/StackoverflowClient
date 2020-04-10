package com.example.envirometalist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.envirometalist.logic.Validator;
import com.example.envirometalist.model.UserEntity;
import com.example.envirometalist.model.UserRoleEntity;
import com.example.envirometalist.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EditText emailRegEditText = findViewById(R.id.emailRegEditText);
        EditText userNameRegEditText = findViewById(R.id.userNameRegEditText);
        EditText roleRegEditText = findViewById(R.id.roleRegEditText);
        EditText avatarRegEditText = findViewById(R.id.avatarRegEditText);
        Button register = findViewById(R.id.registerButton);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);

        register.setOnClickListener(v -> {
            if (Validator.isValidEmail(emailRegEditText.getText().toString())
                    && Validator.isValidUserName(userNameRegEditText.getText().toString())
                    && Validator.isValidRole(roleRegEditText.getText().toString())
                    && Validator.isValidAvatarUrl(avatarRegEditText.getText().toString())) {
                UserEntity newUser = new UserEntity(
                        emailRegEditText.getText().toString(),
                        UserRoleEntity.valueOf(roleRegEditText.getText().toString()),
                        userNameRegEditText.getText().toString(),
                        avatarRegEditText.getText().toString());
                createUser(newUser);
            }else{
                Log.i("TAG", "onCreate: " + "Failed to login invalid field ");
            }
        });




    }



    public void createUser(UserEntity newUser) {
        // TODO create loading bar

        Call<UserEntity> call = userService.createUser(newUser);
        call.enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                if (!response.isSuccessful()) {
                    Log.i("TAG", "onResponse: " + response.code());
                    return;
                }
                //Log.i("TAG", "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                Log.i("TAG", "onFailure: " + t.getMessage());

            }
        });
    }




}