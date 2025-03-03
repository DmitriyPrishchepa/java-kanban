package model;

import util.TaskProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private int newTaskIdCounter = 0;

    private final Map<Integer, Subtask> subtasksOfEpic;

    public Epic(String name, String description, TaskProgress status) {
        super(name, description, status);
        this.name = name;
        this.description = description;
        this.status = status;
        subtasksOfEpic = new HashMap<>();
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subTasks=";

        ArrayList<Task> stringTasks = new ArrayList<>(subtasksOfEpic.values());

        result = result + stringTasks + '}';
        return result;
    }

    public int getNewTaskIdCounter() {
        return newTaskIdCounter;
    }

    public void setNewTaskIdCounter(int newTaskIdCounter) {
        this.newTaskIdCounter = newTaskIdCounter;
    }

    public void addSubtask(Subtask newSubtask) {
        subtasksOfEpic.put(newSubtask.getId(), newSubtask);
    }

    public Map<Integer, Subtask> getSubtasksOfEpic() {
        return subtasksOfEpic;
    }

    public void removeSubtaskById(int subtaskId) {
        if (!subtasksOfEpic.isEmpty()) {
            subtasksOfEpic.values().removeIf(subtask -> subtask.getId() == subtaskId);
        }
    }

    public void removeAllSubtasksOfEpic() {
        if (!subtasksOfEpic.isEmpty()) {
            subtasksOfEpic.clear();
        }
    }
}

