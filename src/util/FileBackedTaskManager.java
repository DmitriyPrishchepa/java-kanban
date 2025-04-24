package util;

import controllers.InMemoryTaskManager;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static util.TypesOfTasks.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;

    public FileBackedTaskManager(
            Path path,
            Map<Integer, Task> tasks,
            Map<Integer, Epic> epics,
            Map<Integer, Subtask> subtasks
    ) {
        this.path = path;
        this.tasks = new HashMap<>(tasks);
        this.epics = new HashMap<>(epics);
        this.subtasks = new HashMap<>(subtasks);
    }

    @Override
    public int addTask(Task newTask) {
        int result = super.addTask(newTask);
        save();
        return result;
    }

    @Override
    public int addEpic(Epic newEpic) {
        int result = super.addEpic(newEpic);
        save();
        return result;
    }

    @Override
    public int addSubtaskToEpic(int epicId, Subtask newSubtask) {
        int result = super.addSubtaskToEpic(epicId, newSubtask);
        save();
        return result;
    }

    //-----------------------------------------------

    @Override
    public Task updateTask(int taskId, Task updatingTask) {
        Task result = super.updateTask(taskId, updatingTask);
        save();
        return result;
    }

    @Override
    public Subtask updateSubtask(int epicId, int subTaskId, Subtask updatingSubtask) {
        Subtask result = super.updateSubtask(epicId, subTaskId, updatingSubtask);
        save();
        return result;
    }

    //-----------------------------------------------

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int epicId, int subtaskId) {
        super.removeSubtaskById(epicId, subtaskId);
        save();
    }

    //------------------------------------


    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public ArrayList<Subtask> removeAllSubtasksOfEpic(int epicId) {
        ArrayList<Subtask> arrayList = super.removeAllSubtasksOfEpic(epicId);
        save();
        return arrayList;
    }

    //-------------------------------


    @Override
    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        return super.getSubtasksOfEpic(epicId);
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(path.getFileName()), StandardCharsets.UTF_8))) {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            if (path.toFile().length() == 0) {
                writer.write("id,type,name,status,description,epic\n");
            }

            ArrayList<Task> tasks = super.getTasks();
            for (Task task : tasks) {
                writer.write(taskToString(task) + "\n");
            }

            ArrayList<Epic> epics = super.getEpics();
            for (Epic epic : epics) {
                writer.write(taskToString(epic) + "\n");
            }

            ArrayList<Subtask> subtasks = super.getSubtasks();
            for (Subtask subtask : subtasks) {
                writer.write(taskToString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        final Map<Integer, Task> tasks = new HashMap<>();
        final Map<Integer, Epic> epics = new HashMap<>();
        final Map<Integer, Subtask> subtasks = new HashMap<>();

        try {
            if (path.toFile().length() != 0) {
                String line = Files.readString(path);

                int stringLength = "id,type,name,status,description,epic\n".length();

                String neededLine = line.substring(stringLength);

                if (!neededLine.isEmpty()) {
                    System.out.println(neededLine);
                    Task instanseOfTask = fromString(neededLine);

                    if (instanseOfTask instanceof Epic) {
                        epics.put(instanseOfTask.getId(), (Epic) instanseOfTask);
                    } else if (instanseOfTask instanceof Subtask) {
                        subtasks.put(instanseOfTask.getId(), (Subtask) instanseOfTask);
                    } else if (instanseOfTask != null) {
                        tasks.put(instanseOfTask.getId(), instanseOfTask);
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла", e);
        }

        return new FileBackedTaskManager(path, tasks, epics, subtasks);
    }

    public String taskToString(Task task) {
        String result = task.getId() + ",";

        if (task instanceof Epic) {
            result = result + EPIC;
        } else if (task instanceof Subtask) {
            result = result + SUBTASK;
        } else {
            result = result + TASK;
        }

        result = result + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription();

        if (task instanceof Subtask) {
            result = result + ((Subtask) task).getEpicId();
        }

        return result;
    }

    public static Task fromString(String stringValue) {
        String[] stringsArray = stringValue.split("\n");

        for (String s : stringsArray) {
            String[] wordsArray = s.split(",");

            int id = Integer.parseInt(wordsArray[0].trim());
            String name = wordsArray[2];
            String description = wordsArray[4];
            String status = wordsArray[3];

            switch (TypesOfTasks.valueOf(wordsArray[1])) {
                case TASK:
                    Task newTask = new Task(name, description, TaskProgress.valueOf(status));
                    newTask.setId(id);
                    return newTask;
                case EPIC:
                    Epic newEpic = new Epic(name, description, TaskProgress.valueOf(status));
                    newEpic.setId(id);
                    return newEpic;
                case SUBTASK:
                    Subtask newSubtask = new Subtask(name, description, TaskProgress.valueOf(status));
                    newSubtask.setId(id);
                    return newSubtask;
            }
        }

        return null;
    }
}
