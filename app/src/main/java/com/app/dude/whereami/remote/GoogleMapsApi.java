package com.app.dude.whereami.remote;

import android.util.Log;


import com.app.dude.whereami.model.PlacesViewModel;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liron.moshkovich on 11/28/17.
 */

public class GoogleMapsApi {


    public static GoogleMapsService createGitHubService() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com/maps/api/");

        return builder.build().create(GoogleMapsService.class);
    }

    public static void getPotentialPlaces(final PlacesViewModel viewModel, final double lat, final double lan) {
        viewModel.getGitHubService().getPlacesByLatLng(lat+","+lan, viewModel.getGeocodeKey()).enqueue(new Callback<PlaceResults>() {
            @Override
            public void onResponse(Call<PlaceResults> call, Response<PlaceResults> response) {
                LatLng latLng = new LatLng(lat, lan);
                for(PlaceResults.Result result : response.body().getResults()){
                    result.setLatLng(latLng);
                }
                viewModel.getPotentialCurrentPlaces().setValue(response.body());
            }

            @Override
            public void onFailure(Call<PlaceResults> call, Throwable t) {
                Log.e("getPlaces failed", t.getMessage());
                viewModel.getPotentialCurrentPlaces().setValue(null);
            }
        });
    }

    public static void getSearchedPlaces(final PlacesViewModel viewModel, String address) {
        viewModel.getGitHubService().getPlacesAddress(address, viewModel.getGeocodeKey()).enqueue(new Callback<PlaceResults>() {
            @Override
            public void onResponse(Call<PlaceResults> call, Response<PlaceResults> response) {
                for(PlaceResults.Result result : response.body().getResults()){
                    result.setLatLng(new LatLng(result.geometry.getLocation().getLat(), result.geometry.getLocation().getLng()));
                }
                viewModel.getPotentialSearchedPlaces().setValue(response.body());
            }

            @Override
            public void onFailure(Call<PlaceResults> call, Throwable t) {
                Log.e("getPlaces failed", t.getMessage());
                viewModel.getPotentialCurrentPlaces().setValue(null);
            }
        });
    }

    public static void getPlacePredictions(final PlacesViewModel viewModel, String input) {
        viewModel.getGitHubService().getPredictions(input, viewModel.getAutoCompleteKey()).enqueue(new Callback<PredictionResults>() {
            @Override
            public void onResponse(Call<PredictionResults> call, Response<PredictionResults> response) {
                viewModel.getSearchPrediction().setValue(response.body());
            }

            @Override
            public void onFailure(Call<PredictionResults> call, Throwable t) {
                Log.e("getPredictions failed", t.getMessage());
                viewModel.getSearchPrediction().setValue(null);
            }
        });
    }
}
