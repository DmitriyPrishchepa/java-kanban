import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskProgress;

import java.time.Duration;
import java.time.LocalDateTime;

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
        taskManager.addTask(task1);
        taskManager.addTask(task1);

        taskManager.getTaskById(1);
        taskManager.getTaskById(1);

        Assertions.assertEquals(1, taskManager.getHistory().size());

        taskManager.removeAllTasks();
    }

//    @Test
//    void shouldRemoveFromHistory() {
//        taskManager.addTask(task1);
//        taskManager.addTask(task2);
//        taskManager.addTask(
//                new Task(
//                        "name 3",
//                        "descr 3",
//                        TaskProgress.NEW,
//                        Duration.ofMinutes(1),
//                        LocalDateTime.now()
//                )
//        );
//
//        taskManager.getTaskById(1);
//        taskManager.getTaskById(2);
//        taskManager.getTaskById(3);
//
//        System.out.println("История: " + taskManager.getHistory());
//
//        taskManager.removeTaskById(2);
//
//        System.out.println();
//
//        System.out.println("История с удалённым элементом: " + taskManager.getHistory());
//
//        Assertions.assertEquals(2, taskManager.getHistory().size());
//    }
}
