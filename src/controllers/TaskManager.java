package controllers;

import model.Epic;
import model.Subtask;
import model.Task;
import util.TaskProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private static int taskIdCounter = 0;
    private static int epicIdCounter = 0;
    private static int subTaskIdCounter = 0;

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

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

    public void removeAllTasks() { //посмотреть на id при удалении всех задач
        if (!tasks.isEmpty()) {
            taskIdCounter = 0;
            tasks.clear();
        }
    }

    public void removeAllEpics() {
        if (!epics.isEmpty()) {
            epicIdCounter = 0;
            epics.clear();
        }
    }

    public void removeAllSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
            if (!epics.isEmpty()) {
                for (Epic epic : epics.values()) {
                    epic.removeAllSubtasksOfEpic();
                }
            }
        }
    }

    public ArrayList<Subtask> removeAllSubtasksOfEpic(int epicId) {
        if (!epics.isEmpty()) {
            Epic neededEpic = getEpicById(epicId);

            for (Subtask subtaskInEpic : neededEpic.getSubtasksOfEpic().values()) {
                subtasks.values().removeIf(subtask -> subtask.equals(subtaskInEpic));
            }

            neededEpic.removeAllSubtasksOfEpic();
            neededEpic.setNewTaskIdCounter(0);
        }
        return new ArrayList<>();
    }

    //--------------------------------------------------------

    public Task getTaskById(int taskId) {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                if (task.getId() == taskId) {
                    return task;
                }
            }
        }
        return null;
    }

    public Epic getEpicById(int epicId) {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == epicId) {
                    System.out.println(epic);
                    return epic;
                }
            }
        }
        return null;
    }

    public Subtask getSubtaskById(int id) {
        if (!subtasks.isEmpty()) {
            return subtasks.get(id);
        }
        return null;
    }

    public Subtask getSubtaskInEpicById(int epicId, int subtaskId) {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == epicId) {
                    if (!epic.getSubtasksOfEpic().isEmpty()) {
                        for (Subtask subtask : epic.getSubtasksOfEpic().values()) {
                            if (subtask.getId() == subtaskId) {
                                return subtask;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    //--------------------------------------------------

    public int addTask(Task newTask) {
        taskIdCounter++;
        newTask.setId(taskIdCounter);
        tasks.put(taskIdCounter, newTask);
        return newTask.getId();
    }

    public int addEpic(Epic newEpic) {
        epicIdCounter++;
        newEpic.setId(epicIdCounter);
        epics.put(epicIdCounter, newEpic);
        return newEpic.getId();
    }

    public int addSubtaskToEpic(int epicId, Subtask newSubtask) {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == epicId) {
                    int newId = epic.getNewTaskIdCounter();
                    newId = newId + 1;
                    epic.setNewTaskIdCounter(newId);
                    newSubtask.setId(epic.getNewTaskIdCounter());

                    epic.addSubtask(newSubtask);

                    subTaskIdCounter++;
                    subtasks.put(subTaskIdCounter, newSubtask);

                    return newSubtask.getId();
                }
            }
        }
        return 0;
    }

    //--------------------------------------------------------

    public Task updateTask(int taskId, Task updatingTask) {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                if (task.getId() == taskId) {
                    task.setName(updatingTask.getName());
                    if (updatingTask.getDescription() != null) {
                        task.setDescription(updatingTask.getDescription());
                    }
                    task.setStatus(updatingTask.getStatus());
                    return task;
                }
            }
        }
        return null;
    }

    public Subtask updateSubtask(int epicId, int subTaskId, Subtask updatingSubtask) {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == epicId) {
                    for (Subtask subtask : epic.getSubtasksOfEpic().values()) {
                        if (subtask.getId() == subTaskId) {
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

                            if (epic.getSubtasksOfEpic().values().stream().
                                    allMatch(task -> task.getStatus().equals(TaskProgress.NEW))) {
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
                }
            }
        }
        return null;
    }

//--------------------------------------------------

    public void removeTaskById(int taskId) {
        if (!tasks.isEmpty()) {
            tasks.remove(taskId);
        }
    }

    public void removeEpicById(int epicId) {
        if (!epics.isEmpty()) {
            epics.remove(epicId);
        }
    }

    public void removeSubtaskById(int epicId, int subtaskId) {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == epicId) {
                    if (!epic.getSubtasksOfEpic().isEmpty()) {

                        Subtask subtaskInEpic = getSubtaskInEpicById(epicId, subtaskId);

                        subtasks.values().removeIf(subtask -> subtask.equals(subtaskInEpic));

                        subTaskIdCounter = 0;

                        for (Integer key : subtasks.keySet()) {
                            subTaskIdCounter++;
                            subtasks.put(subTaskIdCounter, subtasks.get(key));
                        }

                        epic.removeSubtaskById(subtaskId);
                        epic.setNewTaskIdCounter(0);
                        int counter = epic.getNewTaskIdCounter();

                        for (Subtask subtaskInEpic2 : epic.getSubtasksOfEpic().values()) {
                            counter++;
                            epic.setNewTaskIdCounter(counter);
                            subtaskInEpic2.setId(epic.getNewTaskIdCounter());
                        }
                    }
                }
            }
        }
    }
}