package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsActivity extends AppCompatActivity {

    public TextView headlineTextView;
    public TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        headlineTextView = findViewById(R.id.headline_text_view);
        descriptionTextView=findViewById(R.id.description_text_view);

        loadHeadlineFromFile();
    }

    private void loadHeadlineFromFile() {
        try {
            // Open the text file
            InputStream inputStream = openFileInput("headline.txt");

            // Read the content from the text file
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String title = reader.readLine();
            StringBuilder descriptionBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                descriptionBuilder.append(line);
                descriptionBuilder.append("\n");
            }
            reader.close();

            // Update the UI with the loaded data
            String description = descriptionBuilder.toString();
            runOnUiThread(() -> {
                headlineTextView.setText(title);
                descriptionTextView.setText(description);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish(); // optional, depending on your desired behavior
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

