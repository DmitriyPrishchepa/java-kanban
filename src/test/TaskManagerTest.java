package test;

import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;
import util.Node;
import util.TaskProgress;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    InMemoryTaskManager inMemoryTaskManager;
    InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    void create() {
        inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    Task task1 = new Task(
            "Приготовить обед",
            "Сегодня мы будем готовить мясо по французски",
            TaskProgress.NEW
    );

    Task task2 = new Task(
            "Приготовить обед",
            "Сегодня мы будем готовить мясо по французски",
            TaskProgress.NEW
    );

    Epic epic1 = new Epic(
            "Отпуск",
            "Планы на отпуск",
            TaskProgress.NEW
    );

    Epic epic2 = new Epic(
            "Отпуск",
            "Планы на отпуск",
            TaskProgress.NEW
    );


    Subtask subtask1 = new Subtask(
            "Отпуск",
            "Планы на отпуск",
            TaskProgress.NEW
    );

    Subtask subtask2 = new Subtask(
            "Отпуск",
            "Планы на отпуск",
            TaskProgress.NEW
    );

    Task epic4 = new Epic(
            "Работа",
            "Задачи для работы",
            TaskProgress.NEW
    );

    Task subtask4 = new Subtask(
            "Собеседование",
            "Назначить собеседование",
            TaskProgress.NEW
    );

    Subtask subtask5 = new Subtask(
            "Погулять с ребенком",
            "Съездить в центр города, сходить в оканариум, поесть в макдональдс",
            TaskProgress.NEW
    );

    @Test
    public void shouldReturnPositiveIfInstancesOfTaskAreEqualsIfIdsAreEquals() {
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    public void shouldReturnPositiveIfInstancesOfEpicAreEqualsIfIdsAreEquals() {
        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2);
    }

    @Test
    public void shouldReturnPositiveIfInstancesOfSubtaskAreEqualsIfIdsAreEquals() {
        subtask1.setId(3);
        subtask2.setId(3);

        assertEquals(subtask1, subtask2);
    }

    @Test
    public void shouldReturnNegativeIfAdditionOfEpicInItselfLikeSubtaskIsImpossible() {
        boolean isEpicInstanceOfSubtask = epic4 instanceof Subtask;
        Assertions.assertFalse(isEpicInstanceOfSubtask);
    }

    @Test
    public void shouldReturnNegativeIfSubtaskCannotEpicOfItself() {
        boolean isSubTaskInstanceOfEpic = subtask4 instanceof Epic;
        Assertions.assertFalse(isSubTaskInstanceOfEpic);
    }

    @Test
    public void shouldReturnPositiveIfUtilitarianClassReturnInitializedManagersInstances() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }


    @Test
    public void addNewTask() {
        final int taskId = inMemoryTaskManager.addTask(task1);

        final Task savedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask);

        final List<Task> tasks = inMemoryTaskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void addNewEpic() {
        final int epicId = inMemoryTaskManager.addEpic(epic1);

        final Epic savedEpic = inMemoryTaskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic1, savedEpic);

        final List<Epic> epics = inMemoryTaskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    public void addNewSubtask() {

        final int epicId = inMemoryTaskManager.addEpic(epic1);
        final int subtaskId = inMemoryTaskManager.addSubtaskToEpic(epicId, subtask1);

        final Subtask savedSubtask = inMemoryTaskManager.getSubtaskInEpicById(epic1.getId(), subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask1, savedSubtask);

        final List<Subtask> subtasks = inMemoryTaskManager.getSubtasksOfEpic(epic1.getId());

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    //Удаляемые подзадачи не должны хранить внутри себя старые id.
    @Test
    public void checkIfRemovedSubtasksStoreOldIds() {
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);
        epic1.addSubtask(subtask5);

        Subtask subTaskWithId2 = inMemoryTaskManager.getSubtaskInEpicById(1, 2);
        assertNull(subTaskWithId2);
    }

    @Test
    void checkTasksWithGeneratedIdHaveNoConflictWithSetId() {
        Task task3 = new Task(
                "Отдохнуть",
                "Съездить в горы",
                TaskProgress.NEW
        );
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);

        task3.setId(3);

        inMemoryTaskManager.addTask(task3);

        int task3Id = task3.getId();
        int task3IdInTasks = inMemoryTaskManager.getTaskById(3).getId();

        assertEquals(task3Id, task3IdInTasks);
    }

    @Test
    void add() {
        inMemoryHistoryManager.addTaskToHistory(task1);
        final ArrayList<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void checkImmutabilityOfTaskWithAdditionToManager() {
        inMemoryTaskManager.addTask(task1);
        final List<Task> tasks = inMemoryTaskManager.getTasks();
        assertEquals(tasks.getFirst(), task1);
    }

    @Test
    void historySavePreviousVersionOfTask() {
        inMemoryHistoryManager.addTaskToHistory(task1);
        final ArrayList<Task> history = inMemoryHistoryManager.getHistory();

        assertEquals(history.getFirst(), task1);
    }

    @Test
    void addTaskToDoubleLinkedList() {
        inMemoryHistoryManager.addTaskToHistory(task1);
        inMemoryHistoryManager.addTaskToHistory(task2);
        inMemoryHistoryManager.addTaskToHistory(task2);
        final ArrayList<Task> historyDoubleLinkedList = inMemoryHistoryManager.getDoubleLinkedList();
        assertNotNull(historyDoubleLinkedList, "История не пустая.");
        assertEquals(3, historyDoubleLinkedList.size(), "История не пустая.");
    }

    @Test
    void removeTaskNodeFromHistory() {
        inMemoryHistoryManager.addTaskToHistory(task1);
        inMemoryHistoryManager.addTaskToHistory(task2);

        final ArrayList<Task> history = inMemoryHistoryManager.getHistory();

        Node<Task> testNode = new Node<>(null, task1, null);

        inMemoryHistoryManager.removeNode(testNode);

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void checkIfTaskEntityChangeItsFields() {
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addSubtaskToEpic(1, subtask1);
        inMemoryTaskManager.addSubtaskToEpic(1, subtask2);
        inMemoryTaskManager.addSubtaskToEpic(1, subtask5);


        ArrayList<Subtask> subtasksOfEpic1 = inMemoryTaskManager.getSubtasksOfEpic(1);

        Subtask subtask1InEpicCopy = subtasksOfEpic1.get(1);
        subtask1InEpicCopy.setName("new");

        System.out.println("Копия " + subtask1InEpicCopy);
        System.out.println("Оригинал " + subtask1);

        assertNotEquals(subtask1InEpicCopy, subtask1);
    }
}