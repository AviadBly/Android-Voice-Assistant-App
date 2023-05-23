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
    private List<Note> notes;
    private ListView notesListView;
    private NoteAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notesListView = findViewById(R.id.notes_list_view);
        notes = loadNotes();
        notesAdapter = new NoteAdapter(this, notes);
        notesListView.setAdapter(notesAdapter);

        FloatingActionButton addNoteButton = findViewById(R.id.add_note_button);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteDialog();
            }
        });

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditNoteDialog(notes.get(position), position);
            }
        });
    }
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
    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Note");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(6);
        input.setMaxLines(6);
        builder.setView(input);

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

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }

    private void showEditNoteDialog(final Note note, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(6);
        input.setMaxLines(6);
        input.setText(note.getText());
        builder.setView(input);

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
                    Toast.makeText(NotesActivity.this,"confirm", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
// do nothing
            }
        });
        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteFromList(position);
                saveNotes();
            }
        });

        builder.show();
    }

    private void addNoteToList(Note note) {
        notes.add(note);
        notesAdapter.notifyDataSetChanged();
    }

    private void updateNoteInList(Note note, int position) {
        notes.set(position, note);
        notesAdapter.notifyDataSetChanged();
    }

    private void deleteNoteFromList(int position) {
        notes.remove(position);
        notesAdapter.notifyDataSetChanged();
    }

    private List<Note> loadNotes() {
        List<Note> notes = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput("notes.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                Note note = new Note();
                note.setText(line);
                notes.add(note);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notes;
    }

    private void saveNotes() {
        try {
            FileOutputStream fos = openFileOutput("notes.txt", Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos));
            for (Note note : notes) {
                pw.println(note.getText());
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
