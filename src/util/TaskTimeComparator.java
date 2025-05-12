package util;

import model.Task;

import java.util.Comparator;

public class TaskTimeComparator {

    public static Comparator<Task> getTimeComparator() {
        return (o1, o2) -> {

            if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return 1;
            }

            if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return -1;
            }

            return 0;
        };
    }
}
