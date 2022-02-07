package android.example.MyFavoriteMovies.model;

import android.app.Application;
import android.content.Context;
import android.example.MyFavoriteMovies.api.ApiFactory;
import android.example.MyFavoriteMovies.api.ApiService;
import android.example.MyFavoriteMovies.api.StringsStore;
import android.example.MyFavoriteMovies.pojo.FavouriteMovie;
import android.example.MyFavoriteMovies.data.MovieDatabase;
import android.example.MyFavoriteMovies.pojo.Movie;
import android.example.MyFavoriteMovies.pojo.Review;
import android.example.MyFavoriteMovies.pojo.Trailer;
import android.example.MyFavoriteMovies.utils.AppExecutorService;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    private LiveData<List<Movie>> movies;
    private LiveData<List<FavouriteMovie>> favouriteMovies;
    private MutableLiveData<List<Review>> reviews;
    private MutableLiveData<List<Trailer>> trailers;
    private static MutableLiveData<Throwable> errors;
    private static ExecutorService executor;
    private static CompositeDisposable compositeDisposable;
    private static ApiFactory apiFactory;
    private static ApiService apiService;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        executor = AppExecutorService.getInstance();
        favouriteMovies = database.movieDao().getAllFavouriteMovies();
        reviews = new MutableLiveData<>();
        trailers = new MutableLiveData<>();
        errors = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
        apiFactory = ApiFactory.getInstance();
        apiService = apiFactory.getApiService();
    }

    public Movie getMovieById(int id) {
        try {
            return executor.submit(new GetMovieTask(id)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FavouriteMovie getFavouriteMovieById(int id) {
        try {
            return executor.submit(new GetFavouriteMovieTask(id)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    public LiveData<List<Trailer>> getTrailers() {
        return trailers;
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }

    public void clearErrors (){
        errors.setValue(null);
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public static void deleteAllMovies() {
        executor.submit(new DeleteMoviesTask());
    }

    public static void insertMovie(Movie movie) {
        executor.submit(new InsertTask(movie));
    }

    public void deleteMovie(Movie movie) {
        executor.submit(new DeleteTask(movie));
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void insertFavouriteMovie(FavouriteMovie movie) {
        executor.submit(new InsertFavouriteTask(movie));
    }

    public void deleteFavouriteMovie(FavouriteMovie movie) {
        executor.submit(new DeleteFavouriteTask(movie));
    }

    private static class DeleteFavouriteTask implements Runnable {

        private FavouriteMovie movie;

        public DeleteFavouriteTask(FavouriteMovie movie) {
            this.movie = movie;
        }

        @Override
        public void run() {
            if (movie != null) {
                database.movieDao().deleteFavouriteMovie(movie);
            }
        }
    }

    private static class InsertFavouriteTask implements Runnable {

        private FavouriteMovie movie;

        public InsertFavouriteTask(FavouriteMovie movie) {
            this.movie = movie;
        }

        @Override
        public void run() {
            if (movie != null) {
                database.movieDao().insertFavouriteMovie(movie);
            }
        }
    }

    private static class DeleteTask implements Runnable {

        private Movie movie;

        public DeleteTask(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void run() {
            if (movie != null) {
                database.movieDao().deleteMovie(movie);
            }
        }
    }

    private static class InsertTask implements Runnable {

        private Movie movie;

        public InsertTask(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void run() {
            if (movie != null) {
                database.movieDao().insertMovie(movie);
            }
        }
    }

    private static class DeleteMoviesTask implements Runnable {
        @Override
        public void run() {
            database.movieDao().deleteAllMovies();
        }
    }

    private static class GetMovieTask implements Callable<Movie> {

        int id;
        public GetMovieTask(int id) {
            this.id = id;
        }

        @Override
        public Movie call() {
            return database.movieDao().getMovieById(id);
        }
    }

    private static class GetFavouriteMovieTask implements Callable<FavouriteMovie> {

        int id;

        public GetFavouriteMovieTask(int id) {
            this.id = id;
        }

        @Override
        public FavouriteMovie call() {
            return database.movieDao().getFavouriteMovieById(id);
        }
    }
    public static class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public interface OnStartLoadingListener {
            void onStartLoading();
        }

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        public MoviesLoader(@androidx.annotation.NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public List<Movie> loadInBackground() {
            String page = bundle.getString(StringsStore.PARAMS_PAGE);
            int sortBy = bundle.getInt(StringsStore.PARAMS_SORT_BY);
            String lang = bundle.getString(StringsStore.PARAMS_LANGUAGE);
            String methodOfSort;
            if (sortBy == StringsStore.POPULARITY) {
                methodOfSort = StringsStore.SORT_BY_POPULARITY;
            } else {
                methodOfSort = StringsStore.SORT_BY_TOP_RATED;
            }
            List<Movie> movies = new ArrayList<>();
            Disposable disposable = apiService
                    .getMovies(StringsStore.API_KEY, lang, methodOfSort, StringsStore.MIN_VOTE_COUNT_VALUE, page)
                    .subscribe(movieResponse -> {
                        movies.addAll(movieResponse.getMovies());
                        if (Integer.parseInt(page) == 1) {
                            deleteAllMovies();
                        }
                        for (Movie movie : movies) {
                            insertMovie(movie);
                        }
                    }, throwable ->
                            errors.postValue(throwable));
            compositeDisposable.add(disposable);
            return movies;
        }
    }

    public void loadReviews(int id, String lang) {
        Disposable disposable = apiService
                .getFavoriteMoviesReviews(id, lang, StringsStore.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviewResponse ->
                        reviews.setValue(reviewResponse.getReviews()),
                        throwable -> errors.setValue(throwable));
        compositeDisposable.add(disposable);
    }

    public void loadTrailers(int id, String lang) {
        Disposable disposable = apiService
                .getFavoriteMoviesVideos(id, lang, StringsStore.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviewResponse -> trailers.setValue(reviewResponse.getTrailers()),
                        throwable -> errors.setValue(throwable));
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
