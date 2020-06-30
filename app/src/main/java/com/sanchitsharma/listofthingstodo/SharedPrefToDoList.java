package com.sanchitsharma.listofthingstodo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SharedPrefToDoList implements ToDoList {

    private Context context;

    public SharedPrefToDoList(Context context) {
        this.context = context;
    }

    @Override
    public List<String> getList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todos", Context.MODE_PRIVATE);
        HashSet<String> toDoSet = (HashSet<String>) sharedPreferences.getStringSet("todos", null);
        return toDoSet == null ? new ArrayList<>() : new ArrayList<>(toDoSet);
    }

    @Override
    public void updateList(List<String> newList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todos", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet<>(newList);
        sharedPreferences.edit().putStringSet("todos", set).apply();
    }
}
