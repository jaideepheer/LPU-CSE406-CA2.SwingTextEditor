package jd.cse.lpu.CSE406.SwingTextEditor.bloc_lib;

public interface StateListener<STATE_T> {
    void onStateUpdate(STATE_T oldState, STATE_T newState);
}
