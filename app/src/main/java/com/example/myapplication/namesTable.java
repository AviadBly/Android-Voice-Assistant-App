package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class namesTable extends AppCompatActivity {

    // This table shows a list of names and the amount of times they used voice assistant
        private DBHelper dbHelper;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_names_table);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Initialize DBHelper
            dbHelper = new DBHelper(this);

            // Get a reference to the ListView
            ListView namesListView = findViewById(R.id.namesListView);

            // Fetch the data from the database
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME, null);

            // Create a SimpleCursorAdapter to populate the ListView
            String[] fromColumns = {DBHelper.COLUMN_NAME, DBHelper.COLUMN_COUNT};
            int[] toViews = {R.id.nameTextView, R.id.countTextView};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.item_name_count,
                    cursor,
                    fromColumns,
                    toViews,
                    0
            );

            // Set the adapter on the ListView
            namesListView.setAdapter(adapter);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // Close the database connection
            dbHelper.close();
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