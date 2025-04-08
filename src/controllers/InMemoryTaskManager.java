package controllers;

import model.Epic;
import model.Subtask;
import model.Task;
import util.Managers;
import util.Node;
import util.TaskProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager, HistoryManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private static int taskIdCounter = 0;
    private static int epicIdCounter = 0;
    private static int subTaskIdCounter = 0;

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == epicId) {
                    return new ArrayList<>(epic.getSubtasksOfEpic().values());
                }
            }
        }
        return new ArrayList<>();
    }

    //--------------------------------------------------------------

    @Override
    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                remove(task.getId());
            }
            taskIdCounter = 0;
            tasks.clear();
        }
    }

    @Override
    public void removeAllEpics() {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                remove(epic.getId());
            }
            epicIdCounter = 0;
            epics.clear();
            subtasks.clear();
        }
    }

    @Override
    public void removeAllSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
            if (!epics.isEmpty()) {
                for (Epic epic : epics.values()) {
                    epic.removeAllSubtasksOfEpic();
                    epic.setStatus(TaskProgress.NEW);
                }
            }
        }
    }

    @Override
    public ArrayList<Subtask> removeAllSubtasksOfEpic(int epicId) {
        Epic neededEpic = getEpicById(epicId);

        for (Subtask subtaskInEpic : neededEpic.getSubtasksOfEpic().values()) {
            remove(subtaskInEpic.getId());
            subtasks.values().removeIf(subtask -> subtask.equals(subtaskInEpic));
        }

        neededEpic.removeAllSubtasksOfEpic();
        neededEpic.setNewTaskIdCounter(0);

        return new ArrayList<>();
    }

    //--------------------------------------------------------

    @Override
    public Task getTaskById(int taskId) {
        final Task task = tasks.get(taskId);
        if (task != null) {
            addTaskToHistory(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpicById(int epicId) {
        final Epic epic = epics.get(epicId);
        if (epic != null) {
            addTaskToHistory(epic);
            return epic;
        }
        return null;
    }

    @Override
    public ArrayList<Task> getHistoryList() {
        return getHistory();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            addTaskToHistory(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public Subtask getSubtaskInEpicById(int epicId, int subtaskId) {
        final Epic epic = epics.get(epicId);
        if (epic != null) {
            Subtask subtask = epic.getSubtasksOfEpic().get(subtaskId);
            addTaskToHistory(subtask);
            return subtask;
        }

        return null;
    }

    //--------------------------------------------------

    @Override
    public int addTask(Task newTask) {
        taskIdCounter++;
        newTask.setId(taskIdCounter);
        tasks.put(taskIdCounter, newTask);
        return newTask.getId();
    }

    @Override
    public int addEpic(Epic newEpic) {
        epicIdCounter++;
        newEpic.setId(epicIdCounter);
        epics.put(epicIdCounter, newEpic);
        return newEpic.getId();
    }

    @Override
    public int addSubtaskToEpic(int epicId, Subtask newSubtask) {
        final Epic epic = epics.get(epicId);
        if (epic != null) {
            int newId = epic.getNewTaskIdCounter();
            newId = newId + 1;
            epic.setNewTaskIdCounter(newId);
            newSubtask.setId(epic.getNewTaskIdCounter());

            epic.addSubtask(newSubtask);

            subTaskIdCounter++;
            subtasks.put(subTaskIdCounter, newSubtask);

            return newSubtask.getId();
        }

        return 0;
    }

    //--------------------------------------------------------

    @Override
    public Task updateTask(int taskId, Task updatingTask) {
        final Task task = tasks.get(taskId);
        if (task != null) {
            task.setName(updatingTask.getName());
            if (updatingTask.getDescription() != null) {
                task.setDescription(updatingTask.getDescription());
            }
            task.setStatus(updatingTask.getStatus());
            return task;
        }
        return null;
    }

    @Override
    public Subtask updateSubtask(int epicId, int subTaskId, Subtask updatingSubtask) {
        final Epic epic = epics.get(epicId);
        if (epic != null) {
            final Subtask subtask = epic.getSubtasksOfEpic().get(subTaskId);
            if (subtask != null) {
                subtask.setName(updatingSubtask.getName());
                if (updatingSubtask.getDescription() != null) {
                    subtask.setDescription(updatingSubtask.getDescription());
                }
                subtask.setStatus(updatingSubtask.getStatus());

                for (Subtask subtask1 : subtasks.values()) {
                    if (subtask1.equals(subtask)) {
                        subtask1.setName(updatingSubtask.getName());
                        if (subtask1.getDescription() != null) {
                            subtask1.setDescription(updatingSubtask.getDescription());
                        }
                        subtask1.setStatus(updatingSubtask.getStatus());
                    }
                }

                if (epic.getSubtasksOfEpic().values().stream()
                        .allMatch(task -> task.getStatus().equals(TaskProgress.NEW))) {
                    epic.setStatus(TaskProgress.NEW);
                } else if (epic.getSubtasksOfEpic().values().stream()
                        .allMatch(task -> task.getStatus().equals(TaskProgress.DONE))) {
                    epic.setStatus(TaskProgress.DONE);
                } else {
                    epic.setStatus(TaskProgress.IN_PROGRESS);
                }

                return subtask;
            }
        }
        return null;
    }

//--------------------------------------------------

    @Override
    public void removeTaskById(int taskId) {
        if (!tasks.isEmpty()) {
            tasks.remove(taskId);
            remove(taskId);
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        if (!epics.isEmpty()) {
            epics.remove(epicId);
            remove(epicId);
        }
    }

    @Override
    public void removeSubtaskById(int epicId, int subtaskId) {
        final Epic epic = epics.get(epicId);
        if (epic != null) {
            if (!epic.getSubtasksOfEpic().isEmpty()) {

                Subtask subtaskInEpic = getSubtaskInEpicById(epicId, subtaskId);

                subtasks.values().removeIf(subtask -> subtask.equals(subtaskInEpic));
                remove(subtaskId);

                epic.removeSubtaskById(subtaskId);
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void addTaskToHistory(Task anyTask) {
        historyManager.addTaskToHistory(anyTask);
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Task> getDoubleLinkedList() {
        return historyManager.getDoubleLinkedList();
    }
}