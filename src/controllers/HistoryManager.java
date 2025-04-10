package controllers;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    ArrayList<Task> getHistory();

    void addTaskToHistory(Task anyTask);

    void remove(int id);

    //для тестирования
    ArrayList<Task> getDoubleLinkedList();
}
