package model;

import util.TaskProgress;

public class Subtask extends Task {
    public Subtask(String name, String description, TaskProgress status) {
        super(name, description, status);
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
