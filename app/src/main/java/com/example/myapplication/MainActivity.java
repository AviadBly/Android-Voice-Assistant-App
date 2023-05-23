package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView commandsList;
    ArrayAdapter<String> adapter;
    private AlanButton alanButton;
    private Context mContext;
    static int count=0;
    private static final int JOB_ID = 123; // Unique ID for the job


    private static final String DATABASE_NAME = "name_counter.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "name_counter";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COUNT = "count";
    public static String savedName="";
    private static final int REQUEST_IMAGE_CAPTURE1 =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isJobScheduled()) {
            scheduleJob();
        }

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        AlanConfig config = AlanConfig.builder().setProjectId("18a0240371cc92c179756b84f86e4c822e956eca572e1d8b807a3e2338fdd0dc/stage").build();
        alanButton = findViewById(R.id.Alan_button);
        alanButton.initWithConfig(config);
        mContext=this;
        Commands commands1 = new   Commands(mContext);
        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        savedName = prefs.getString("name", "");
        String command = "change name to " + savedName; // Construct the command
        if (!savedName.isEmpty()) {
            // Check if the name exists in the table
            String selectQuery = "SELECT " + COLUMN_COUNT + " FROM " + TABLE_NAME
                    + " WHERE " + COLUMN_NAME + " = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{savedName});

            if (cursor.moveToFirst()) {
                // Name exists in the table, retrieve its count
               count = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT));
                // Use the count as needed
            } else {
                // Name doesn't exist in the table, insert it with count = 0
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, savedName);
                values.put(COLUMN_COUNT, 0);
                db.insert(TABLE_NAME, null, values);
               count=0;
            }

            cursor.close();
        }
        alanButton.playText(command);

        AlanCallback alanCallback = new AlanCallback() {
            /// Handling commands from Alan Studio


            @Override
            public void onCommand(final EventCommand eventCommand) {
                try {
                    JSONObject command = eventCommand.getData();
                    JSONObject dataObj = command.getJSONObject("data");
                    String commandName = dataObj.getString("command");
                    String dataStr;
                    System.out.println(commandName);
                    System.out.println(dataObj);

                    count++; // Increment the count value

                    if (!savedName.isEmpty()) {
                        // Update the count column for the current name
                        ContentValues values = new ContentValues();
                        values.put(COLUMN_COUNT, count);

                        String whereClause = COLUMN_NAME + " = ?";
                        String[] whereArgs = {savedName};

                        db.update(TABLE_NAME, values, whereClause, whereArgs);
                    }

                    // Based on commandName, perform different tasks
                    switch (commandName)
                    {
                        case "show_time":
                            commands1.showTimeMessage();
                            break;

                        case "show_weather":
                            openWeather();
                            Thread.sleep(2000);
                            alanButton.deactivate();
                            break;
                        case "send_weather_data":
                             dataStr = dataObj.getString("data"); // get the "data" field as a string
                             openWeather(dataStr);
                            Thread.sleep(2000);
                            alanButton.deactivate();
                            break;

                        case "open_notes":
                            openNotes();
                            Thread.sleep(2000);
                            alanButton.deactivate();
                            break;
                        case "start_timer":
                            openTimerDefiner();
                            Thread.sleep(2000);
                            alanButton.deactivate();
                            break;

                        case "show_news":
                            openNews();
                            Thread.sleep(2000);
                            alanButton.deactivate();
                            break;

                        case "set_name":
                            String nameData = dataObj.getString("data"); // get the "data" field as a string
                            SharedPreferences.Editor editor = prefs.edit();
                            // Put a string value into the shared preferences
                            editor.putString("name", nameData);
                            // Commit the changes to the shared preferences
                            editor.apply();
                            savedName=nameData;
                            Thread.sleep(2000);
                            alanButton.deactivate();
                            break;
                        case "start_timer_timed":
                            dataStr = dataObj.getString("data"); // get the "data" field as a string

                            String[] timeArray = dataStr.split(":"); // split timeString into an array of strings

                            int minutes1 = Integer.parseInt(timeArray[1]); // parse the second element as minutes
                            int seconds = Integer.parseInt(timeArray[2]); // parse the third element as seconds
                            openTimerDefinerWithVariables(minutes1,seconds);
                            Thread.sleep(2000);
                            alanButton.deactivate();
                            break;
                        case "open_chart":
                            openChart();
                            Thread.sleep(2000);
                            alanButton.deactivate();
                            break;
                    }
                } catch (JSONException | InterruptedException e) {
                    Log.e("AlanButton", e.getMessage());
                }
            }
        };

        /// Registering callbacks
        alanButton.registerCallback(alanCallback);


        String[] commands = new String[] {

                "What Time is it",
                "Show News",
                "Start timer",
                "What is the weather",
                "Open Notes",
                "open chart"

        };


        commandsList=findViewById(R.id.commands);
        // Create a List from String Array elements
        final List<String> itemsList = new ArrayList<String>(Arrays.asList(commands));

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, R.layout.list_item_custom, itemsList);




        // DataBind ListView with items from ArrayAdapter
        commandsList.setAdapter(arrayAdapter);

        commandsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if(position==0)
                {
                    commands1.showTimeMessage();

                }
                if(position==1)
                {
                    openNews();
                }
                if(position==2)
                {
                    openTimerDefiner();
                }
                if(position==3)
                {
                    openWeather();
                }
                if(position==4)
                {
                    openNotes();

                }
                if(position==5)
                {
                    openChart();
                }

            }
        });
    }
    private boolean isJobScheduled() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
                if (jobInfo.getId() == JOB_ID) {
                    return true;
                }
            }
        }
        return false;
    }
    private void scheduleJob() {
        ComponentName componentName = new ComponentName(this, NewsJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setPeriodic(24 * 60 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("hi", "Job scheduled");
        } else {
            Log.d("hi", "Job scheduling failed");
        }
    }


    public  void openTimerDefiner(){
        Intent intent= new Intent(this, TimerDefiner.class);

        startActivity(intent);
        finish();
    }

    public void openNews(){
        Intent intent= new Intent(this, NewsActivity.class);
        startActivity(intent);
        finish();
    }
    public void openChart()
    {
        Intent intent= new Intent(this, namesTable.class);
        startActivity(intent);
        finish();
    }

    public  void openTimerDefinerWithVariables(int minutes, int seconds){
        Intent intent= new Intent(this, TimerDefiner.class);
        intent.putExtra("minutes", minutes); // add the hours value as an extra to the intent
        intent.putExtra("seconds", seconds); // add the minutes value as an extra to the intent
        startActivity(intent);
        finish();
    }


    public void openWeather(String city)
    {


        // Create an intent and pass the input city name to the WeatherActivity
        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
        intent.putExtra("cityName", city);
        startActivity(intent);
        finish();
    }

    public void openWeather() {
        // Create a dialog to ask for the city name
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter City Name");

        // Add an EditText view to the dialog to allow the user to input the city name
        final EditText input = new EditText(this);
        builder.setView(input);

        // Add the positive button to the dialog to allow the user to submit the city name
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the input city name
                String cityName = input.getText().toString();

                // Create an intent and pass the input city name to the WeatherActivity
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra("cityName", cityName);
                startActivity(intent);
                finish();
            }
        });
        // Add the negative button to the dialog to allow the user to cancel the input
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }

    public void openNotes()
    {
        Intent intent= new Intent(this, NotesActivity.class);

        startActivity(intent);
        finish();
    }


}