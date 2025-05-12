package model;

import util.TaskProgress;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId = 0;

    public Subtask(String name, String description, TaskProgress status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(dateTimeFormatter()) +
                '}';
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
