import controllers.FileBackedTaskManager;
import exceptions.ManagerSaveException;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static util.TaskProgress.NEW;

public class BackedTaskManagerTest {

    static FileBackedTaskManager manager;
    static Path path;

    static Task task1 = new Task(
            "name1",
            "descr1",
            NEW,
            Duration.ofSeconds(1),
            LocalDateTime.now()
    );
    static Task task2 = new Task(
            "name2",
            "descr2",
            NEW,
            Duration.ofSeconds(1),
            LocalDateTime.now()
    );

    @BeforeAll
    static void createFile() throws IOException {
        path = File.createTempFile("testTasks", ".csv").toPath();
        manager = FileBackedTaskManager.loadFromFile(path);
        manager.removeAllTasks();
        manager.addTask(task1);
        manager.addTask(task2);
    }

    @Test
    void shouldCreateFile() {
        Assertions.assertNotNull(path);
    }

    @Test
    void shouldSaveSeveralTasks() {

        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path.getFileName()), StandardCharsets.UTF_8))) {

            while (br.ready()) {
                String stringValue = br.readLine();
                System.out.println("Tasks: " + stringValue);
                Assertions.assertFalse(stringValue.isEmpty());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл", e);
        }
    }

    @Test
    void shouldLoadFromFile() {
        System.out.println(manager.getTasks());
        Assertions.assertFalse(manager.getTasks().isEmpty());
    }

    @Test
    void testSaveToFileException() {
        Path path = Paths.get("C://Users//Дмитрий//java-kanbin//java-kanban//tasks.csv");
        FileBackedTaskManager man = new FileBackedTaskManager(
                path,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                0,
                0,
                0
        );
        assertThrows(ManagerSaveException.class,
                man::save
        );
    }

//    @Test
//    void testLoadFromFileException() throws FileNotFoundException {
//        new PrintWriter(String.valueOf(path)).close();
//        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(path));
//    }
}