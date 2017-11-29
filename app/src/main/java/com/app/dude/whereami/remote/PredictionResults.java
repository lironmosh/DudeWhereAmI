package com.app.dude.whereami.remote;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by liron.moshkovich on 11/28/17.
 */

@Entity
public class PredictionResults {

    @SerializedName("predictions")
    public ArrayList<Result> results;

    public ArrayList<Result> getResults() {
        return results;
    }

    @Entity
    public class Result {

        @SerializedName("description")
        public String description;

        @SerializedName("matched_substrings")
        public ArrayList<MatchedSubstring> matchedSubstrings;


        public ArrayList<MatchedSubstring> getMatchedSubstrings() {
            return matchedSubstrings;
        }

        @Entity
        public class MatchedSubstring {

            @SerializedName("length")
            public int length;

            @SerializedName("offset")
            public int offset;

            public int getLength() {
                return length;
            }

            public int getOffset() {
                return offset;
            }
        }


    }
}
