package android.example.MyFavoriteMovies.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.MyFavoriteMovies.R;
import android.example.MyFavoriteMovies.adapters.ReviewAdapter;
import android.example.MyFavoriteMovies.adapters.TrailerAdapter;
import android.example.MyFavoriteMovies.data.FavoriteMovie;
import android.example.MyFavoriteMovies.data.Movie;
import android.example.MyFavoriteMovies.data.Review;
import android.example.MyFavoriteMovies.data.Trailer;
import android.example.MyFavoriteMovies.data.ViewModel;
import android.example.MyFavoriteMovies.utils.JsonUtils;
import android.example.MyFavoriteMovies.utils.NetworkUtils;
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

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ScrollView scrollViewInfo;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private ImageView imageViewAddToFavorite;
    private FavoriteMovie favoriteMovie;
    private Movie movie;
    private int id;
    private String lang;

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
        setContentView(R.layout.activity_detail);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        imageViewAddToFavorite = findViewById(R.id.imageViewAddToFavorite);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);
        lang = Locale.getDefault().getLanguage();
        imageViewAddToFavorite.setOnClickListener(this);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("id")){
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.placeholder_large).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewRating.setText(String.valueOf(movie.getVoteAverage()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
        setFavoriteMovie();
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();

        trailerAdapter.setOnTrailerClickListener(url -> {
        Intent intentToWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intentToWeb);
        });
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setHasFixedSize(true);
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setHasFixedSize(true);
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJsonForVideos(movie.getId(), lang);
        JSONObject jsonObjectReviews = NetworkUtils.getJsonForReviews(movie.getId(), lang);
        ArrayList<Trailer> trailers = JsonUtils.getTrailersFromJson(jsonObjectTrailers);
        ArrayList<Review> reviews = JsonUtils.getReviewsFromJson(jsonObjectReviews);
        trailerAdapter.setTrailers(trailers);
        reviewAdapter.setReviews(reviews);
        scrollViewInfo.smoothScrollTo(0, 0);
    }

    @Override
    public void onClick(View v) { // onClickChangeFavorite
        if(favoriteMovie == null) {
            viewModel.insertFavoriteMovie(new FavoriteMovie(movie));
            Toast.makeText(DetailActivity.this, "" + getString(R.string.add_to_favorite), Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavoriteMovie(favoriteMovie);
            Toast.makeText(DetailActivity.this, "" + getString(R.string.remove_to_favorite), Toast.LENGTH_SHORT).show();
        }
        setFavoriteMovie();
    }

    private void setFavoriteMovie(){
        favoriteMovie = viewModel.getFavoriteMovieById(id);
        if(favoriteMovie == null) {
            imageViewAddToFavorite.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavorite.setImageResource(R.drawable.favourite_remove);
        }
    }
}