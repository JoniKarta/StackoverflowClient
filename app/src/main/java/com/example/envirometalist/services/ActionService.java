package com.example.envirometalist.services;

import com.example.envirometalist.model.Action;
import com.example.envirometalist.model.Element;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ActionService {
    String BASE_URL = "http://" + EnvConnections.IP + ":" + EnvConnections.PORT + "/acs/actions/";

    @POST(".")
    Call<Element[]> getElementsInPerimeter(@Body Action action);

    @POST(".")
    Call<Object> invokeAction(@Body Action action);

}
