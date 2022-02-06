package com.example.tinkoffproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private ImageView gifImage;
    private final ImageLoader imageLoader = new ImageLoader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gifImage = findViewById(R.id.image_gif);
        ImageButton btnNext = findViewById(R.id.button_next);
        ImageButton btnPrevious = findViewById(R.id.button_previous);
        ToggleButton latest = findViewById(R.id.latest);
        ToggleButton top = findViewById(R.id.top);
        ToggleButton hot = findViewById(R.id.hot);
        latest.setChecked(true);
        imageLoader.showNextImage(gifImage);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLoader.showNextImage(gifImage);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLoader.showPreviousImage(gifImage);
            }
        });
        latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = ((ToggleButton)view).isChecked();
                if (state = false){
                    return;
                }
                imageLoader.switchCategory(ImageLoader.CategoryType.LATEST);
                imageLoader.showCurrentImage(gifImage);
                hot.setChecked(false);
                top.setChecked(false);
            }
        });
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = ((ToggleButton)view).isChecked();
                if (state = false){
                    return;
                }
                imageLoader.switchCategory(ImageLoader.CategoryType.TOP);
                imageLoader.showCurrentImage(gifImage);
                hot.setChecked(false);
                latest.setChecked(false);
            }
        });
        hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = ((ToggleButton)view).isChecked();
                if (state = false){
                    return;
                }
                imageLoader.switchCategory(ImageLoader.CategoryType.HOT);
                imageLoader.showCurrentImage(gifImage);
                latest.setChecked(false);
                top.setChecked(false);
            }
        });
    }
}