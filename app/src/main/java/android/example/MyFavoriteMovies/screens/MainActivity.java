package android.example.MyFavoriteMovies.screens;

import android.content.Intent;
import android.example.MyFavoriteMovies.R;
import android.example.MyFavoriteMovies.adapters.MovieAdapter;
import android.example.MyFavoriteMovies.api.StringsStore;
import android.example.MyFavoriteMovies.model.MainViewModel;
import android.example.MyFavoriteMovies.pojo.Movie;
import android.example.MyFavoriteMovies.utils.DisplayWidth;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private SwitchCompat switchSort;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private TextView textViewTopRated;
    private TextView textViewPopularity;
    private ProgressBar progressBarLoading;

    private MainViewModel viewModel;

    private static final int LOADER_ID = 133;
    private LoaderManager loaderManager;

    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoading = false;

    private static String lang;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavorite:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lang = Locale.getDefault().getLanguage();
        loaderManager = LoaderManager.getInstance(this);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        switchSort = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, DisplayWidth.getColumnCount(this)));
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener((buttonView, isChecked) -> {
            page = 1;
            setMethodOfSort(isChecked);
        });
        switchSort.setChecked(false);
        movieAdapter.setOnPosterClickListener(position -> {
            Movie movie = movieAdapter.getMovies().get(position);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("id", movie.getId());
            startActivity(intent);
        });

        textViewPopularity.setOnClickListener(v -> {
            setMethodOfSort(false);
            switchSort.setChecked(false);
        });

        textViewTopRated.setOnClickListener(v -> {
            setMethodOfSort(true);
            switchSort.setChecked(true);
        });

        movieAdapter.setOnReachEndListener(() -> {
            if (!isLoading) {
                downloadData(methodOfSort, page);
            }
        });
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, movies -> {
            if (page == 1) {
                movieAdapter.setMovies(movies);
            }
        });
        viewModel.getErrors().observe(this, throwable -> {
            if (throwable != null) {
                Toast.makeText(getApplicationContext(), R.string.Error_download, Toast.LENGTH_SHORT).show();
                viewModel.clearErrors();
            }
        });
    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            methodOfSort = StringsStore.TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.red_400));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white));
        } else {
            methodOfSort = StringsStore.POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.red_400));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white));
        }
        downloadData(methodOfSort, page);
    }

    private void downloadData(int methodOfSort, int page) {
        Bundle bundle = new Bundle();
        bundle.putInt(StringsStore.PARAMS_SORT_BY, methodOfSort);
        bundle.putString(StringsStore.PARAMS_PAGE, String.valueOf(page));
        bundle.putString(StringsStore.PARAMS_LANGUAGE, lang);
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        MainViewModel.MoviesLoader moviesLoader = new MainViewModel.MoviesLoader(this, args);
        moviesLoader.setOnStartLoadingListener(() -> {
            isLoading = true;
            progressBarLoading.setVisibility(View.VISIBLE);
        });
        return moviesLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> moviesFromNetwork) {
        if (moviesFromNetwork != null && !moviesFromNetwork.isEmpty()) {
            if (page == 1) {
                movieAdapter.clear();
            }
            movieAdapter.addMovies(moviesFromNetwork);
            page++;
        }
        isLoading = false;
        progressBarLoading.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@androidx.annotation.NonNull Loader<List<Movie>> loader) {

    }
}

























