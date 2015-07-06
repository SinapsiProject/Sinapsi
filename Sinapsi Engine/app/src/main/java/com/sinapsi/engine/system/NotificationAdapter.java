package com.sinapsi.engine.system;

/**
 * Interface used to adapt various system-dependent calls
 * to show various types of dialogs
 */
public interface NotificationAdapter {
    public static final String SERVICE_NOTIFICATION = "SERVICE_NOTIFICATION";
    public static final String REQUIREMENT_SIMPLE_NOTIFICATIONS = "REQUIREMENT_SIMPLE_NOTIFICATIONS";

    /**
     * shows up a simple notification on the system
     * @param title the title
     * @param message the message
     */
    public void showSimpleNotification(String title, String message);
}
