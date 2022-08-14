package com.elijah.ukeme.firebase.signup.movieapp.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.elijah.ukeme.firebase.signup.movieapp.module.MovieModel;
import com.elijah.ukeme.firebase.signup.movieapp.request.MovieApiClient;

import java.util.List;

public class MovieRepository {

    private static MovieRepository instance;

    private MovieApiClient movieApiClient;

    private String mQuery;
    private int mPageNumber;


    public static MovieRepository getInstance(){

        if (instance == null){
            instance = new MovieRepository();
        }
        return instance;
    }

    private MovieRepository(){
        movieApiClient = MovieApiClient.getInstance();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return movieApiClient.getMovies();
    }
    //2 call the search movie method from the movieApiclient in the repository class
    public void searchMovieApi(String query, int pageNumber){
        mQuery = query;
        mPageNumber = pageNumber;
        movieApiClient.searchMovieApi(query,pageNumber);
    }

    public void searchNextPage(){
        searchMovieApi(mQuery,mPageNumber+1);
    }
}
