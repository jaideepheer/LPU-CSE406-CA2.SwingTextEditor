package blocs.loading_bloc;

public class LoadingEvent {
    public final String message;
    public final Status status;

    private LoadingEvent(String message, Status status) {
        this.message = message;
        this.status = status;
    }

    public enum Status {
        BEGIN_LOADING,
        END_LOADING,
        ABORT_LOADING,
        ERROR_LOADING
    }

    public static LoadingEvent beginLoading(String message)
    {
        return new LoadingEvent(message, Status.BEGIN_LOADING);
    }
    public static LoadingEvent endLoading(String message)
    {
        return new LoadingEvent(message, Status.END_LOADING);
    }
    public static LoadingEvent abortLoading(String message)
    {
        return new LoadingEvent(message, Status.ABORT_LOADING);
    }
    public static LoadingEvent errorLoading(String message)
    {
        return new LoadingEvent(message, Status.ERROR_LOADING);
    }
}
