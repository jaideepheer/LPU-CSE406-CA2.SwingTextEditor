package blocs.loading_bloc;

public class LoadingState {
    public final String message;
    public final Status status;

    public enum Status {
        IS_LOADING,
        DONE_LOADING,
        ABORTED_LOADING,
        ERRORED_LOADING
    }

    public LoadingState(String message, Status status) {
        this.message = message;
        this.status = status;
    }
}
