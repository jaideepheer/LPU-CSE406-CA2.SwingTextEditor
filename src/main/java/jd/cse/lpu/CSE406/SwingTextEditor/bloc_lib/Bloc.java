package jd.cse.lpu.CSE406.SwingTextEditor.bloc_lib;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class Bloc<EVENT_T, STATE_T> {
    private final List<StateListener<STATE_T>> registeredListeners = new LinkedList<>();
    private STATE_T oldState = getInitialState();

    abstract protected STATE_T getInitialState();
    abstract protected STATE_T mapEventToState(STATE_T currentState, EVENT_T event);

    public final STATE_T getCurrentState() {return oldState;}

    public final void register(StateListener<STATE_T> obj)
    {
        registeredListeners.add(obj);
    }

    public final void add(EVENT_T event)
    {
        CompletableFuture.runAsync(()->{
            try {
                STATE_T newState = mapEventToState(oldState, event);
                for (StateListener<STATE_T> listener : registeredListeners) {
                    listener.onStateUpdate(oldState, newState);
                }
                oldState = newState;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });
    }
}
