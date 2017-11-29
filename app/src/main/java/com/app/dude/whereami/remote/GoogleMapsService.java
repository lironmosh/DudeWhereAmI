package com.app.dude.whereami.remote;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liron.moshkovich on 11/28/17.
 */

public interface GoogleMapsService {
    @GET("geocode/json")
    Call<PlaceResults> getPlacesByLatLng(@Query("latlng") String latlng, @Query("key") String key);

    @GET("geocode/json")
    Call<PlaceResults> getPlacesAddress(@Query("address") String address, @Query("key") String key);

    @GET("place/autocomplete/json")
    Call<PredictionResults> getPredictions(@Query("input") String input, @Query("key") String key);
}
