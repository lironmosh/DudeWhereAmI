package com.app.dude.whereami.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.app.dude.whereami.remote.GoogleMapsApi;
import com.app.dude.whereami.remote.GoogleMapsService;
import com.app.dude.whereami.remote.PlaceResults;
import com.app.dude.whereami.remote.PredictionResults;

/**
 * Created by liron.moshkovich on 11/28/17.
 */

public class PlacesViewModel extends ViewModel {

    private GoogleMapsService gitHubService;
    private MutableLiveData<PlaceResults> potentialCurrentPlaces;
    private MutableLiveData<PlaceResults> potentialSearchedPlaces;
    private MutableLiveData<PredictionResults> searchPrediction;
    private String geocodeKey;
    private String autoCompleteKey;

    public void init(String geocodeKey, String autoCompleteKey) {
        if(gitHubService == null){
            gitHubService = GoogleMapsApi.createGitHubService();
        }
        if(potentialCurrentPlaces == null){
            potentialCurrentPlaces = new MutableLiveData<>();
        }
        if(potentialSearchedPlaces == null){
            potentialSearchedPlaces = new MutableLiveData<>();
        }
        if(searchPrediction == null){
            searchPrediction = new MutableLiveData<>();
        }
        this.geocodeKey = geocodeKey;
        this.autoCompleteKey = autoCompleteKey;
    }

    public GoogleMapsService getGitHubService() {
        return gitHubService;
    }

    public MutableLiveData<PlaceResults> getPotentialCurrentPlaces() {
        return potentialCurrentPlaces;
    }

    public MutableLiveData<PlaceResults> getPotentialSearchedPlaces() {
        return potentialSearchedPlaces;
    }

    public MutableLiveData<PredictionResults> getSearchPrediction() {
        return searchPrediction;
    }

    public String getGeocodeKey() {
        return geocodeKey;
    }

    public String getAutoCompleteKey() {
        return autoCompleteKey;
    }
}
