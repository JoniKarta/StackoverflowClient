package com.example.envirometalist.services;

import com.example.envirometalist.model.UserEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    String BASE_URL = "http://10.100.102.7:8091/acs/users/";

    @GET("login/{userEmail}")
    Call<UserEntity> getUser(@Path("userEmail") String userEmail);

    @POST(".")
    Call<UserEntity> createUser(@Body UserEntity newUser);

    @PUT("{userEmail}")
    Call<UserEntity> updateUser(@Path("userEmail") String userEmail,@Body UserEntity user);
}


//    public void updateUser(String userEmail, UserEntity user) {
//        // TODO create loading bar
//
//        Call<UserEntity> call = userService.updateUser(userEmail, user);
//        call.enqueue(new Callback<UserEntity>() {
//            @Override
//            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
//                if (!response.isSuccessful()) {
//                    Log.i("TAG", "onResponse: " + response.code());
//                    return;
//                }
//                Log.i("TAG", "onResponse: " + response.body());
//            }
//
//            @Override
//            public void onFailure(Call<UserEntity> call, Throwable t) {
//                Log.i("TAG", "onFailure: " + t.getMessage());
//
//            }
//        });
//    }