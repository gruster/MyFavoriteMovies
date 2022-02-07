package android.example.MyFavoriteMovies.api;

public abstract class StringsStore {
    public static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    public static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";
    public static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";

    public static final String PARAMS_API_KEY = "api_key";
    public static final String PARAMS_LANGUAGE = "language";
    public static final String PARAMS_SORT_BY = "sort_by";
    public static final String PARAMS_PAGE = "page";
    public static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";
    public static final String ID = "id";

    public static final String API_KEY = "51f2a81ba0d2fca696609194f5b2405d";
    public static final String SORT_BY_POPULARITY = "popularity.desc";
    public static final String SORT_BY_TOP_RATED = "vote_average.desc";
    public static final String MIN_VOTE_COUNT_VALUE = "1000";

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";
}
