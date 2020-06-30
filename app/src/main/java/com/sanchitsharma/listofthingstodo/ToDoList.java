package com.sanchitsharma.listofthingstodo;

import java.util.List;

interface ToDoList {

    List<String> getList();

    void updateList(List<String> newList);
}
