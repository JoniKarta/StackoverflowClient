package com.example.envirometalist.services;

import com.example.envirometalist.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    //String BASE_URL = "http://10.0.2.2:8091/acs/users/";
    String BASE_URL = "http://10.100.102.7:8091/acs/users/";

    @GET("login/{userEmail}")
    Call<User> getUser(@Path("userEmail") String userEmail);

    @POST(".")
    Call<User> createUser(@Body User newUser);

    @PUT("{userEmail}")
    Call<User> updateUser(@Path("userEmail") String userEmail, @Body User user);
}

