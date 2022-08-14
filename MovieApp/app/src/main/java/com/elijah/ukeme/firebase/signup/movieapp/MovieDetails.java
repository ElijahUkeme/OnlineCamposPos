package com.elijah.ukeme.firebase.signup.movieapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elijah.ukeme.firebase.signup.movieapp.module.MovieModel;

public class MovieDetails extends AppCompatActivity {

    private ImageView image;
    private TextView details, description;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        image=findViewById(R.id.imageviewDetails);
        details=findViewById(R.id.textviewDetails);
        description=findViewById(R.id.textviewDetailsDescription);
        ratingBar=findViewById(R.id.ratingBarDetails);

        getDataFromIntent();
    }

    private void getDataFromIntent(){
        if (getIntent().hasExtra("movie")){
            MovieModel movieModel = getIntent().getParcelableExtra("movie");

            details.setText(movieModel.getTitle());
            description.setText(movieModel.getMovie_overview());
            ratingBar.setRating(movieModel.getVote_average());

            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500/"+movieModel.getPoster_path())
                    .into(image);

        }

    }
}