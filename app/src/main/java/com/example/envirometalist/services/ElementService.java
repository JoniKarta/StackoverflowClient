package com.example.envirometalist.services;

import com.example.envirometalist.model.Element;
import com.example.envirometalist.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ElementService {

    String BASE_URL = "http://10.0.0.4:8091/acs/elements/";

    @POST("{managerEmail}")
    Call<Element> createElement(@Path("managerEmail") String managerEmail,
                                @Body Element element);

    @PUT("{managerEmail}/{elementId}")
    Call<Void> updateElement(@Path("managerEmail") String managerEmail,
                             @Path("elementId") String elementId,
                             @Body Element update);

    @GET("{userEmail}/{elementId}")
    Call<Element> getElement(@Path("userEmail") String userEmail,
                             @Path("elementId") String elementId);

    @GET("{userEmail}")
    Call<Element[]> getAllElements(@Path("userEmail") String userEmail,
                                   @Body User user);
}
