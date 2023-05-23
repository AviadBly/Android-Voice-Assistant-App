package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.*;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static androidx.core.content.ContextCompat.getSystemService;

public class Commands {

    private Context mContext;

    public Commands(Context context)
    {

        mContext=context;
    }

    public void showTimeMessage() {
        // Inflate the layout file

        LayoutInflater LayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View timeMessageView = LayoutInflater.inflate(R.layout.stylishlayout , null);

        // Find the TextView and set the time
        TextView timeTextView = timeMessageView.findViewById(R.id.time_text_view);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        String currentTime = sdf.format(new Date());
        timeTextView.setText(currentTime);

        // Show the message
        Toast toast = new Toast(mContext);
        toast.setView(timeMessageView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }



}
