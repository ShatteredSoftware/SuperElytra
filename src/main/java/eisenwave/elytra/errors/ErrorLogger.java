package eisenwave.elytra.errors;

import io.sentry.Hub;

import java.util.HashSet;

public class ErrorLogger {
    private final HashSet<String> handledExceptions = new HashSet<>();
    private final Hub hub;

    public ErrorLogger(Hub hub) {
        this.hub = hub;
    }

    public void handleError(Throwable throwable) {
        if (handledExceptions.contains(throwable.getMessage())) {
            return;
        }
        this.hub.captureException(throwable);
        this.handledExceptions.add(throwable.getMessage());
    }
}
