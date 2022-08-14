package com.elijah.ukeme.firebase.signup.movieapp.module;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.dynamicanimation.animation.SpringAnimation;

public class MovieModel implements Parcelable {
    private String title;
    private String poster_path;
    private String release_date;
    private int movie_id;
    private String movie_overview;
    private String original_language;
    private float vote_average;


    public MovieModel(String title, String poster_path, String release_date, int movie_id, String movie_overview, String original_language, float vote_average) {
        this.title = title;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.movie_id = movie_id;
        this.movie_overview = movie_overview;
        this.original_language = original_language;
        this.vote_average = vote_average;
    }

    protected MovieModel(Parcel in) {
        title = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        movie_id = in.readInt();
        movie_overview = in.readString();
        original_language = in.readString();
        vote_average = in.readFloat();
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public String getMovie_overview() {
        return movie_overview;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public float getVote_average() {
        return vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "MovieModel{" +
                "title='" + title + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", release_date='" + release_date + '\'' +
                ", movie_id=" + movie_id +
                ", movie_overview='" + movie_overview + '\'' +
                ", original_language='" + original_language + '\'' +
                ", vote_average=" + vote_average +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(release_date);
        dest.writeInt(movie_id);
        dest.writeString(movie_overview);
        dest.writeString(original_language);
        dest.writeFloat(vote_average);
    }
}
