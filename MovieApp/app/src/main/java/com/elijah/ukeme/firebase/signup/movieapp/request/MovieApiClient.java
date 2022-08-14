package com.elijah.ukeme.firebase.signup.movieapp.request;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.elijah.ukeme.firebase.signup.movieapp.module.MovieModel;
import com.elijah.ukeme.firebase.signup.movieapp.repository.AppExecutor;
import com.elijah.ukeme.firebase.signup.movieapp.response.MovieSearchResponse;
import com.elijah.ukeme.firebase.signup.movieapp.utils.Credentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {

    private MutableLiveData<List<MovieModel>> mMovies ;

    private static MovieApiClient instance;

    private RetrieveMoviesRunnable retrieveMoviesRunnable;

    public static MovieApiClient getInstance(){
        if (instance == null){
            instance = new MovieApiClient();
        }
        return instance;
    }

    private MovieApiClient(){
        mMovies = new MutableLiveData<>();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return mMovies;
    }

    // 1 search movie by Api
    public void searchMovieApi(String query, int pageNumber){
        if (retrieveMoviesRunnable != null){
            retrieveMoviesRunnable = null;
        }
        retrieveMoviesRunnable = new RetrieveMoviesRunnable(query,pageNumber);
        final Future myHandler = AppExecutor.getInstance().netWorkIO().submit(retrieveMoviesRunnable);


        AppExecutor.getInstance().netWorkIO().schedule(new Runnable() {
            @Override
            public void run() {
                myHandler.cancel(true);

                // cancelling the retrofit call

            }
        },3000, TimeUnit.MILLISECONDS);
    }
    private class RetrieveMoviesRunnable implements Runnable{

        private String query;
        private int pageNumber;
        boolean cancellRequest;

        public RetrieveMoviesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancellRequest = false;
        }

        @Override
        public void run() {

            try {
                Response response = getMovies(query,pageNumber).execute();
                if (cancellRequest) {
                    return;
                }
                if (response.code() == 200){
                    List<MovieModel> list = new ArrayList<>(((MovieSearchResponse)response.body()).getMovies());

                    if (pageNumber == 1){
                        mMovies.postValue(list);
                    }else {
                        List<MovieModel> currentMovies = mMovies.getValue();
                        currentMovies.addAll(list);
                        mMovies.postValue(currentMovies);
                    }
                }else {
                    mMovies.postValue(null);
                }


            } catch (IOException e) {
                e.printStackTrace();
                mMovies.postValue(null);
            }


        }
           // search/query method
            Call<MovieSearchResponse> getMovies(String query, int pageNumber){
                return Services.getMovieApi().searchMovie(
                        Credentials.API_KEY,
                        query,
                        pageNumber
                );
            }

            private void cancelRequest(){
            cancellRequest = true;
            }
        }
    }


