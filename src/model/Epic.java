package model;

import util.TaskProgress;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private int newTaskIdCounter = 0;
    private final LocalDateTime endTime;

    private final Map<Integer, Subtask> subtasksOfEpic;

    public Epic(String name, String description, TaskProgress status) {
        super(name, description, status);
        this.name = name;
        this.description = description;
        this.status = status;

        subtasksOfEpic = new HashMap<>();

        if (getSubtasksOfEpic().isEmpty()) {
            this.startTime = LocalDateTime.now();
            this.endTime = LocalDateTime.now();
        } else {
            this.startTime = getSubtasksOfEpic().get(1).startTime;
            this.endTime = getSubtasksOfEpic().get(getSubtasksOfEpic().size()).getEndTime();
        }

        Duration subDuration = Duration.ofMinutes(0);

        for (Subtask subtask : getSubtasksOfEpic().values()) {
            subDuration = subDuration.plus(subtask.getDuration());
        }

        this.duration = subDuration;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(dateTimeFormatter()) +
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

    @Override
    public Duration getDuration() {
        return super.getDuration();
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}

