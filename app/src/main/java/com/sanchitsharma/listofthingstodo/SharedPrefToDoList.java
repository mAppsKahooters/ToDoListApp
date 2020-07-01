package com.sanchitsharma.listofthingstodo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedPrefToDoList implements ToDoList {

    private Context context;
    private List<String> items;

    public SharedPrefToDoList(Context context) {
        this.context = context;
        items = getList();
    }

    @Override
    public List<String> getList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todos", Context.MODE_PRIVATE);
        Set<String> toDoSet = (HashSet<String>) sharedPreferences.getStringSet("todos", null);
        items = toDoSet == null ? new ArrayList<>() : new ArrayList<>(toDoSet);
        return items;
    }

    @Override
    public void updateList(List<String> newList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("todos", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet<>(newList);
        sharedPreferences.edit().putStringSet("todos", set).apply();
        items = newList;
    }

    @Override
    public String getItemAt(int position) {
        return getList().get(position);
    }

    @Override
    public void removeItemAt(int position) {
        items.remove(position);
        updateList(items);
    }

    @Override
    public void setItemAt(int position, String newValue) {
        items.set(position, newValue);
        updateList(items);
    }

    @Override
    public void addItem(String newValue) {
        items.add(newValue);
        updateList(items);
    }

    @Override
    public int size() {
        return getList().size();
    }
}
