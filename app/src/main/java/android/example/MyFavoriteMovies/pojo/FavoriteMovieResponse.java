package android.example.MyFavoriteMovies.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoriteMovieResponse {
    @SerializedName("results")
    @Expose
    private List<FavouriteMovie> favoriteMovies = null;

    public List<FavouriteMovie> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void setFavoriteMovies(List<FavouriteMovie> favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }
}
