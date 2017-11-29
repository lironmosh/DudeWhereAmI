package com.app.dude.whereami.remote;

import android.arch.persistence.room.Entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by liron.moshkovich on 11/28/17.
 */

@Entity
public class PlaceResults {

    @SerializedName("results")
    public ArrayList<Result> results;

    public ArrayList<Result> getResults() {
        return results;
    }

    @Entity
    public class Result {

        public LatLng latLng;

        @SerializedName("formatted_address")
        public String formattedAddress;

        @SerializedName("geometry")
        public Geometry geometry;


        public String getFormattedAddress() {
            return formattedAddress;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public void setLatLng(LatLng latLng) {
            this.latLng = latLng;
        }

        @Entity
        public class Geometry {

            @SerializedName("location")
            Location location;

            public Location getLocation() {
                return location;
            }

            @Entity
            public class Location {

                @SerializedName("lat")
                double lat;

                @SerializedName("lng")
                double lng;

                public double getLat() {
                    return lat;
                }

                public double getLng() {
                    return lng;
                }
            }
        }
    }
}
