package com.example.myapplication;

import java.io.Serializable;

public class Note {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isValid() {
        String[] lines = text.split("\n");
        return lines.length <= 6;
    }

    @Override
    public String toString() {
        String[] lines = text.split("\n");
        if (lines.length > 0) {
            return lines[0];
        } else {
            return "";
        }
    }
}

