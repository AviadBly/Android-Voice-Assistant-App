package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.OkHttpClient;

public class NewsJobService extends JobService {
    private boolean jobCancelled = false;
    public String NewsTitle="";
    @Override

    public boolean onStartJob(JobParameters params) {
        Log.d("hi","Job Started");
        // Perform the task of fetching the main headline and sending a notification here
        doBackgroundWork(params);

        // Return false if the job is completed, otherwise true if it needs rescheduling
        return true;
    }

    public boolean onStopJob(JobParameters params) {
        Log.d("hi", "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Example code to fetch the main headline
                fetchMainHeadline();

                // Example code to send a notification
                sendNotification(NewsTitle);

                jobFinished(params, false);
            }
        }).start();
    }


    private void fetchMainHeadline() {
        String apiKey = "b2b668bfb0b694031b213444bde96d35";
        String url = "https://gnews.io/api/v4/top-headlines?token=" + apiKey + "&lang=en&category=general&country=us";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray articles = jsonResponse.getJSONArray("articles");

                if (articles.length() > 0) {
                    JSONObject article = articles.getJSONObject(0);
                    String title = article.getString("title");
                    String description = article.getString("description");

                    // Do something with the article information
                    System.out.println("Title: " + title);
                    System.out.println("Description: " + description);
                    System.out.println("------------------------------------");

                    // Clear the file and write the new headline
                    clearTextFileContent();
                    writeHeadlineToFile(title, description);

                    // Send notification
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.postDelayed(() -> sendNotification(title), 1000); // Delay sending the notification

                }
            } else {
                // Handle the error response
                System.out.println("Error: " + response.code() + " - " + response.message());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearTextFileContent() {
        try {
            // Open the text file in write mode
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("headline.txt", Context.MODE_PRIVATE));

            // Clear the content by writing an empty string
            writer.write("");

            // Close the writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeadlineToFile(String title, String description) {
        try {
            // Open the text file in append mode
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("headline.txt", Context.MODE_APPEND));

            // Write the title to the file
            writer.write(title + "\n");

            // Write the description to the file
            writer.write(description);

            // Close the writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String message) {
        // Notification manager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Notification channel ID (fixed value)
        String channelId = "my_channel_id";

        // Check if running on Android Oreo (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Get the notification channel by ID
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                // If the channel does not exist, create it
                channel = new NotificationChannel(
                        channelId,
                        "Channel Name",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Channel Description");
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.duck)
                .setContentTitle("Main headline:")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        notificationManager.notify(0, builder.build());
    }
}
