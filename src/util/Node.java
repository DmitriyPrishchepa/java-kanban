package util;

import java.util.Objects;

public class Node<E> {
    public E data;
    public Node<E> prev;
    public Node<E> next;

    public Node(Node<E> prev, E data, Node<E> next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
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
