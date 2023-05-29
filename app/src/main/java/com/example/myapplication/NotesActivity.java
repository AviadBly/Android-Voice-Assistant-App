package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private List<Note> notes;  // List to store the notes
    private ListView notesListView;  // ListView to display the notes
    private NoteAdapter notesAdapter;  // Custom ArrayAdapter for the notes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the ListView and load the notes
        notesListView = findViewById(R.id.notes_list_view);
        notes = loadNotes();
        notesAdapter = new NoteAdapter(this, notes);
        notesListView.setAdapter(notesAdapter);

        // Set up the "Add Note" button
        FloatingActionButton addNoteButton = findViewById(R.id.add_note_button);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteDialog();
            }
        });

        // Set up click listener for the notes in the ListView
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditNoteDialog(notes.get(position), position);
            }
        });
    }

    /**
     * Handle the selection of menu items.
     *
     * @param item the selected menu item
     * @return true if the item is handled, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate back to the MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show a dialog for adding a new note.
     */
    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Note");

        // Create an EditText for the user to enter the note text
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(6);
        input.setMaxLines(6);
        builder.setView(input);

        // Set up the "OK" button to add the note
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                Note note = new Note();
                note.setText(text);
                if (note.isValid()) {
                    addNoteToList(note);
                    saveNotes();
                } else {
                    Toast.makeText(NotesActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Set up the "Cancel" button
        builder.setNegativeButton(android.R.string.cancel, null);

        // Show the dialog
        builder.show();
    }

    /**
     * Show a dialog for editing an existing note.
     *
     * @param note     the note to edit
     * @param position the position of the note in the list
     */
    private void showEditNoteDialog(final Note note, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");

        // Create an EditText with the note text pre-filled
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(6);
        input.setMaxLines(6);
        input.setText(note.getText());
        builder.setView(input);

        // Set up the "OK" button to update the note
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                Note updatedNote = new Note();
                updatedNote.setText(text);
                if (updatedNote.isValid()) {
                    updateNoteInList(updatedNote, position);
                    saveNotes();
                } else {
                    Toast.makeText(NotesActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Set up the "Cancel" button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        // Set up the "Delete" button to delete the note
        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteFromList(position);
                saveNotes();
            }
        });

        // Show the dialog
        builder.show();
    }

    /**
     * Add a note to the list.
     *
     * @param note the note to add
     */
    private void addNoteToList(Note note) {
        notes.add(note);
        notesAdapter.notifyDataSetChanged();
    }

    /**
     * Update a note in the list.
     *
     * @param note     the updated note
     * @param position the position of the note in the list
     */
    private void updateNoteInList(Note note, int position) {
        notes.set(position, note);
        notesAdapter.notifyDataSetChanged();
    }

    /**
     * Delete a note from the list.
     *
     * @param position the position of the note in the list
     */
    private void deleteNoteFromList(int position) {
        notes.remove(position);
        notesAdapter.notifyDataSetChanged();
    }

    /**
     * Load the notes from the file.
     *
     * @return the list of loaded notes
     */
    private List<Note> loadNotes() {
        List<Note> notes = new ArrayList<>();
        try {
            // Open the file for reading
            FileInputStream fis = openFileInput("notes.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            // Read each line and create a Note object
            String line;
            while ((line = br.readLine()) != null) {
                Note note = new Note();
                note.setText(line);
                notes.add(note);
            }

            // Close the file
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notes;
    }

    /**
     * Save the notes to the file.
     */
    private void saveNotes() {
        try {
            // Open the file for writing
            FileOutputStream fos = openFileOutput("notes.txt", Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos));

            // Write each note to the file
            for (Note note : notes) {
                pw.println(note.getText());
            }

            // Close the file
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
