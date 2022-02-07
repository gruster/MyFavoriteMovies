package android.example.MyFavoriteMovies.api;

import android.example.MyFavoriteMovies.pojo.MovieResponse;
import android.example.MyFavoriteMovies.pojo.ReviewResponse;
import android.example.MyFavoriteMovies.pojo.TrailerResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("discover/movie")
    public Observable<MovieResponse> getMovies(
            @Query(StringsStore.PARAMS_API_KEY) String api_key,
            @Query(StringsStore.PARAMS_LANGUAGE) String lang,
            @Query(StringsStore.PARAMS_SORT_BY) String sortBy,
            @Query(StringsStore.PARAMS_MIN_VOTE_COUNT) String voteCount,
            @Query(StringsStore.PARAMS_PAGE) String page
    );

    @GET("movie/{id}/reviews")
    public Observable<ReviewResponse> getFavoriteMoviesReviews(
            @Path("id") int id,
            @Query(StringsStore.PARAMS_LANGUAGE) String lang,
            @Query(StringsStore.PARAMS_API_KEY) String api_key
    );

    @GET("movie/{id}/videos")
    public Observable<TrailerResponse> getFavoriteMoviesVideos(
            @Path(StringsStore.ID) int id,
            @Query(StringsStore.PARAMS_LANGUAGE) String lang,
            @Query(StringsStore.PARAMS_API_KEY) String api_key
    );
}
