package com.example.envirometalist.services;

import com.example.envirometalist.model.Action;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ActionService {
    String BASE_URL = "http://10.100.102.3:8091/acs/actions";

    @POST(".")
    Call<Object> invokeAction(@Body Action action);

}
