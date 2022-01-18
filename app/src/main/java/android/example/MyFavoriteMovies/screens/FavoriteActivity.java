package android.example.MyFavoriteMovies.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.MyFavoriteMovies.R;
import android.example.MyFavoriteMovies.data.FavoriteMovie;
import android.example.MyFavoriteMovies.data.Movie;
import android.example.MyFavoriteMovies.data.ViewModel;
import android.example.MyFavoriteMovies.adapters.MovieAdapter;
import android.example.MyFavoriteMovies.utils.DisplayWidth;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavoriteMovies;
    private MovieAdapter movieAdapter;
    private ViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavorite:
                Intent intentToFavorite = new Intent(this, FavoriteActivity.class);
                startActivity(intentToFavorite);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        recyclerViewFavoriteMovies = findViewById(R.id.recyclerViewFavoriteMovies);
        recyclerViewFavoriteMovies.setLayoutManager(new GridLayoutManager(this, DisplayWidth.getColumnCount(this)));
        recyclerViewFavoriteMovies.setHasFixedSize(true);
        movieAdapter = new MovieAdapter();
        recyclerViewFavoriteMovies.setAdapter(movieAdapter);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        LiveData<List<FavoriteMovie>> favoriteMovies = viewModel.getFavoriteMovies();
        favoriteMovies.observe(this, observeFavoriteMovies -> {
            if (observeFavoriteMovies != null) {
                List<Movie> movies = new ArrayList<>(observeFavoriteMovies);
                movieAdapter.setMovies(movies);
            }
        });

        movieAdapter.setOnPosterClickListener(position -> {
            Movie movie = movieAdapter.getMovies().get(position);
            Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
            intent.putExtra("id", movie.getId());
            startActivity(intent);
        });
    }
}