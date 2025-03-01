import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();

    private static int taskIdCounter = 0;
    private static int epicIdCounter = 0;

    public HashMap<Integer, Task> getTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
        }
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список эпиков пуст");
        }
        return epics;
    }

    public ArrayList<Subtask> getTasksOfEpic(int epicId) {
        if (!epics.isEmpty()) {
            if (!epics.get(epicId).getSubtasksOfEpic().isEmpty()) {
                return epics.get(epicId).getSubtasksOfEpic();
            }
        }
        System.out.println("Список подзадач пуст");
        return new ArrayList<>();
    }

    //--------------------------------------------------------------

    public HashMap<Integer, Task> removeAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
        System.out.println("Все задачи удалены. Список пуст");
        System.out.println(tasks);
        return tasks;
    }

    public HashMap<Integer, Epic> removeAllEpics() {
        if (!epics.isEmpty()) {
            epics.clear();
            epicIdCounter = 0;
            System.out.println("Все эпики удалены. Список пуст");
            System.out.println(epics);
        }
        return epics;
    }

    public ArrayList<Subtask> removeAllSubtasksOfEpic(int epicId) {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == epicId) {
                    epic.getSubtasksOfEpic().removeAll(epic.getSubtasksOfEpic());
                    System.out.println("Все подзадачи эпика удалены. Список пуст");
                    System.out.println(epics);
                    return epic.getSubtasksOfEpic();
                }
            }
        }
        return new ArrayList<>();
    }

    //--------------------------------------------------------

    public Task getTaskById(int taskId) {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                if (task.getId() == taskId) {
                    System.out.println(task);
                    return task;
                }
            }
        }
        System.out.println("Задачи с таким id нет");
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
        System.out.println("Эпика с таким id нет");
        return null;
    }

    public Task getSubtaskOfEpicById(int epicId, int subtaskId) {
        for (Epic epic : epics.values()) {
            if (epic.getId() == epicId) {
                for (Subtask subtask : epic.getSubtasksOfEpic()) {
                    if (subtask.getId() == subtaskId) {
                        return subtask;
                    }
                }
            }
        }
        System.out.println("Подзадачи с таким id нет");
        return null;
    }

    //--------------------------------------------------

    public HashMap<Integer, Task> addTask(Task newTask) {
        taskIdCounter++;
        newTask.setId(taskIdCounter);
        tasks.put(taskIdCounter, newTask);
        System.out.println("Задача успешно добавлена");
        return tasks;
    }

    public HashMap<Integer, Epic> addEpic(Epic newEpic) {
        epicIdCounter++;
        newEpic.setId(epicIdCounter);
        epics.put(epicIdCounter, newEpic);
        System.out.println("Эпик успешно добавлен");
        return epics;
    }

    public ArrayList<Subtask> addSubtaskToEpic(int epicId, Subtask newSubtask) {
        for (Epic epic : epics.values()) {
            if (epic.getId() == epicId) {
                int newId = epic.getNewTaskIdCounter();
                newId = newId + 1;
                epic.setNewTaskIdCounter(newId);
                newSubtask.setId(epic.getNewTaskIdCounter());
                epics.get(epicId).getSubtasksOfEpic().add(newSubtask);
                return epic.getSubtasksOfEpic();
            }
        }
        return new ArrayList<>();
    }

    //--------------------------------------------------------

    public HashMap<Integer, Task> updateTask(int taskId, Task updatingTask) {
        for (Task task : tasks.values()) {
            if (task.getId() == taskId) {
                task.setName(updatingTask.getName());
                if (updatingTask.getDescription() != null) {
                    task.setDescription(updatingTask.getDescription());
                }
                task.setStatus(updatingTask.getStatus());
                System.out.println("Задача успешно изменена");
            }
        }
        return tasks;
    }

    public ArrayList<Subtask> updateSubtask(int epicId, int subTaskId, Subtask updatingSubtask) {
        for (Epic epic : epics.values()) {
            if (epic.getId() == epicId) {
                for (Subtask subtask : epic.getSubtasksOfEpic()) {
                    if (subtask.getId() == subTaskId) {
                        subtask.setName(updatingSubtask.getName());
                        if (updatingSubtask.getDescription() != null) {
                            subtask.setDescription(updatingSubtask.getDescription());
                        }
                        subtask.setStatus(updatingSubtask.getStatus());
                    }
                }

                if (epic.getSubtasksOfEpic().stream()
                        .allMatch(task -> task.getStatus().equals(TaskProgress.NEW))) {
                    epic.setStatus(TaskProgress.NEW);
                } else if (epic.getSubtasksOfEpic().stream()
                        .allMatch(task -> task.getStatus().equals(TaskProgress.DONE))) {
                    epic.setStatus(TaskProgress.DONE);
                } else {
                    epic.setStatus(TaskProgress.IN_PROGRESS);
                }

                System.out.println(epic.getSubtasksOfEpic());
                return epic.getSubtasksOfEpic();
            }
        }
        return new ArrayList<>();
    }

    //--------------------------------------------------

    public HashMap<Integer, Task> removeTaskById(int taskId) {
        if (!tasks.isEmpty()) {
            if (tasks.containsKey(taskId)) {
                tasks.remove(taskId);
                taskIdCounter = 0;
                for (Task task : tasks.values()) {
                    taskIdCounter++;
                    task.setId(taskIdCounter);
                }
                return tasks;
            } else {
                System.out.println("Задача с таким id не найдена");
                return tasks;
            }
        }
        return tasks;
    }

    public HashMap<Integer, Epic> removeEpicById(int epicId) {
        epics.remove(epicId);
        epicIdCounter = 0;
        for (Epic epic : epics.values()) {
            epicIdCounter++;
            epic.setId(epicIdCounter);
        }
        return epics;
    }

    public ArrayList<Subtask> removeSubtaskById(int epicId, int subtaskId) {
        for (Epic epic : epics.values()) {
            if (epic.getId() == epicId) {
                epic.getSubtasksOfEpic().removeIf(subtask -> subtask.getId() == subtaskId);
                epic.setNewTaskIdCounter(0);
                for (Subtask subtask : epic.getSubtasksOfEpic()) {
                    epic.setNewTaskIdCounter(epic.getNewTaskIdCounter() + 1);
                    subtask.setId(epic.getNewTaskIdCounter());
                }
                return epic.getSubtasksOfEpic();
            }
        }
        return new ArrayList<>();
    }
}
