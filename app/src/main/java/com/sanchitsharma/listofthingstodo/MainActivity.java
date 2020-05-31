package com.sanchitsharma.listofthingstodo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> toDoList = new ArrayList<>();
    static ArrayAdapter adapter;
    public ListView listView;

    HashSet<String> set;
    SharedPreferences sharedPreferences;
    private int tasksCompleted = 0;
    private int tasksRemaining = 0;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date()); //get current date & time
        TextView datetime = (TextView) findViewById(R.id.dateTimeTextView);
        datetime.setText(currentDateTimeString);

        //AdMbob sdk init
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView); //find the banner adview in activity_main.xml
        AdRequest adRequest = new AdRequest.Builder().build(); // compose simple Ad Request
        mAdView.loadAd(adRequest); // load the banner. No pre_checks/null_checks needed for banners.

        TextView remainingCounter = (TextView) findViewById(R.id.taskRemainingCountTextView); //track remaiining tasks & display them on hone screen
        remainingCounter.setText(String.valueOf(tasksRemaining) + " tasks remaining.");

        listView = (ListView) findViewById(R.id.ListView); //main holder view for todo items
        sharedPreferences = getApplicationContext() //since the data will be a small arraylist - sharedPreferences will be sufficient for this use-case.
                .getSharedPreferences("com.sanchitsharma.listofthingstodo", Context.MODE_PRIVATE);
        set = (HashSet<String>) sharedPreferences.getStringSet("todos", null);

        if (set == null || set.size() < 1) { //checking if there's noo todo items - and if so then hiding the listview
            listView.setVisibility(View.GONE);

        } else { // if todo items exist - then read them from sharedpreference into an arraylist & show them in a listview
            listView.setVisibility(View.VISIBLE);
            toDoList = new ArrayList(set);
        }

        adapter = new ArrayAdapter(this, R.layout.todolistitemlayout, toDoList);  // using a textview as template for the adapter to poopulate each todo list item into a textview within the listview
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {   // handle onClick - regular click event - leads to detailed toDo page - with edit and delete options.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ToDoListItemActivity.class);
                intent.putExtra("ToDoItemPosition", position); // needed for tracking position in case of updates to existing todo items. for new items created via "Add New Task" button, this won't be needed as we'll set the position manually
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //show a dialog box for long press on a list item - currently only shows 2 options: Edit and Delete
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final int itemPosition = position;
                final TextView tv = (TextView) view;

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(toDoList.get(position))
                        .setMessage("PLease select one of the options below...")
                        .setPositiveButton("Delete this ToDo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                toDoList.remove(itemPosition);
                                adapter.notifyDataSetChanged();
                                SharedPreferences sharedPreferences = getApplicationContext()
                                        .getSharedPreferences("com.sanchitsharma.listofthingstodo", Context.MODE_PRIVATE);
                                HashSet<String> set = new HashSet<String>(MainActivity.toDoList);
                                sharedPreferences.edit().putStringSet("todos", set).apply();
                                if (toDoList.size() < 1) {
                                    listView.setVisibility(View.GONE);
                                }
                                updateRemainingTasksCounter();
                            }
                        }).setNeutralButton("Edit this ToDo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, ToDoListItemActivity.class);
                        intent.putExtra("ToDoItemPosition", itemPosition);
                        startActivity(intent);
                    }
                }).show();
                return true;
            }
        });

        Button addButton;  // Add New Task button
        addButton = (Button) findViewById(R.id.addnewTaskButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Add Task Button Clicked.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, ToDoListItemActivity.class);
                startActivity(intent);
                listView.setVisibility(View.VISIBLE);
            }
        });
    }

    //Updating the 'Tasks Remaining' text field after any changes to the list of tasks
    public void updateRemainingTasksCounter() {
        TextView remainingCounter = (TextView) findViewById(R.id.taskRemainingCountTextView);
        tasksRemaining = toDoList.size();
        if (tasksRemaining > 0) {
            if (tasksRemaining == 1) {
                remainingCounter.setText("Just " + String.valueOf(tasksRemaining) + " task remaining."); //Extra motivation for the last remaining task
            } else
                remainingCounter.setText(String.valueOf(tasksRemaining) + " tasks remaining.");
        }
        else {
            remainingCounter.setText(String.valueOf("Everything's done. Nothing's pending!")); //Message when there's no task/todo added by user.
        }
    }

    @Override
    protected void onPostResume() {     // checking for list size on resume event to hidfe the listview if needed.
        super.onPostResume();
        if (toDoList.size() < 1) {
            listView.setVisibility(View.GONE);
        }
        updateRemainingTasksCounter();
    }
}