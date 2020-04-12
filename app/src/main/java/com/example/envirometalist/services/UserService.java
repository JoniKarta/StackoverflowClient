package com.example.envirometalist.services;

import com.example.envirometalist.model.UserEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    String BASE_URL = "http://10.0.2.2:8091/acs/users/";

    @GET("login/{userEmail}")
    Call<UserEntity> getUser(@Path("userEmail") String userEmail);

    @POST(".")
    Call<UserEntity> createUser(@Body UserEntity newUser);

    @PUT("{userEmail}")
    Call<UserEntity> updateUser(@Path("userEmail") String userEmail,@Body UserEntity user);
}

