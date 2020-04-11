package com.example.envirometalist.services;

import com.example.envirometalist.model.ActionEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ActionService {
    String BASE_URL = "http://10.100.102.7:8091/acs/actions";

    @POST(".")
    Call<Object> invokeAction(@Body ActionEntity action);

}
