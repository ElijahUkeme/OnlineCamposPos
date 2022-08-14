package com.elijah.ukeme.firebase.signup.movieapp.request;

import com.elijah.ukeme.firebase.signup.movieapp.utils.Credentials;
import com.elijah.ukeme.firebase.signup.movieapp.utils.MovieApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Services {
    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(Credentials.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static MovieApi movieApi = retrofit.create(MovieApi.class);

    public static MovieApi getMovieApi(){
        return movieApi;
    }
}
