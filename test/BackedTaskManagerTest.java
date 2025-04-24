import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.FileBackedTaskManager;
import util.TaskProgress;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BackedTaskManagerTest {

    FileBackedTaskManager manager;
    static Path path = Paths.get("C://Users//Дмитрий//javaKanban//java-kanban//tasks.csv");

//    @BeforeAll
//    static void createFile() throws IOException {
//        path = File.createTempFile("tasks", ".csv").toPath();
//    }

    @BeforeEach
    void create() {
        manager = FileBackedTaskManager.loadFromFile(path);
    }

    @Test
    void shouldCreateFile() {
        Assertions.assertNotNull(path);
    }

    @Test
    void shouldSaveSeveralTasks() throws IOException {

        String stringValue = Files.readString(path);

        System.out.println(stringValue);

        Assertions.assertFalse(stringValue.isEmpty());
    }

    @Test
    void shouldLoadFromFile() {
        Task task1 = new Task("name", "descr", TaskProgress.NEW);
        Task task2 = new Task("name2", "descr2", TaskProgress.NEW);

        manager.addTask(task1);
        manager.addTask(task2);

        Assertions.assertFalse(manager.getTasks().isEmpty());
    }
}
