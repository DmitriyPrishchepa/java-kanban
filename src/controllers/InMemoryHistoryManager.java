package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;

    private final HashMap<Integer, Node<Task>> history = new HashMap<>();
    private final ArrayList<Task> historyLinkedList = new ArrayList<>();

    public void linkLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        historyLinkedList.add(tail.data);
    }

    public ArrayList<Task> getTasks() {
        return historyLinkedList;
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyList = new ArrayList<>();

        Node<Task> first = head;

        while (first != null) {
            historyList.add(first.data);
            first = first.next;
        }

        return historyList;
    }

    @Override
    //метод для теста
    public ArrayList<Task> getDoubleLinkedList() {
        return getTasks();
    }

    @Override
    public void addTaskToHistory(Task anyTask) {
        if (anyTask == null) {
            return;
        }

        if (history.isEmpty()) {
            Node<Task> newNode = new Node<>(null, anyTask, null);
            linkLast(newNode.data);
            history.put(anyTask.getId(), newNode);
        } else {
            Node<Task> searchNode = history.getOrDefault(anyTask.getId(), null);
            if (searchNode != null) {
                removeNode(searchNode);
                history.put(anyTask.getId(), new Node<>(null, anyTask, null));
            } else {
                linkLast(anyTask);
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

    public static class Node<Task> {
        public Task data;
        public Node<Task> prev;
        public Node<Task> next;

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(data, node.data) && Objects.equals(prev, node.prev) && Objects.equals(next, node.next);
        }

        @Override
        public int hashCode() {
            int hash = 17;

            if (data != null) {
                hash = hash + data.hashCode();
            }

            hash = hash * 31;

            if (prev != null) {
                hash = hash + prev.hashCode();
            }

            hash = hash * 31;

            if (next != null) {
                hash = hash + next.hashCode();
            }

            hash = hash * 31;

            return hash;
        }
    }

}
