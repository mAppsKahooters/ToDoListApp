package com.sanchitsharma.listofthingstodo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.List;

public class ToDoListItemActivity extends AppCompatActivity {

    int position;
    EditText editText;
    Button completed;
    private ToDoList toDoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailedtodoitem);
        editText = (EditText) findViewById(R.id.todoitemtext);
        completed = (Button) findViewById(R.id.completedButton);

        toDoList = new SharedPrefToDoList(getApplicationContext());

        Intent intent = getIntent();
        position = intent.getIntExtra("ToDoItemPosition", -1);  // if we arrive into this activity by clicking on an existing todo item from the listvoew - then the position will be passed along with the intent - otherwise it default to -1
        if (newItemMode()) {
            editText.setText(""); //new toDo - so no existing todo text..
            completed.setVisibility(View.INVISIBLE); //hide Complete button in case of new task creation - since it's irrelevant during creation.
        } else {
            editText.setText(toDoList.getItemAt(position));
            completed.setVisibility(View.VISIBLE); // this is needed since we hide Completed option for new tasks not created yet - both NEW and EDIT tasks use this activity and share the layout as of now.
        }
    }

    private boolean newItemMode() {
        return position == -1;
    }

    public void saveToDo(View view) {  //Save button's onClick Listener
        if (editText.length() < 1) { //check if thte text field is left blank
            Toast.makeText(ToDoListItemActivity.this, "Text left blank. Nothing saved", Toast.LENGTH_SHORT).show(); //Alert user that text was blank so nothing was saved
        } else if (newItemMode()) {
            toDoList.addItem(editText.getText().toString());
        } else {
            toDoList.setItemAt(position, editText.getText().toString());
        }
        finish();
    }

    public void deleteToDoItem(View view) { //onClickListener for Delete button
        deleteToDo(position);
    }

    public void deleteToDo(int position) { //Delete a todo item from itt's position in the arraylist
        toDoList.removeItemAt(position);
        finish();
    }

    public void completeDelete(View view) { //'Compeleted' - is set to deleting the task for now - same as the usual delete function - will improve on this in v2
        Toast.makeText(ToDoListItemActivity.this, "Well Done! Keep it up! :)", Toast.LENGTH_LONG).show(); //Reward for completing the todo
        deleteToDo(position);
    }
}
