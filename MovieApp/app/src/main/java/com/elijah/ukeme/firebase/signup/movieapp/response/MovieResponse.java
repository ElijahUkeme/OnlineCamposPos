package com.elijah.ukeme.firebase.signup.movieapp.response;


import com.elijah.ukeme.firebase.signup.movieapp.module.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// This class is for single movie
public class MovieResponse {

    // getting the movie objects
    @SerializedName("results")
    @Expose
    private MovieModel movie;

    public MovieModel getMovie(){
        return movie;
    }
}
