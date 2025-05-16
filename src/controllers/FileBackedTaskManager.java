package controllers;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import util.TaskProgress;
import util.TypesOfTasks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static util.TypesOfTasks.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    public FileBackedTaskManager(
            Path path,
            HashMap<Integer, Task> newTasks,
            HashMap<Integer, Epic> newEpics,
            HashMap<Integer, Subtask> newSubtasks,
            int taskIdCounter,
            int epicIdCounter,
            int subtaskIdCounter
    ) {
        super(newTasks, newEpics, newSubtasks, taskIdCounter, epicIdCounter, subtaskIdCounter);
        this.path = path;
    }

    @Override
    public int addTask(Task newTask) {
        super.addTask(newTask);
        save();
        return 0;
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
                writer.write("id,type,name,status,description,duration,startTime,epic\n");
            }

            ArrayList<Task> tasks = getTasks();

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
        final HashMap<Integer, Task> newTasks = new HashMap<>();
        final HashMap<Integer, Epic> newEpics = new HashMap<>();
        final HashMap<Integer, Subtask> newSubtasks = new HashMap<>();

        int taskIdCounter = 0;
        int epicIdCounter = 0;
        int subTaskIdCounter = 0;

        try {
            if (path.toFile().length() != 0) {
                String line = Files.readString(path);

                int stringLength = "id,type,name,status,description,duration,startTime,epic\n".length();

                String neededLine = line.substring(stringLength);

                if (!neededLine.isEmpty()) {
                    System.out.println(neededLine);

                    String[] stringsArray = neededLine.split("\n");

                    for (String s : stringsArray) {

                        Task instanseOfTask = fromString(s);

                        if (instanseOfTask != null) {
                            if (instanseOfTask.getClass().equals(Epic.class)) {
                                newEpics.put(instanseOfTask.getId(), (Epic) instanseOfTask);
                                epicIdCounter++;
                            } else if (instanseOfTask.getClass().equals(Subtask.class)) {
                                newSubtasks.put(instanseOfTask.getId(), (Subtask) instanseOfTask);
                                subTaskIdCounter++;
                            } else {
                                newTasks.put(instanseOfTask.getId(), instanseOfTask);
                                taskIdCounter++;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла", e);
        }

        return new FileBackedTaskManager(
                path,
                newTasks,
                newEpics,
                newSubtasks,
                taskIdCounter,
                epicIdCounter,
                subTaskIdCounter++
        );
    }

    public String taskToString(Task task) {
        String result = task.getId() + ",";

        if (task.getClass().equals(Epic.class)) {
            result = result + EPIC;
        } else if (task.getClass().equals(Subtask.class)) {
            result = result + SUBTASK;
        } else {
            result = result + TASK;
        }

        result = result + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getDuration().toMinutes() + "," +
                task.getStartTime();

        if (task.getClass().equals(Subtask.class)) {
            result = result + ((Subtask) task).getEpicId();
        }

        return result;
    }

    public static Task fromString(String stringValue) {

        String[] wordsArray = stringValue.split(",");

        int id = Integer.parseInt(wordsArray[0].trim());
        String name = wordsArray[2];
        String description = wordsArray[4];
        String status = wordsArray[3];
        int duration = Integer.parseInt(wordsArray[5]);
        String startTime = wordsArray[6];

        switch (TypesOfTasks.valueOf(wordsArray[1])) {
            case TASK:
                Task newTask = new Task(name, description, TaskProgress.valueOf(status), Duration.ofMinutes(duration), LocalDateTime.parse(startTime));
                newTask.setId(id);
                return newTask;
            case EPIC:
                Epic newEpic = new Epic(name, description, TaskProgress.valueOf(status));
                newEpic.setId(id);
                return newEpic;
            case SUBTASK:
                Subtask newSubtask = new Subtask(name, description, TaskProgress.valueOf(status), Duration.ofMinutes(duration), LocalDateTime.parse(startTime));
                newSubtask.setId(id);
                return newSubtask;
        }

        return null;
    }
}