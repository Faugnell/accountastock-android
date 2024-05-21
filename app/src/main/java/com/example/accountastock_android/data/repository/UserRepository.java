package com.example.accountastock_android.data.repository;

import android.content.Context;
import com.example.accountastock_android.data.api.ApiClient;
import com.example.accountastock_android.data.api.ApiService;
import com.example.accountastock_android.data.database.MyDatabaseHelper;
import com.example.accountastock_android.data.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private MyDatabaseHelper dbHelper;
    private ApiService apiService;

    public UserRepository(Context context) {
        dbHelper = new MyDatabaseHelper(context);
        apiService = ApiClient.getApiService();
    }

    // Function to check subscription
    public void checkSubscription(String email, Callback<Integer> callback) {
        apiService.checkSubscription(email).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    // Return subscription level to the ViewModel
                    callback.onResponse(call, response);
                } else {
                    // Handle API error
                    callback.onFailure(call, new Throwable("API Error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                // Handle network error
                callback.onFailure(call, t);
            }
        });
    }

    public void addUser(String name, String email) {
        // Ajouter à l'API en ligne
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        apiService.addUser(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // L'utilisateur a été ajouté avec succès à l'API en ligne
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Gérer l'erreur
            }
        });
    }
}
