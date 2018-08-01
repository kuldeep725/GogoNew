package com.iam725.kunal.gogonew.DataLoader;

import org.json.JSONObject;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by amit on 1/8/18.
 * Create an interface to call api for fetching data for distance between two given points...
 * ...oring and destination using retrofit.
 */

public interface MapApi {
    String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";

    @GET("json?")
    Single<GsonObject> getData(@Query("origin") String origin,
                                       @Query("destination") String destination,
                                       @Query("key") String apiKey);
}

