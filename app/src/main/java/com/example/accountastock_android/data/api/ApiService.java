package com.example.accountastock_android.data.api;

import com.example.accountastock_android.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // Endpoint to check subscription
    @GET("checkSubscription")
    Call<Integer> checkSubscription(@Query("email") String email);

    // Endpoint to add a user
    @POST("addUser")
    Call<User> addUser(@Body User user);

    // Endpoint to get all users
    @GET("getUsers")
    Call<List<User>> getUsers();
}
