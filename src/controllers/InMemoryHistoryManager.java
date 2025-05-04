package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void addTaskToHistory(Task anyTask) {
        if (anyTask == null) {
            return;
        }

        history.add(anyTask);
        if (history.size() > 10) {
            history.removeFirst();
        }
    }
}