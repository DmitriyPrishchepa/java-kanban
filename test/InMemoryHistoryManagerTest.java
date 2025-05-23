import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskProgress;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class InMemoryHistoryManagerTest {
    InMemoryTaskManager taskManager;
    InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    void create() {
        taskManager = new InMemoryTaskManager();
        inMemoryHistoryManager = new InMemoryHistoryManager();
        taskManager.removeAllEpics();
        taskManager.removeAllTasks();
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

    @Test
    void shouldReturnTrueIfHistoryIsNotEmpty() {

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(1);

        Assertions.assertFalse(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnTrueIfTasksAreNotEquals() {

        task1.setId(1);
        task2.setId(2);

        inMemoryHistoryManager.addTaskToHistory(task1);
        inMemoryHistoryManager.addTaskToHistory(task2);

        System.out.println(inMemoryHistoryManager.getHistory());

        Assertions.assertEquals(2, inMemoryHistoryManager.getHistory().size());

        taskManager.removeAllTasks();

        task1.setId(0);
        task2.setId(0);
    }

    @Test
    void removeTaskWithRemovingFromHistory() {
        task1.setId(1);
        inMemoryHistoryManager.addTaskToHistory(task1);
        Assertions.assertEquals(1, inMemoryHistoryManager.getHistory().size());

        task2.setId(2);
        inMemoryHistoryManager.addTaskToHistory(task2);
        Assertions.assertEquals(2, inMemoryHistoryManager.getHistory().size());

        inMemoryHistoryManager.removeFromHistory(task1.getId());
        Assertions.assertEquals(1, inMemoryHistoryManager.getHistory().size());

        List<Task> tasks = inMemoryHistoryManager.getHistory();
        System.out.println(tasks);
    }
}
