package com.sanchitsharma.listofthingstodo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;

public class ToDoListItemActivity extends AppCompatActivity {
    int position;
    EditText editText;
    Button save;
    Button completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailedtodoitem);
        editText = (EditText) findViewById(R.id.todoitemtext);
        completed = (Button) findViewById(R.id.completedButton);

        Intent intent = getIntent();
        position = intent.getIntExtra("ToDoItemPosition", -1);  // if we arrive into this activity by clicking on an existing todo item from the listvoew - then the position will be passed along with the intent - otherwise it default to -1
        if (position != -1) {
            editText.setText(MainActivity.toDoList.get(position)); // Populating the textview with the existing text from the ToDo item
            completed.setVisibility(View.VISIBLE); // this is needed since we hide Completed option for new tasks not created yet - both NEW and EDIT tasks use this activity and share the layout as of now.
        } else {
            editText.setText(""); //new toDo - so no existing todo text..
            completed.setVisibility(View.INVISIBLE); //hide Complete button in case of new task creation - since it's irrelevant during creation.
        }
    }

    public void saveToDo(View view) {  //Save button's onClick Listener
        if (editText.length() < 1) { //check if thte text field is left blank
            Toast.makeText(ToDoListItemActivity.this, "Text left blank. Nothing saved", Toast.LENGTH_SHORT).show(); //Alert user that text was blank so nothing was saved

        } else if (position == -1) {
            MainActivity.toDoList.add(editText.getText().toString()); //ADD a NEW todo to the ArrayList
            MainActivity.adapter.notifyDataSetChanged();
        } else {
            MainActivity.toDoList.set(position, editText.getText().toString()); //UPDATET an existing todo
            MainActivity.adapter.notifyDataSetChanged();
        }

        SharedPreferences sharedPreferences = getApplicationContext()  //Write any changes to the device memory
                .getSharedPreferences("com.sanchitsharma.listofthingstodo", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet<String>(MainActivity.toDoList);
        sharedPreferences.edit().putStringSet("todos", set).apply();
        onBackPressed();
    }

    public void deleteToDoItem(View view) { //onClickListener for Delete button
        deleteToDo(position);
    }

    public void deleteToDo(int position) { //Delete a todo item from itt's position in the arraylist
        MainActivity.toDoList.remove(position);
        MainActivity.adapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences = getApplicationContext()  // update local storage of the deletion
                .getSharedPreferences("com.sanchitsharma.listofthingstodo", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet<String>(MainActivity.toDoList);
        sharedPreferences.edit().putStringSet("todos", set).apply();
        onBackPressed(); //return to Home Screen/Main Activity
    }

    public void completeDelete(View view) { //'Compeleted' - is set to deleting the task for now - same as the usual delete function - will improve on this in v2
        Toast.makeText(ToDoListItemActivity.this, "Well Done! Keep it up! :)", Toast.LENGTH_LONG).show(); //Reward for completing the todo
        deleteToDo(position);
    }
}
