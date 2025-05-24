package controllers;

import exceptions.SubtaskNotFoundException;
import exceptions.TaskNotFoundException;
import model.Epic;
import model.Subtask;
import model.Task;
import util.Managers;
import util.TaskProgress;
import util.TaskTimeComparator;

import java.util.*;

public class InMemoryTaskManager implements TaskManager, HistoryManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Set<Task> tasksSortedByTime = new TreeSet<>(TaskTimeComparator.getTimeComparator());

    protected int taskIdCounter = 0;
    protected int epicIdCounter = 0;
    protected int subTaskIdCounter = 0;

    public InMemoryTaskManager() {
    }

    public InMemoryTaskManager(
            Map<Integer, Task> newTasks,
            Map<Integer, Epic> newEpics,
            Map<Integer, Subtask> newSubtasks,
            int taskIdCounter,
            int epicIdCounter,
            int subTaskIdCounter
    ) {
        this.tasks = new HashMap<>(newTasks);
        this.epics = new HashMap<>(newEpics);
        this.subtasks = new HashMap<>(newSubtasks);
        this.taskIdCounter = taskIdCounter;
        this.epicIdCounter = epicIdCounter;
        this.subTaskIdCounter = subTaskIdCounter;
    }

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
            taskIdCounter = 0;
            for (Task task : tasks.values()) {
                removeFromHistory(task.getId());
            }
            tasks.clear();
        }
    }

    @Override
    public void removeAllEpics() {
        if (!epics.isEmpty()) {
            epicIdCounter = 0;
            for (Epic epic : epics.values()) {
                removeFromHistory(epic.getId());
            }
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
                    removeFromHistory(epic.getId());
                }
            }
        }
    }

    @Override
    public ArrayList<Subtask> removeAllSubtasksOfEpic(int epicId) {
        Epic neededEpic = getEpicById(epicId);

        for (Subtask subtaskInEpic : neededEpic.getSubtasksOfEpic().values()) {
            subtasks.values().removeIf(subtask -> subtask.equals(subtaskInEpic));
        }

        neededEpic.removeAllSubtasksOfEpic();
        neededEpic.setNewTaskIdCounter(0);

        return new ArrayList<>();
    }

    //--------------------------------------------------------

    @Override
    public Task getTaskById(int taskId) {
        Optional<Task> optionalTask = Optional.ofNullable(tasks.get(taskId));
        optionalTask.ifPresent(this::addTaskToHistory);
        return optionalTask.orElseThrow(() -> new TaskNotFoundException("Задача не найдена"));
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
    public Subtask getSubtaskById(int id) {
        Optional<Subtask> optionalSubtask = Optional.ofNullable(subtasks.get(id));
        return optionalSubtask.orElseThrow(() -> new SubtaskNotFoundException("Подзадача не найдена"));
    }

    @Override
    public Subtask getSubtaskInEpicById(int epicId, int subtaskId) {
        final Epic epic = epics.get(epicId);

        if (epic != null) {
            Optional<Subtask> optionalSubtask = Optional.ofNullable(epic.getSubtasksOfEpic().get(subtaskId));
            Subtask subtask = optionalSubtask.orElseThrow(() -> new SubtaskNotFoundException("Подзадача не найдена"));
            optionalSubtask.ifPresent(this::addTaskToHistory);
            return subtask;
        } else {
            Optional<Subtask> optionalSubtask = Optional.ofNullable(epic.getSubtasksOfEpic().get(subtaskId));
            return optionalSubtask.orElseThrow(() -> new SubtaskNotFoundException("Подзадача не найдена"));
        }
    }

//--------------------------------------------------

    @Override
    public int addTask(Task newTask) {
        if (newTask.getStartTime() != null &&
                newTask.getDuration() != null &&
                checkTasksIntersectionsByRuntime(newTask)
        ) {
            return 0;
        }

        if (newTask.getStartTime() == null &&
                newTask.getDuration() == null) {
            taskIdCounter++;
            newTask.setId(taskIdCounter);
            tasks.put(taskIdCounter, newTask);
            return newTask.getId();
        }

        if (newTask.getStartTime() != null &&
                newTask.getDuration() != null &&
                !checkTasksIntersectionsByRuntime(newTask)) {
            taskIdCounter++;
            newTask.setId(taskIdCounter);
            tasks.put(taskIdCounter, newTask);
            tasksSortedByTime.add(newTask);
            return newTask.getId();
        }

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
            newSubtask.setEpicId(epic.getId());

            epic.addSubtask(newSubtask);

            if (newSubtask.getStartTime() != null &&
                    newSubtask.getDuration() != null &&
                    checkTasksIntersectionsByRuntime(newSubtask)
            ) {
                return 0;
            }

            if (newSubtask.getStartTime() == null &&
                    newSubtask.getDuration() == null) {
                subTaskIdCounter++;
                newSubtask.setId(subTaskIdCounter);
                subtasks.put(subTaskIdCounter, newSubtask);
                return newSubtask.getId();
            }

            if (newSubtask.getStartTime() != null &&
                    newSubtask.getDuration() != null &&
                    !checkTasksIntersectionsByRuntime(newSubtask)) {
                subTaskIdCounter++;
                newSubtask.setId(subTaskIdCounter);
                subtasks.put(subTaskIdCounter, newSubtask);
                tasksSortedByTime.add(newSubtask);
                return newSubtask.getId();
            }

            return newSubtask.getId();
        }

        return 0;
    }

//--------------------------------------------------------

    @Override
    public Task updateTask(int taskId, Task updatingTask) {

        if (checkTasksIntersectionsByRuntime(updatingTask)) {
            return null;
        }

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

        if (checkTasksIntersectionsByRuntime(updatingSubtask)) {
            return null;
        }

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
            Task removedTask = tasks.get(taskId);
            tasksSortedByTime.remove(removedTask);
            tasks.remove(taskId);
            removeFromHistory(taskId);
            getPrioritizedTasks().remove(tasks.get(taskId));
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        if (!epics.isEmpty()) {
            epics.remove(epicId);
            removeFromHistory(epicId);
        }
    }

    @Override
    public void removeSubtaskById(int epicId, int subtaskId) {

        tasksSortedByTime.remove(subtasks.get(subtaskId));

        final Epic epic = epics.get(epicId);
        if (epic != null) {
            if (!epic.getSubtasksOfEpic().isEmpty()) {

                Subtask subtaskInEpic = getSubtaskInEpicById(epicId, subtaskId);

                subtasks.values().removeIf(subtask -> subtask.equals(subtaskInEpic));

                subTaskIdCounter = 0;

                for (Integer key : subtasks.keySet()) {
                    subTaskIdCounter++;
                    subtasks.put(subTaskIdCounter, subtasks.get(key));
                }

                epic.removeSubtaskById(subtaskId);
                removeFromHistory(subtaskId);
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

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void addTaskToHistory(Task anyTask) {
        historyManager.addTaskToHistory(anyTask);
    }


    @Override
    public List<Task> getPrioritizedTasks() {

        for (Task task : getTasks()) {
            if (task.getStartTime() != null) {
                tasksSortedByTime.add(task);
            }
        }

        for (Subtask subtask : getSubtasks()) {
            if (subtask.getStartTime() != null) {
                tasksSortedByTime.add(subtask);
            }
        }

        return new ArrayList<>(tasksSortedByTime);
    }

    @Override
    public boolean checkTasksIntersectionsByRuntime(Task task) {
        List<Task> prioritizedTasks = getPrioritizedTasks();

        for (Task prioritizedTask : prioritizedTasks) {
            if (prioritizedTask.getStartTime().isBefore(task.getEndTime()) &&
                    prioritizedTask.getEndTime().isAfter(task.getStartTime())
            ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void removeFromHistory(int id) {
        historyManager.removeFromHistory(id);
    }
}