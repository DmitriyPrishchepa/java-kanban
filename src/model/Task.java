package model;

import util.TaskProgress;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskProgress status;


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public Task(String name, String description, TaskProgress status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskProgress status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public TaskProgress getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || (this.getClass() != obj.getClass())) return false;
        Task newTask = (Task) obj;
        return Objects.equals(name, newTask.name) &&
                Objects.equals(description, newTask.description) &&
                (id == newTask.id) &&
                Objects.equals(status, newTask.status);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;

        if (description != null) {
            hash = hash + description.hashCode();
        }

        hash = hash * 31;

        if (status != null) {
            hash = hash + status.hashCode();
        }

        hash = hash * 31;

        return hash;
    }
}
