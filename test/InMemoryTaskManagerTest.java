import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;
import util.TaskProgress;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    InMemoryTaskManager taskManager;
    InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    void create() {
        taskManager = new InMemoryTaskManager();
        inMemoryHistoryManager = new InMemoryHistoryManager();
        taskManager.removeAllEpics();
        taskManager.removeAllTasks();
    }

    @AfterEach
    void cleanAllEpics() {
        taskManager.removeAllEpics();
    }

    Task task1 = new Task(
            "Приготовить обед",
            "Сегодня мы будем готовить мясо по французски",
            TaskProgress.NEW,
            Duration.ofSeconds(30),
            LocalDateTime.now()
    );

    Task task2 = new Task(
            "Приготовить обед",
            "Сегодня мы будем готовить мясо по французски",
            TaskProgress.NEW,
            Duration.ofSeconds(30),
            LocalDateTime.now()
    );

    Task task3 = new Task(
            "Съезить в отпуск",
            "Накопить на отпуск",
            TaskProgress.NEW,
            Duration.ofSeconds(2),
            LocalDateTime.now()
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
            TaskProgress.NEW,
            Duration.ofSeconds(2),
            LocalDateTime.now()
    );

    Subtask subtask2 = new Subtask(
            "Отпуск",
            "Планы на отпуск",
            TaskProgress.NEW,
            Duration.ofSeconds(1),
            LocalDateTime.now()
    );

    Task epic4 = new Epic(
            "Работа",
            "Задачи для работы",
            TaskProgress.NEW
    );

    Task subtask4 = new Subtask(
            "Собеседование",
            "Назначить собеседование",
            TaskProgress.NEW,
            Duration.ofSeconds(2),
            LocalDateTime.now()
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
        final int taskId = taskManager.addTask(task1);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask);

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");

        taskManager.removeAllTasks();
    }

    @Test
    public void addNewEpic() {
        final int epicId = taskManager.addEpic(epic1);

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic1, savedEpic);

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    public void addNewSubtask() {

        final int epicId = taskManager.addEpic(epic1);
        final int subtaskId = taskManager.addSubtaskToEpic(epicId, subtask1);

        final Subtask savedSubtask = taskManager.getSubtaskInEpicById(epic1.getId(), subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask1, savedSubtask);

        final List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epic1.getId());

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void checkTasksWithGeneratedIdHaveNoConflictWithSetId() {

        task1.setId(1);

        System.out.println(task1.getId());

        taskManager.addTask(task1);

        Task newTask = taskManager.getTaskById(1);

        System.out.println(newTask.getId());

        System.out.println("Задачи: " + taskManager.getTasks());

        assertEquals(task1.getId(), newTask.getId());
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
        taskManager.addTask(task1);
        final List<Task> tasks = taskManager.getTasks();
        assertEquals(tasks.getFirst(), task1);
    }

    @Test
    void historySavePreviousVersionOfTask() {
        inMemoryHistoryManager.addTaskToHistory(task1);
        final ArrayList<Task> history = inMemoryHistoryManager.getHistory();

        assertEquals(history.getFirst(), task1);
    }


    @Test
    void calculateEpicIfAllSubtasksAreNew() throws InterruptedException {
        taskManager.removeAllEpics();

        int newEpicId = taskManager.addEpic(epic1);

        Epic newEpic = taskManager.getEpicById(newEpicId);

        newEpic.addSubtask(subtask1);
        Thread.sleep(3000);
        newEpic.addSubtask(subtask2);
        Thread.sleep(3000);
        newEpic.addSubtask(
                new Subtask(
                        "new name",
                        "new descr",
                        TaskProgress.NEW,
                        Duration.ofSeconds(1),
                        LocalDateTime.now())
        );

        boolean isStatusOfEpicNew =
                taskManager.getEpics().stream().allMatch(epic -> epic.getStatus() == TaskProgress.NEW);

        assertTrue(isStatusOfEpicNew);
    }

    @Test
    void calculateEpicStatusIfAllSubtasksAreDone() throws InterruptedException {
        taskManager.removeAllEpics();

        int newEpicId = taskManager.addEpic(epic1);

        Epic newEpic = taskManager.getEpicById(newEpicId);

        newEpic.addSubtask(subtask1);
        Thread.sleep(3000);
        newEpic.addSubtask(subtask2);
        Thread.sleep(3000);
        newEpic.addSubtask(
                new Subtask(
                        "new name",
                        "new descr",
                        TaskProgress.DONE,
                        Duration.ofSeconds(1),
                        LocalDateTime.now())
        );

        System.out.println(taskManager.getEpics());

        for (Subtask sub : taskManager.getSubtasksOfEpic(newEpicId)) {
            sub.setStatus(TaskProgress.DONE);
        }

        boolean isEpicStatusDone = taskManager.getSubtasksOfEpic(newEpicId).stream().anyMatch(subtask -> subtask.getStatus() == TaskProgress.DONE);

        assertTrue(isEpicStatusDone);
    }

    @Test
    void calculateEpicStatusIfEpicHaveSubtasksNewAndDone() throws InterruptedException {
        taskManager.removeAllEpics();

        int newEpicId = taskManager.addEpic(epic1);

        Epic newEpic = taskManager.getEpicById(newEpicId);

        newEpic.addSubtask(subtask1);
        Thread.sleep(3000);
        newEpic.addSubtask(subtask2);
        Thread.sleep(3000);
        newEpic.addSubtask(
                new Subtask(
                        "new name",
                        "new descr",
                        TaskProgress.DONE,
                        Duration.ofSeconds(1),
                        LocalDateTime.now())
        );

        taskManager.updateSubtask(newEpicId, 1, new Subtask(  "Отпуск",
                "Планы на отпуск",
                TaskProgress.DONE,
                Duration.ofSeconds(2),
                LocalDateTime.now()));

        boolean isEpicStatusInProgress =
                taskManager.getSubtasksOfEpic(newEpicId).
                        stream().
                        allMatch(subtask -> subtask.getStatus() == TaskProgress.NEW);

        assertFalse(isEpicStatusInProgress);

        taskManager.removeAllEpics();
    }

    @Test
    void shouldReturnRelatedEpicForSubtasks() throws InterruptedException {
        taskManager.removeAllEpics();

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Epic epic1 = taskManager.getEpicById(1);
        Epic epic2 = taskManager.getEpicById(2);

        taskManager.addSubtaskToEpic(epic1.getId(), subtask1);
        taskManager.addSubtaskToEpic(epic2.getId(), subtask2);

        System.out.println(taskManager.getEpicById(1));
        System.out.println(taskManager.getEpicById(2));

        Subtask subtask1 = taskManager.getSubtaskInEpicById(epic1.getId(), 1);
        Subtask subtask2 = taskManager.getSubtaskInEpicById(epic2.getId(), 1);

        assertEquals(epic1.getId(), subtask1.getEpicId());
        assertEquals(epic2.getId(), subtask2.getEpicId());
    }

    @Test
    void checkCorrectTasksTimeIntersections() throws InterruptedException {

        taskManager.addTask(new Task(
                "name",
                "descr",
                TaskProgress.NEW,
                Duration.ofMinutes(1),
                LocalDateTime.now()));
        taskManager.addTask(new Task(
                "name2",
                "descr2",
                TaskProgress.NEW,
                Duration.ofMinutes(2),
                LocalDateTime.now())
        );

        assertEquals(1, taskManager.getTasks().size());
    }
}