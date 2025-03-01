import java.util.ArrayList;

public class Epic extends Task {
    private String name;
    private String description;
    private int id;
    private TaskProgress status;
    private int newTaskIdCounter = 0;

    private ArrayList<Subtask> subtasksOfEpic;

    public Epic(String name, String description, TaskProgress status) {
        super(name, description, status);
        this.name = name;
        this.description = description;
        this.status = status;
        subtasksOfEpic = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasksOfEpic() {
        return subtasksOfEpic;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subTasks=";

        ArrayList<Task> stringTasks = new ArrayList<>(subtasksOfEpic);

        result = result + stringTasks + '}';
        return result;
    }

    @Override
    public TaskProgress getStatus() {
        return status;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getNewTaskIdCounter() {
        return newTaskIdCounter;
    }

    public void setNewTaskIdCounter(int newTaskIdCounter) {
        this.newTaskIdCounter = newTaskIdCounter;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setStatus(TaskProgress status) {
        this.status = status;
    }
}

