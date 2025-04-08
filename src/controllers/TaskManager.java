package controllers;

import model.Epic;
import model.Subtask;
import model.Task;
import util.Node;

import java.util.ArrayList;
import java.util.Map;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Subtask> getSubtasksOfEpic(int epicId);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    ArrayList<Subtask> removeAllSubtasksOfEpic(int epicId);

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    Subtask getSubtaskById(int id);

    Subtask getSubtaskInEpicById(int epicId, int subtaskId);

    int addTask(Task newTask);

    int addEpic(Epic newEpic);

    int addSubtaskToEpic(int epicId, Subtask newSubtask);

    Task updateTask(int taskId, Task updatingTask);

    Subtask updateSubtask(int epicId, int subTaskId, Subtask updatingSubtask);

    void removeTaskById(int taskId);

    void removeEpicById(int epicId);

    void removeSubtaskById(int epicId, int subtaskId);

    ArrayList<Task> getHistoryList();

    //для теста
    ArrayList<Task> getDoubleLinkedList();
}
