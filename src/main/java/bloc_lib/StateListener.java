package bloc_lib;

public interface StateListener<STATE_T> {
    void onStateUpdate(STATE_T oldState, STATE_T newState);
}
