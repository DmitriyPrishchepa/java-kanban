package controllers;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    ArrayList<Task> getHistory();

    void addTaskToHistory(Task anyTask);

    void removeFromHistory(int id);
}
