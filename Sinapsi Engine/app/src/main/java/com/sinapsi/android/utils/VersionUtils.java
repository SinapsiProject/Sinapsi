package com.sinapsi.android.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class containing a set of methods and classes used to
 * manage conditional execution of code by checking the version of the os
 */
public class VersionUtils {

    /**
     * Picks the first versioned task which version is less or equal of the currentVersion
     * from from the passed varargs and executes it.
     *
     * @param currentVersion
     * @param tasks
     */
    public static void versionedDo(int currentVersion, VersionedTask... tasks) {
        versionedDo(null, currentVersion, tasks);
    }

    /**
     * Picks the first versioned task which version is less or equal of the currentVersion
     * from from the passed varargs and executes it. If the current version is not enough big,
     * failedTask is executed instead.
     *
     * @param currentVersion
     * @param failedTask
     * @param tasks
     */
    public static void versionedDo(VersionedTask failedTask, int currentVersion, VersionedTask... tasks) {
        VersionedTaskManager vtm = new VersionedTaskManager(currentVersion);
        vtm.putAllFailedTask(failedTask);
        for (VersionedTask vt : tasks) {
            vtm.putTask(vt);
        }

        vtm.doTasks();
    }

    private static class VersionedTaskManager {
        private int currentVersion = 0;
        private List<VersionedTask> tasks = new ArrayList<>();
        private VersionedTask allFailedTask = null;


        public VersionedTaskManager(int currentVersion) {
            this.currentVersion = currentVersion;
        }


        public VersionedTaskManager putTask(VersionedTask vk) {
            tasks.add(vk);
            return this;
        }

        public VersionedTaskManager putAllFailedTask(VersionedTask vk) {
            allFailedTask = vk;
            return this;
        }

        public void doTasks() {
            Collections.sort(tasks, new VersionedTaskComparator());
            Collections.reverse(tasks);

            for (VersionedTask vk : tasks) {
                if (currentVersion >= vk.getRequiredVersion()) {
                    vk.doTask();
                    return;
                }
            }

            if (allFailedTask != null) allFailedTask.doTask();
        }

    }

    /**
     * Abstract class representing a specific task with a minimum system version
     * requirement.
     */
    public static abstract class VersionedTask {
        private int required = 0;

        public VersionedTask(int version) {
            required = version;
        }

        public int getRequiredVersion() {
            return required;
        }

        public abstract void doTask();
    }


    private static class VersionedTaskComparator implements Comparator<VersionedTask> {

        @Override
        public int compare(VersionedTask lhs, VersionedTask rhs) {
            return lhs.getRequiredVersion() - rhs.getRequiredVersion();
        }
    }

}
