package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class note extends AppCompatActivity {

    private Toolbar toolbar;
    private Button saveButton;
    private EditText titleText;
    private EditText bodyText;

    private String SPOT = "Position";
    private String AVAILABLE = "Available";

    public static final String SHARED_PREFS = "SharedPrefs";
    private String NOTE_KEY = "Note";

    public note() {

    }

    public void openExistingNote() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences.contains(NOTE_KEY)) {
            Intent mainIntent = getIntent();
            int position = mainIntent.getIntExtra(SPOT, 0);
            Gson gson = new Gson();
            String json = sharedPreferences.getString(NOTE_KEY, null);
            Type type = new TypeToken<ArrayList<String[]>>() {}.getType();
            ArrayList<String[]> noteBook = gson.fromJson(json, type);
            if(noteBook != null) {
                String title = noteBook.get(position)[0];
                String body = noteBook.get(position)[1];
                titleText.setText(title);
                bodyText.setText(body);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            CustomIntent.customType(this, "right-to-left");
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        CustomIntent.customType(this, "right-to-left");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        toolbar = findViewById(R.id.note_toolbar);
        saveButton = findViewById(R.id.saveButton);
        titleText = findViewById(R.id.noteTitle);
        bodyText = findViewById(R.id.noteBody);
        setSupportActionBar(toolbar);
        // This was to not have the toolBar text show in the corner
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent tempIntent = getIntent();
        boolean isAvailable = tempIntent.getBooleanExtra(AVAILABLE, false);
        if(isAvailable) {
            openExistingNote();
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String title = titleText.getText().toString();
                String body = bodyText.getText().toString();
                String[] note = new String[2];
                note[0] = title;
                note[1] = body;
                if(!sharedPreferences.contains(NOTE_KEY)) {
                    ArrayList<String[]> noteBook = new ArrayList<String[]>();
                    noteBook.add(note);
                    Gson gson = new Gson();
                    String json = gson.toJson(noteBook);
                    editor.putString(NOTE_KEY, json);
                    editor.apply();
                } else {
                    Gson gson = new Gson();
                    String json = sharedPreferences.getString(NOTE_KEY, null);
                    Type type = new TypeToken<ArrayList<String[]>>() {}.getType();
                    ArrayList<String[]> noteBook = new ArrayList<String[]>();
                    noteBook = gson.fromJson(json, type);
                    // see if later you can simplify this into one if statement not two.
                    if(noteBook != null) {
                        noteBook.add(note);
                        json = gson.toJson(noteBook);
                        editor.putString(NOTE_KEY, json);
                        editor.apply();
                    }
                }
                Toast.makeText(note.this, "Saved Correctly", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
