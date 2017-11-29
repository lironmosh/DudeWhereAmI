package com.app.dude.whereami;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dude.whereami.model.PlacesViewModel;
import com.app.dude.whereami.remote.GoogleMapsApi;
import com.app.dude.whereami.remote.PlaceResults;
import com.app.dude.whereami.remote.PredictionResults;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by liron.moshkovich on 11/27/17.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, OnCurrentPlaceChanged, OnSearchedPlaceChanged {

    final private static int PERMISSIONS_REQUEST_LOCATION = 60;

    FloatingActionButton locationFAB;
    ProgressBar progressBar;

    PlacesViewModel viewModel;

    boolean isAppBarLayoutExpended = true;
    GoogleMap googleMap;
    Marker currentPlaceMarker;
    Marker searchedMarker;
    PlaceResults.Result currentPlace;
    PlaceResults.Result searchedPlace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final FloatingActionButton searchFAB = findViewById(R.id.fab_search);
        locationFAB = findViewById(R.id.fab_location);
        progressBar = findViewById(R.id.progressBar);
        final AppBarLayout appBarLayout = findViewById(R.id.appbar);
        RecyclerView recyclerView = findViewById(R.id.predictions_rv);
        final EditText searchET = findViewById(R.id.search_et);
        final TextView message = findViewById(R.id.message);

        // Set RecyclerView and adapter
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        final PredictionAdapter adapter = new PredictionAdapter(this, new ArrayList<PredictionResults.Result>());
        recyclerView.setAdapter(adapter);

        // Set ViewModel and observers
        viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        viewModel.init(getResources().getString(R.string.google_maps_geocode_key), getResources().getString(R.string.google_maps_autocomplete_key));
        viewModel.getPotentialCurrentPlaces().observe(this, new Observer<PlaceResults>() {

            @Override
            public void onChanged(@Nullable PlaceResults placeResults) {

                if(placeResults != null && !placeResults.getResults().isEmpty()){
                    currentPlace = placeResults.getResults().get(0);
                    if(googleMap != null) {
                        showPlacesOnMap();
                    }
                } else {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.error_place_call_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getPotentialSearchedPlaces().observe(this, new Observer<PlaceResults>() {

            @Override
            public void onChanged(@Nullable PlaceResults placeResults) {

                if(placeResults != null && !placeResults.getResults().isEmpty()){
                    searchedPlace = placeResults.getResults().get(0);
                    appBarLayout.setExpanded(true);
                    searchET.setText(null);
                    searchET.clearFocus();
                    if(googleMap != null) {
                        showPlacesOnMap();
                    }
                } else {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.error_search_call_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getSearchPrediction().observe(this, new Observer<PredictionResults>() {

            @Override
            public void onChanged(@Nullable PredictionResults predictionResults) {

                if(predictionResults != null){
                    adapter.setPredictions(predictionResults.getResults());
                    if(predictionResults.getResults().size() == 0){
                        message.setVisibility(View.VISIBLE);
                        message.setText(getResources().getString(R.string.search_empty));
                    }else{
                        message.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.error_prediction_call_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Disable AppBarLayout drag
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

        // Handle fab image src
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(verticalOffset == 0){
                    searchFAB.setImageResource(R.drawable.places_ic_search);
                    isAppBarLayoutExpended = true;
                }else if(isAppBarLayoutExpended){
                    searchFAB.setImageResource(R.drawable.quantum_ic_keyboard_arrow_down_white_36);
                    isAppBarLayoutExpended = false;
                }
            }
        });

        // Handle fab click event
        searchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean fullyExpanded = (appBarLayout.getHeight() - appBarLayout.getBottom()) == 0;
                appBarLayout.setExpanded(!fullyExpanded);
            }
        });
        locationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacesOnMap();
            }
        });

        // Handle search text change
        searchET.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > 0){
                    GoogleMapsApi.getPlacePredictions(viewModel, editable.toString());
                }
            }
        });

        // Get Map instance
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Add location observer if permission exist else ask for a permission
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }else {
            getLifecycle().addObserver(new LocationService((LocationManager) getSystemService(Context.LOCATION_SERVICE), this));
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLifecycle().addObserver(new LocationService((LocationManager) getSystemService(Context.LOCATION_SERVICE), this));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.error_permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void showPlacesOnMap(){
        if(currentPlace != null){
            if (currentPlaceMarker == null){
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(currentPlace.getLatLng());
                markerOptions.title(currentPlace.getFormattedAddress());
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin));
                currentPlaceMarker = googleMap.addMarker(markerOptions);
            }else {
                currentPlaceMarker.setPosition(currentPlace.getLatLng());
                currentPlaceMarker.setTitle(currentPlace.getFormattedAddress());
            }
        }

        if(searchedPlace != null){
            if (searchedMarker == null){
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchedPlace.getLatLng());
                markerOptions.title(searchedPlace.getFormattedAddress());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                searchedMarker = googleMap.addMarker(markerOptions);
            }else {
                searchedMarker.setPosition(searchedPlace.getLatLng());
                searchedMarker.setTitle(searchedPlace.getFormattedAddress());
            }
        }

        locationFAB.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        focusCamera();
    }

    private void focusCamera(){
        CameraUpdate cu;

        if(searchedMarker != null && currentPlaceMarker != null){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(currentPlaceMarker.getPosition());
            builder.include(searchedMarker.getPosition());
            LatLngBounds bounds = builder.build();
            int padding = 128;
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            currentPlaceMarker.showInfoWindow();
        } else if (currentPlaceMarker != null) {
            cu = CameraUpdateFactory.newLatLngZoom(currentPlaceMarker.getPosition(), 18);
            currentPlaceMarker.showInfoWindow();
        } else {
            cu = CameraUpdateFactory.newLatLngZoom(searchedMarker.getPosition(), 18);
            searchedMarker.showInfoWindow();
        }

        googleMap.animateCamera(cu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setPadding(0, 200, 0, 0);
        if(currentPlace != null || searchedPlace != null){
            showPlacesOnMap();
        }
    }

    @Override public void onPointerCaptureChanged(boolean hasCapture) {}

    @Override
    public void onCurrentPlaceChanged(Location location) {
        GoogleMapsApi.getPotentialPlaces(viewModel, location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onSearchedPlaceChanged(String address) {
        GoogleMapsApi.getSearchedPlaces(viewModel, address);
    }
}

