package com.awesome.todo;

import java.util.List;

interface ToDoList {

    List<String> getList();

    void updateList(List<String> newList);

    String getItemAt(int position);

    void removeItemAt(int position);

    void setItemAt(int position, String newValue);

    void addItem(String newValue);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }
}
