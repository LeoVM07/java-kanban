package Managers;

import Tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Node<Task> first;
    Node<Task> last;
    Map<Integer, Node<Task>> idToNode;

    public InMemoryHistoryManager() {
        this.idToNode = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    // код по сути дублирует LinkedList, тут добавить особо нечего ¯\_(ツ)_/¯
    @Override
    public void remove(int id) {
        removeNode(idToNode.get(id));
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    private static class Node<T> {

        public Task data;

        public Node<Task> next;

        public Node<Task> prev;

        public Node(Task data, Node<Task> next, Node<Task> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

    }

    private void linkLast(Task task) {
        Node<Task> oldLast = last;
        Node<Task> newNode = new Node<>(task, null, oldLast);
        last = newNode;
        idToNode.put(task.getId(), newNode);
        if (oldLast == null)
            first = newNode;
        else
            oldLast.next = newNode;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            Node<Task> next = node.next;
            Node<Task> prev = node.prev;
            node.data = null;

            if (first == node && last == node) {
                first = null;
                last = null;
            } else if (first == node) {
                first = next;
                first.prev = null;
            } else if (last == node) {
                last = prev;
                last.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }

        }
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> countingTask = first;
        while (countingTask != null) {
            tasks.add(countingTask.data);
            countingTask = countingTask.next;
        }
        return tasks;
    }
}
