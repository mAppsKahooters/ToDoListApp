package com.sanchitsharma.listofthingstodo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> toDoList = new ArrayList<>();
    public ListView todoItemsView;

    private AdView adView;
    private ToDoList newTodo;
    private TextView remainingCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date()); //get current date & time
        TextView datetime = (TextView) findViewById(R.id.dateTimeTextView);
        datetime.setText(currentDateTimeString);

        initAdMob();

        newTodo = new SharedPrefToDoList(getApplicationContext());
        List<String> list = newTodo.getList();

        toDoList = list;
        todoItemsView = (ListView) findViewById(R.id.ListView);
        remainingCounter = (TextView) findViewById(R.id.taskRemainingCountTextView);

        FloatingActionButton addItemButton = (FloatingActionButton) findViewById(R.id.addnewTaskButton);
        addItemButton.setOnClickListener(view -> startActivity(new Intent(this, ToDoListItemActivity.class)));
    }

    private void initAdMob() {
        MobileAds.initialize(this, initializationStatus -> {});

        adView = findViewById(R.id.adView); //find the banner adview in activity_main.xml
        AdRequest adRequest = new AdRequest.Builder().build(); // compose simple Ad Request
        adView.loadAd(adRequest); // load the banner. No pre_checks/null_checks needed for banners.
    }

    private void setupTodosView(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.todolistitemlayout, items);  // using a textview as template for the adapter to poopulate each todo list item into a textview within the listview
        todoItemsView.setAdapter(adapter);
        todoItemsView.setOnItemClickListener((parent, view, position, id) -> startEditItemActivity(position));

        //show a dialog box for long press on a list item - currently only shows 2 options: Edit and Delete
        todoItemsView.setOnItemLongClickListener((parent, view, position, id) -> {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(toDoList.get(position))
                    .setMessage("Please select one of the options below...")
                    .setPositiveButton("Delete this ToDo", (dialog, which) -> removeItemAndUpdateView(position))
                    .setNeutralButton("Edit this ToDo", (dialogInterface, i) -> startEditItemActivity(position))
                    .show();
            return true;
        });
    }

    private void removeItemAndUpdateView(int position) {
        toDoList.remove(position);
        newTodo.updateList(toDoList);
        refreshTodoItemsViews();
    }

    private void startEditItemActivity(int position) {
        Intent intent = new Intent(MainActivity.this, ToDoListItemActivity.class);
        intent.putExtra("ToDoItemPosition", position);
        startActivity(intent);
    }

    //Updating the 'Tasks Remaining' text field after any changes to the list of tasks
    public void updateRemainingTasksCounter() {
        int tasksRemaining = toDoList.size();
        String text = "";
        if (tasksRemaining == 1) {
            text = getResources().getString(R.string.one_task_remaining);
        } else if (tasksRemaining > 0) {
            text = String.format(getResources().getString(R.string.x_tasks_remaining), tasksRemaining);
        } else {
            text = getResources().getString(R.string.empty_list_text);
        }
        remainingCounter.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTodoItemsViews();
    }

    private void refreshTodoItemsViews() {
        List<String> list = newTodo.getList();
        if (list.isEmpty()) {
            todoItemsView.setVisibility(View.GONE);
        } else {
            todoItemsView.setVisibility(View.VISIBLE);
            setupTodosView(list);
        }
        updateRemainingTasksCounter();
    }
}