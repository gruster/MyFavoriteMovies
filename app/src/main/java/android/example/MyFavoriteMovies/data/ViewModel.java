package android.example.MyFavoriteMovies.data;

import android.app.Application;
import android.example.MyFavoriteMovies.utils.AppExecutorService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class ViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    private LiveData<List<Movie>> movies;
    private LiveData<List<FavoriteMovie>> favoriteMovies;
    private ExecutorService executor;

    public ViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        executor = AppExecutorService.getInstance();
        favoriteMovies = database.movieDao().getAllFavoriteMovies();
    }

    public LiveData<List<Movie>> getMovies() { return movies; }

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

    public FavoriteMovie getFavoriteMovieById(int id) {
        try {
            return executor.submit(new GetFavoriteMovieTask(id)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void insertFavoriteMovie(FavoriteMovie movie){
        executor.submit(new InsertFavoriteMovieTask(movie));
    }

    public void deleteFavoriteMovie(FavoriteMovie movie){
        executor.submit(new DeleteFavoriteMovieTask(movie));
    }

    public void deleteAllMovies() {
        executor.submit(new DeleteMoviesTask());
    }

    public void deleteMovie(Movie movie) {
        executor.submit(new DeleteTask(movie));
    }

    public void insertMovie(Movie movie) {
        executor.submit(new InsertMovieTask(movie));
    }

    private static class InsertMovieTask implements Runnable {

        private Movie movie;
        public InsertMovieTask(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void run() {
            if(movie != null) {
                database.movieDao().insertMovie(movie);
            }
        }
    }
    private static class InsertFavoriteMovieTask implements Runnable {

        private FavoriteMovie movie;
        public InsertFavoriteMovieTask(FavoriteMovie movie) {
            this.movie = movie;
        }

        @Override
        public void run() {
            if(movie != null) {
                database.movieDao().insertFavoriteMovie(movie);
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
            if(movie != null) {
                database.movieDao().deleteMovie(movie);
            }
        }
    }
    private static class DeleteFavoriteMovieTask implements Runnable {

        private FavoriteMovie movie;
        public DeleteFavoriteMovieTask(FavoriteMovie movie) {
            this.movie = movie;
        }

        @Override
        public void run() {
            if(movie != null) {
                database.movieDao().deleteFavoriteMovie(movie);
            }
        }
    }

    private static class DeleteMoviesTask implements Runnable {
        @Override
        public void run() {
            database.movieDao().deleteAllFromMovies();
        }
    }

    private static class GetMovieTask implements Callable<Movie> {

        int id;
        public GetMovieTask(int id) {
            this.id = id;
        }

        @Override
        public Movie call() {
            if (id != 0) {
                return database.movieDao().getMovieById(id);
            }
            return null;
        }
    }

    private static class GetFavoriteMovieTask implements Callable<FavoriteMovie> {

        int id;
        public GetFavoriteMovieTask(int id) {
            this.id = id;
        }

        @Override
        public FavoriteMovie call() {
            if (id != 0) {
                return database.movieDao().getFavoriteMovieById(id);
            }
            return null;
        }
    }
}
