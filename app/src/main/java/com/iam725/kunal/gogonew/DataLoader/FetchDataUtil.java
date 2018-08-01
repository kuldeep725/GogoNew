package com.iam725.kunal.gogonew.DataLoader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by amit on 1/8/18.
 */

public class FetchDataUtil {
    public static final String API_KEY = "AIzaSyChXllnUaESuRZPDpSHtb3oyXgL1edHITg";

    public static MapApi createMapApi(){
        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MapApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(MapApi.class);
    }
}
