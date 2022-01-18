package android.example.MyFavoriteMovies.utils;

import android.example.MyFavoriteMovies.data.Movie;
import android.example.MyFavoriteMovies.data.Review;
import android.example.MyFavoriteMovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    private static final String KEY_RESULTS = "results";

    // for Reviews
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";

    // for videos
    private static final String KEY_NAME = "name";
    private static final String KEY_KEY_OF_VIDEO = "key";
    private static final String BASE_URL_YOUTUBE = "https://www.youtube.com/watch?v=";

    // total info about movie
    private static final String KEY_ID = "id";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String SMALL_POSTER_SIZE = "w185";
    private static final String BIG_POSTER_SIZE = "w780";

    public static ArrayList<Review> getReviewsFromJson(JSONObject jsonObject){
        ArrayList<Review> reviews = new ArrayList<>();
        if(jsonObject == null){
            return reviews;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectReview = jsonArray.getJSONObject(i);
                String author = jsonObjectReview.getString(KEY_AUTHOR);
                String content = jsonObjectReview.getString(KEY_CONTENT);
                Review review = new Review(author, content);
                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  reviews;
    }

    public static ArrayList<Trailer> getTrailersFromJson(JSONObject jsonObject){
        ArrayList<Trailer> reviews = new ArrayList<>();
        if(jsonObject == null){
            return reviews;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectReview = jsonArray.getJSONObject(i);
                String name = jsonObjectReview.getString(KEY_NAME);
                String key = BASE_URL_YOUTUBE + jsonObjectReview.getString(KEY_KEY_OF_VIDEO);
                Trailer trailer = new Trailer(name, key);
                reviews.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  reviews;
    }

    public static ArrayList<Movie> getMoviesFromJson(JSONObject jsonObject){
        ArrayList<Movie> result = new ArrayList<>();
        if(jsonObject == null){
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);

                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
                result.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
