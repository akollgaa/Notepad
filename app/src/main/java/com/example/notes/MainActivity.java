package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<exampleItem> mExampleList;
    private ArrayList<Integer> mSelectedList;
    private Boolean isSelecting = false;

    private FloatingActionButton addNote;
    private String SPOT = "Position";
    private String AVAILABLE = "Available";

    public static final String SHARED_PREFS = "SharedPrefs";
    private String NOTE_KEY = "Note";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addNote = findViewById(R.id.add_note_button);
        mSelectedList = new ArrayList<Integer>();

        Toolbar toolbar = findViewById(R.id.notebook_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        loadData();
        buildRecyclerView(R.layout.example_item);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem("Title", "description");
                openNote(mExampleList.size()-1, false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAll :
                // Create a dialog alert message to confirm you want to delete all notes.
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                mExampleList.clear();
                mAdapter.notifyDataSetChanged();
                editor.clear();
                editor.apply();
                break;
            case R.id.selectDelete :
                showSelect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSelect() {
        // Create some sort of delete all or confirm button. Then a undo or unselect button.
        isSelecting = true;
        buildRecyclerView(R.layout.example_item_selected);
        FloatingActionButton addNote = findViewById(R.id.add_note_button);
        addNote.setVisibility(View.INVISIBLE);
        addNote.setEnabled(false);
        BottomNavigationView selectNav = findViewById(R.id.selectNav);
        selectNav.setVisibility(View.VISIBLE);
        selectNav.setEnabled(true);
        selectNav.setOnNavigationItemSelectedListener(navListener);
    }

    private void destroySelectNavigation() {
        BottomNavigationView selectNav = findViewById(R.id.selectNav);
        FloatingActionButton addNote = findViewById(R.id.add_note_button);
        selectNav.setEnabled(false);
        selectNav.setVisibility(View.INVISIBLE);
        addNote.setEnabled(true);
        addNote.setVisibility(View.VISIBLE);
        buildRecyclerView(R.layout.example_item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.cancel :
                            isSelecting = false;
                            selectBox(false);
                            destroySelectNavigation();
                            break;
                        case R.id.selectAll :
                            selectBox(true);
                            break;
                        case R.id.unselectAll :
                            selectBox(false);
                            break;
                        case R.id.deleteSelected :
                            int temp = 0;
                            Collections.sort(mSelectedList);
                            for(int position : mSelectedList) {
                                if(temp != 0) {
                                    position -= temp;
                                }
                                temp += 1;
                                removeItem(position);
                            }

                            mSelectedList.clear();
                            isSelecting = false;
                            destroySelectNavigation();
                            break;
                    }
                    return true;
                }
            };

    private void selectBox(Boolean check) {
        for(int i = 0; i < mExampleList.size(); i++) {
            String title = mExampleList.get(i).getTitleText();
            String sub = mExampleList.get(i).getSubText();
            mExampleList.set(i, new exampleItem(title, sub, check));
        }
        mSelectedList.clear();
        if(check) {
            for(int i = 0; i < mExampleList.size(); i++) {
                mSelectedList.add(i);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NOTE_KEY, null);
        Type type = new TypeToken<ArrayList<String[]>>() {}.getType();
        ArrayList<String[]> noteBook = gson.fromJson(json, type);
        if(noteBook != null) {
            mExampleList = new ArrayList<>();
            for(String[] note : noteBook) {
                mExampleList.add(new exampleItem(note[0], note[1], false));
            }
        } else {
            mExampleList = new ArrayList<>();
        }
    }

    public void insertItem(String title, String description) {
        mExampleList.add(new exampleItem(title, description, false));
        mAdapter.notifyItemInserted(mExampleList.size());
    }

    public void removeItem(int position) {
        mExampleList.remove(position);
        mAdapter.notifyItemRemoved(position);
        // Here we remove the Item from sharedPreference notebook
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NOTE_KEY, null);
        Type type = new TypeToken<ArrayList<String[]>>() {}.getType();
        ArrayList<String[]> noteBook = gson.fromJson(json, type);
        if(noteBook != null) {
            noteBook.remove(position);
        }
        json = gson.toJson(noteBook);
        editor.putString(NOTE_KEY, json);
        editor.apply();
    }

    public void buildRecyclerView(int layout) {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList, layout);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                if (!isSelecting) {
                    openNote(position, true);
                }
            }

            @Override
            public void OnDeleteItem(int position) {
                removeItem(position);
            }

            @Override
            public void isBoxSelected(int position, Boolean checked) {
                if (checked) {
                    mSelectedList.add(position);
                } else {
                    for(int i = 0; i < mSelectedList.size(); i++) {
                        if(mSelectedList.get(i) == position) {
                            mSelectedList.remove(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    public void openNote(int position, boolean isAvailable) {
        Intent intent = new Intent(this, note.class);
        intent.putExtra(SPOT, position);
        intent.putExtra(AVAILABLE, isAvailable);
        startActivity(intent);
        CustomIntent.customType(this, "left-to-right");
        finish();
    }
}
