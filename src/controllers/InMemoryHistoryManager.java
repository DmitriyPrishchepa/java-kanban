package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    public Map<Integer, Task> viewedTasks = new HashMap<>();
    private static int viewedTasksCounter = 0;

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(viewedTasks.values());
    }

    @Override
    public <T extends Task> void addTaskToHistory(T anyTask) {
        if (anyTask != null) {
            viewedTasksCounter++;
            viewedTasks.put(viewedTasksCounter, anyTask);
        }
    }
}
