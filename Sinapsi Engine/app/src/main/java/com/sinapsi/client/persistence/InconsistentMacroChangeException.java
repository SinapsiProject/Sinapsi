package com.sinapsi.client.persistence;

/**
 * An exception class to indicate that a failure in the attempt to
 * add a change to the db in a non-consistent way (for example, editing
 * a deleted macro).
 */
public class InconsistentMacroChangeException extends Exception{
    public InconsistentMacroChangeException() {
        super();
    }

    public InconsistentMacroChangeException(String detailMessage) {
        super(detailMessage);
    }

    public InconsistentMacroChangeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InconsistentMacroChangeException(Throwable throwable) {
        super(throwable);
    }
}
