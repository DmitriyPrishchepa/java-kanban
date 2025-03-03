import controllers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import util.TaskProgress;

import java.util.Scanner;

public class Main {

    public static Scanner scanner = new Scanner(System.in);
    public static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        System.out.println("Поехали!");

        while (true) {
            printMenu();

            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1:
                    System.out.println("Чтобы создать задачу, нужно ввести данные:");
                    System.out.println("Введите название задачи");
                    String taskName = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String taskDescription = scanner.nextLine();
                    taskManager.addTask(new Task(taskName, taskDescription, TaskProgress.NEW));
                    break;
                case 2:
                    System.out.println("Чтобы создать Эпик, нужно ввести данные:");
                    System.out.println("Введите название эпика:");
                    String epicName = scanner.nextLine();
                    System.out.println("Введите описание эпика:");
                    String epicDescription = scanner.nextLine();
                    taskManager.addEpic(new Epic(epicName, epicDescription, TaskProgress.NEW));
                    break;
                case 3:
                    System.out.println("Введите id эпика, в который хотите добавить задачу:");
                    int epicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите название подзадачи:");
                    String subTaskName = scanner.nextLine();
                    System.out.println("Введите описание подзадачи");
                    String subTaskDescription = scanner.nextLine();
                    taskManager.addSubtaskToEpic(epicId, new Subtask(subTaskName, subTaskDescription, TaskProgress.NEW));
                    break;
                case 4:
                    System.out.println("Чтобы обновить задачу, нужно ввести данные:");
                    System.out.println("Введите id задачи, которую вы хотите изменить:");
                    int neededId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Поменяйте название задачи");
                    String updatedName = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String updatedDescription = scanner.nextLine();
                    System.out.println("Введите новый статус задачи:");
                    String updatedStatus = scanner.nextLine();
                    taskManager.updateTask(neededId,
                            new Task(updatedName,
                                    updatedDescription,
                                    TaskProgress.valueOf(updatedStatus)
                            )
                    );
                    break;
                case 5:
                    System.out.println("Чтобы обновить задачу в эпике, нужно ввести данные:");
                    System.out.println("Введите id эпика, к которм вы хотите изменить задачу:");
                    int neededEpicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите id задачи в эпике, которую нужно обновить:");
                    int neededSubtaskId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Поменяйте название задачи");
                    String updatedSubTaskName = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String updatedSubtaskDescription = scanner.nextLine();
                    System.out.println("Введите новый статус задачи:");
                    String updatedSubtaskStatus = scanner.nextLine();
                    taskManager.updateSubtask(
                            neededEpicId,
                            neededSubtaskId,
                            new Subtask(updatedSubTaskName,
                                    updatedSubtaskDescription,
                                    TaskProgress.valueOf(updatedSubtaskStatus)
                            )
                    );
                    break;
                case 6:
                    System.out.println(taskManager.getTasks());
                    break;
                case 7:
                    System.out.println(taskManager.getEpics());
                    break;
                case 8:
                    System.out.println(taskManager.getSubtasks());
                    break;
                case 9:
                    System.out.println("Введите id эпика, чьи задачи нужно вывести:");
                    int idOfEpic = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.getSubtasksOfEpic(idOfEpic);
                    System.out.println(taskManager.getSubtasksOfEpic(idOfEpic));
                    break;
                case 10:
                    System.out.println("Введите id задачи, которую вы хотите найти: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.getTaskById(id);
                    System.out.println(taskManager.getTaskById(id));
                    break;
                case 11:
                    System.out.println("Введите id подзадачи: ");
                    int subId = scanner.nextInt();
                    taskManager.getSubtaskById(subId);
                    System.out.println(taskManager.getSubtaskById(subId));
                    break;
                case 12:
                    System.out.println("Введите id эпика, который вы хотите найти: ");
                    int findingEpicId = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.getEpicById(findingEpicId);
                    System.out.println(taskManager.getEpicById(findingEpicId));
                    break;
                case 13:
                    System.out.println("Введите id эпика, подзадачу которого вы хотите найти: ");
                    int findEpicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите id подзадачи, которую вы хотите найти: ");
                    int findSubTaskId = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.getSubtaskInEpicById(findEpicId, findSubTaskId);
                    System.out.println(taskManager.getSubtaskInEpicById(findEpicId, findSubTaskId));
                    break;
                case 14:
                    System.out.println("Введите id задачи, которую вы хотите удалить: ");
                    int removingTaskId = scanner.nextInt();
                    taskManager.removeTaskById(removingTaskId);
                    break;
                case 15:
                    System.out.println("Введите id эпика, который вы хотите удалить: ");
                    int removingEpicId = scanner.nextInt();
                    taskManager.removeEpicById(removingEpicId);
                    break;
                case 16:
                    System.out.println("Введите id эпика, подзадачу в которм вы хотите удалить ");
                    int removingEpicSubtaskId = scanner.nextInt();
                    System.out.println("Введите id подзадачи, которую вы хотите удалить ");
                    int removingSubtaskId = scanner.nextInt();
                    taskManager.removeSubtaskById(removingEpicSubtaskId, removingSubtaskId);
                    break;
                case 17:
                    taskManager.removeAllTasks();
                    break;
                case 18:
                    taskManager.removeAllEpics();
                    break;
                case 19:
                    taskManager.removeAllSubtasks();
                    break;
                case 20:
                    System.out.println("Вввдите id эпика:");
                    int epicId2 = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.removeAllSubtasksOfEpic(epicId2);
                    System.out.println(taskManager.removeAllSubtasksOfEpic(epicId2));
                    break;
                case 21:
                    System.out.println("Выход из программы");
                    return;
                default:
                    System.out.println("Такой команды нет");
                    break;
            }
        }
    }

    public static void printMenu() {
        System.out.println("Что хотите сделать?");
        System.out.println("1 - добавить задачу");
        System.out.println("2 - добавить эпик");
        System.out.println("3 - добавить задачу в эпик");
        System.out.println("4 - обновить задачу");
        System.out.println("5 - обновить задачу в эпике");
        System.out.println("6 - вывести список задач");
        System.out.println("7 - вывести список эпиков");
        System.out.println("8 - вывести список ВСЕХ подзадач");
        System.out.println("9 - вывести список подзадач эпика");
        System.out.println("10 - найти задачу по id");
        System.out.println("11 - найти подзадачу по id");
        System.out.println("12 - найти эпик по id");
        System.out.println("13 - найти подзадачу в эпике по id");
        System.out.println("14 - удалить задачу по id");
        System.out.println("15 - удалить эпик по id");
        System.out.println("16 - удалить подзадачу в эпике по id");
        System.out.println("17 - удалить все задачи");
        System.out.println("18 - удалить все эпики");
        System.out.println("19 - удалить все подзадачи");
        System.out.println("20 - удалить все подзадачи в эпике");
        System.out.println("21 - выйти из программы");
    }
}
