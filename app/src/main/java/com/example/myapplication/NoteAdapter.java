package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The NoteAdapter class is a custom ArrayAdapter used to populate a ListView with Note objects.
 */
public class NoteAdapter extends ArrayAdapter<Note> {
    /**
     * Constructs a new NoteAdapter.
     *
     * @param context the context in which the adapter is used
     * @param notes   the list of notes to be displayed
     */
    public NoteAdapter(Context context, List<Note> notes) {
        super(context, 0, notes);
    }

    /**
     * Gets a View that displays the data at the specified position in the list.
     *
     * @param position    the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent      the parent that this view will eventually be attached to
     * @return the View corresponding to the data at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the view if it's null
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Get the TextView for the note text
        TextView textView = convertView.findViewById(android.R.id.text1);

        // Get the note object at the specified position
        Note note = getItem(position);

        // Set the text of the TextView to the note's string representation
        if (note != null) {
            textView.setText(note.toString());
        }

        return convertView;
    }
}