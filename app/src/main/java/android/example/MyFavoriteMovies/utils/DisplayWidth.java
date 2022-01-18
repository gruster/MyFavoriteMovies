package android.example.MyFavoriteMovies.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayWidth {

    private static DisplayMetrics displayMetrics;

    public static int getColumnCount(Context context){
        if(displayMetrics == null){
            displayMetrics = new DisplayMetrics();
        }
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return Math.max(width / 185, 2);
    }
}
