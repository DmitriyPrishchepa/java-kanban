package controllers;

import model.Task;
import util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> history = new HashMap<>();
    private final HistoryDoublyLinkedList<Task> doubleLinkedList = new HistoryDoublyLinkedList<>();

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyList = new ArrayList<>();
        if (!history.isEmpty()) {
            for (Node<Task> node : history.values()) {
                historyList.add(node.data);
            }
            return historyList;
        }
        return new ArrayList<>();
    }

    @Override
    //метод для теста
    public ArrayList<Task> getDoubleLinkedList() {
        return doubleLinkedList.getTasks();
    }

    @Override
    public void addTaskToHistory(Task anyTask) {
        if (anyTask == null) {
            return;
        }

        if (history.isEmpty()) {
            Node<Task> newNode = new Node<>(null, anyTask, null);
            System.out.println("id" + anyTask.getId());
            doubleLinkedList.linkLast(newNode.data);
            history.put(anyTask.getId(), newNode);
        } else {
            Node<Task> searchNode = history.getOrDefault(anyTask.getId(), null);
            if (searchNode != null) {
                removeNode(searchNode);
                history.put(anyTask.getId(), new Node<>(null, anyTask, null));
            } else {
                doubleLinkedList.linkLast(anyTask);
                history.put(anyTask.getId(), new Node<>(null, anyTask, null));
            }
        }
    }

    @Override
    public void remove(int id) {
        history.values().removeIf(task -> task.data.getId() == id);
    }

    public void removeNode(Node<Task> node) {
        history.values().removeIf(taskNode -> taskNode.equals(node));
    }

    public static class HistoryDoublyLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size = 0;

        private final ArrayList<T> list = new ArrayList<>();

        public void linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            list.add(tail.data);
            size++;
        }

        public ArrayList<T> getTasks() {
            return list;
        }
    }
}
