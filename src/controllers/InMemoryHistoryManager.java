package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;

    private final HashMap<Integer, Node> history = new HashMap<>();

    public void linkLast(Task element) {
        final Node newNode = new Node(tail, element, null);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyList = new ArrayList<>();

        Node first = head;

        while (first != null) {
            historyList.add(first.data);
            first = first.next;
        }

        return historyList;
    }

    @Override
    public void addTaskToHistory(Task anyTask) {
        if (anyTask == null) {
            return;
        }

        int anyTaskId = anyTask.getId();
        Node node = history.get(anyTaskId);

        if (node != null) {
            removeNode(node);
        } else {
            linkLast(anyTask);
            history.put(anyTaskId, tail);
        }
    }


    @Override
    public void removeFromHistory(int id) {
        if (history.containsKey(id)) {
            Node node = history.get(id);
            removeNode(node);
            history.remove(id);
        }
    }

    public void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    public static class Node {
        public Task data;
        public Node prev;
        public Node next;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
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