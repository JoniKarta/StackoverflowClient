package com.example.envirometalist.services;

import com.example.envirometalist.model.Element;
import com.example.envirometalist.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ElementService {

    String BASE_URL = "http://10.100.102.3:8091/acs/elements/";

    @POST("{managerEmail}")
    Call<Element> createElement(
            @Path("managerEmail") String managerEmail,
            @Body Element element);

    @PUT("{managerEmail}/{elementId}")
    Call<Void> updateElement(
            @Path("managerEmail") String managerEmail,
            @Path("elementId") String elementId,
            @Body Element update);

    @GET("{userEmail}/{elementId}")
    Call<Element> getElement(
            @Path("userEmail") String userEmail,
            @Path("elementId") String elementId);

    @GET("{userEmail}")
    Call<Element[]> getAllElements(
            @Path("userEmail") String userEmail,
            @Query("size") int size,
            @Query("page") int page);

    @PUT("{managerEmail}/{parentElementId}/children")
    Call<Void> addChildToParent(
            @Path("managerEmail") String managerEmail,
            @Path("parentElementId") String parentElementId,
            @Body Element element);

    @GET("{userEmail}/{parentElementId}/children")
    Call<Element[]> getAllElementChildren(
            @Path("userEmail") String userEmail,
            @Path("parentElementId") String parentElementId,
            @Query("size") int size,
            @Query("page") int page);

    @GET("{userEmail}/{parentElementId}/parent")
    Call<Element[]> getAllElementParent(
            @Path("userEmail") String userEmail,
            @Path("childElementId") String parentElementId,
            @Query("size") int size,
            @Query("page") int page);

    @GET("{userEmail}/search/byName/{name}")
    Call<Element[]> searchElementByName(
            @Path("userEmail") String userEmail,
            @Path("name") String elementName,
            @Query("size") int size,
            @Query("page") int page);

    @GET("{userEmail}/search/byType/{type}")
    Call<Element[]> searchElementByType(
            @Path("userEmail") String userEmail,
            @Path("type") String elementType,
            @Query("size") int size,
            @Query("page") int page);


}
