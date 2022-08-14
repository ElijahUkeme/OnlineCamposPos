package com.elijah.ukeme.firebase.signup.movieapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elijah.ukeme.firebase.signup.movieapp.module.MovieModel;
import com.elijah.ukeme.firebase.signup.movieapp.repository.MovieRepository;

import java.util.List;

public class MovieListVieModel extends ViewModel {

private MovieRepository movieRepository;

    public MovieListVieModel() {

        movieRepository = MovieRepository.getInstance();
    }
    public LiveData <List<MovieModel>> getMovies(){
        return movieRepository.getMovies();
    }


    // 3 call the searchmovieApi from the repository class in the viewmodel class
    public void searchMovieApi(String query, int pageNumber){
        movieRepository.searchMovieApi(query,pageNumber);
    }

    public void searchNextPage(){
        movieRepository.searchNextPage();
    }
}
