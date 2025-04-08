package controllers;

import model.Task;
import util.Node;

import java.util.ArrayList;
import java.util.Map;

public interface HistoryManager {

    ArrayList<Task> getHistory();

    void addTaskToHistory(Task anyTask);

    /* ! ВОПРОС РЕВЬЮЕРУ !
        В задании сказано "добавить метод void remove(int id) для удаления задачи из просмотра"
        Зачем он нужен, если у нас есть метод removeNode(Node<Task> node), который удаляет
            нужный узел из истории просмотров?
    */

    void remove(int id);

    //для тестирования
    ArrayList<Task> getDoubleLinkedList();
}
