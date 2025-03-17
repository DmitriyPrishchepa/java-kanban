package controllers;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    ArrayList<Task> getHistory();

    <T extends Task> void addTaskToHistory(T anyTask);
}
