package android.example.MyFavoriteMovies.screens;

import android.content.Intent;
import android.example.MyFavoriteMovies.R;
import android.example.MyFavoriteMovies.adapters.ReviewAdapter;
import android.example.MyFavoriteMovies.adapters.TrailerAdapter;
import android.example.MyFavoriteMovies.pojo.FavouriteMovie;
import android.example.MyFavoriteMovies.model.MainViewModel;
import android.example.MyFavoriteMovies.pojo.Movie;
import android.example.MyFavoriteMovies.pojo.Review;
import android.example.MyFavoriteMovies.pojo.Trailer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener  {

    private ImageView imageViewAddToFavourite;
    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ScrollView scrollViewInfo;
    private TextView textViewLabelReviews;
    private TextView textViewLabelTrailers;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;

    private MainViewModel viewModel;

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
        setContentView(R.layout.activity_detail);
        lang = Locale.getDefault().getLanguage();
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewLabelReviews = findViewById(R.id.textViewLabelReviews);
        textViewLabelTrailers = findViewById(R.id.textViewLabelTrailers);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
        imageViewAddToFavourite.setOnClickListener(this);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.placeholder_large).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(String.valueOf(movie.getVoteAverage()));
        setFavourite();
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
        trailerAdapter.setOnTrailerClickListener(url -> {
            Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intentToTrailer);
        });
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);
        List<Trailer> trailers = new ArrayList<>();
        List<Review> reviews = new ArrayList<>();
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);
        viewModel.loadReviews(movie.getId(), lang);
        viewModel.loadTrailers(movie.getId(), lang);
        viewModel.getReviews().observe(this, reviewsFromNetwork -> reviewAdapter.setReviews(reviewsFromNetwork));

        viewModel.getTrailers().observe(this, trailersFromNetwork ->
                trailerAdapter.setTrailers(trailersFromNetwork));

        viewModel.getErrors().observe(this, throwable -> {
            if (throwable != null) {
                Toast.makeText(DetailActivity.this.getApplicationContext(), R.string.Error_download, Toast.LENGTH_SHORT).show();
                viewModel.clearErrors();
            }
        });

        if (trailers.isEmpty()) {
            textViewLabelTrailers.setVisibility(View.INVISIBLE);
        }
        if (reviews.isEmpty()) {
            textViewLabelReviews.setVisibility(View.INVISIBLE);
        }
        scrollViewInfo.smoothScrollTo(0,0);
    }

    @Override
    public void onClick(View v) { // onClickChangeFavorite
        if(favouriteMovie == null) {
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(DetailActivity.this, "" + getString(R.string.add_to_favorite), Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(DetailActivity.this, "" + getString(R.string.remove_to_favorite), Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }
}
