package com.elijah.ukeme.firebase.signup.movieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.elijah.ukeme.firebase.signup.movieapp.adapters.MovieRecyclerView;
import com.elijah.ukeme.firebase.signup.movieapp.adapters.OnMovieListener;
import com.elijah.ukeme.firebase.signup.movieapp.module.MovieModel;
import com.elijah.ukeme.firebase.signup.movieapp.request.Services;
import com.elijah.ukeme.firebase.signup.movieapp.response.MovieSearchResponse;
import com.elijah.ukeme.firebase.signup.movieapp.utils.Credentials;
import com.elijah.ukeme.firebase.signup.movieapp.utils.MovieApi;
import com.elijah.ukeme.firebase.signup.movieapp.viewmodels.MovieListVieModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMovieListener {

    private MovieListVieModel movieListVieModel;

    private RecyclerView recycler;
    private MovieRecyclerView movieRecyclerAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycler = findViewById(R.id.recyclerview);
       toolbar = findViewById(R.id.main_toolbar);
       setSupportActionBar(toolbar);

        setUpsearchView();

        //999ab7e3dceefb3c0e622c1cb86219d0
        //https://api.themoviedb.org/3/movie/550?api_key=999ab7e3dceefb3c0e622c1cb86219d0


        movieListVieModel = new ViewModelProvider(this).get(MovieListVieModel.class);

        observeAnyChange();
        configureRecyclerView();

    }

    // 4 calling the same method from the viewmodel in the main activity
    private void searchMovieApi(String query, int pageNumber){
        movieListVieModel.searchMovieApi(query,pageNumber);
    }

    private void configureRecyclerView(){
        movieRecyclerAdapter = new MovieRecyclerView(this);

        recycler.setAdapter(movieRecyclerAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)){
                    movieListVieModel.searchNextPage();

                }
            }
        });

    }

    private void observeAnyChange() {
        movieListVieModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                if (movieModels != null){
                    for (MovieModel movieModel: movieModels){
                        Log.v("Main","onChanged");

                        movieRecyclerAdapter.setmMovies(movieModels);
                    }
                }

            }
        });
    }

    @Override
    public void onMovieClick(int position) {

        Intent intent = new Intent(this,MovieDetails.class);
        intent.putExtra("movie",movieRecyclerAdapter.getSelectedMovie(position));
        startActivity(intent);


    }

    @Override
    public void onCategoryClick(String category) {

    }

   /* private void getRetrofitResponse() {
        MovieApi movieApi = Services.getMovieApi();

        Call<MovieSearchResponse> movieSearchResponseCall = movieApi
                .searchMovie(Credentials.API_KEY,
                        "Action",
                        1);

        movieSearchResponseCall.enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if (response.code() == 200) {
                    List<MovieModel> movieModels = new ArrayList<>(response.body().getMovies());
                    for (MovieModel movieModel : movieModels) {
                        Log.v("Main", "The movies titles are " + movieModel.getTitle());
                    }
                } else {
                    try {
                       // textView.setText("There was an error and the error is " + response.errorBody().string());
                        Log.v("Main","there was an error"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {

            }
        });
    }

    private void getRetrofitResponseBaseOnId() {
        MovieApi movieApi = Services.getMovieApi();
        Call<MovieModel> responseCall = movieApi.getMovie(550, Credentials.API_KEY);

        responseCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.code() == 200) {
                    MovieModel movieModel = response.body();
                    Log.v("Main", "The movie is " + movieModel.getTitle());
                } else {
                    try {
                        Log.v("Main", "Error " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {

            }
        });
    }*/

    private void setUpsearchView(){
        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                movieListVieModel.searchMovieApi(query,1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}