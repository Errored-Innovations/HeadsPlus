package io.github.thatsmusic99.headsplus.util.events;

import java.util.HashMap;

public class HeadsPlusException extends Exception {

    private final HashMap<String, String> exceptionInfo;
    private final Exception originalException;

    // Invocation exception but better
    public HeadsPlusException(Exception originalException, HashMap<String, String> exInfo) {
        this.originalException = originalException;
        this.exceptionInfo = exInfo;
    }

    public HeadsPlusException(Exception ex) {
        this.originalException = ex;
        this.exceptionInfo = new HashMap<>();
    }

    public Exception getOriginalException() {
        return originalException;
    }

    public HashMap<String, String> getExceptionInfo() {
        return exceptionInfo;
    }
}
