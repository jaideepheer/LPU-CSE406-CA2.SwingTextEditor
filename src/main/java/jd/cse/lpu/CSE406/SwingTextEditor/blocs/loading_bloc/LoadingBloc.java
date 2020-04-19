package jd.cse.lpu.CSE406.SwingTextEditor.blocs.loading_bloc;

import jd.cse.lpu.CSE406.SwingTextEditor.bloc_lib.Bloc;

public class LoadingBloc extends Bloc<LoadingEvent, LoadingState> {
    @Override
    protected LoadingState getInitialState() {
        return new LoadingState(null, LoadingState.Status.DONE_LOADING);
    }

    @Override
    protected LoadingState mapEventToState(LoadingState currentState, LoadingEvent event) {
        LoadingState.Status status;
        switch (event.status)
        {
            case BEGIN_LOADING:
                status = LoadingState.Status.IS_LOADING;
                break;
            case END_LOADING:
                status = LoadingState.Status.DONE_LOADING;
                break;
            case ABORT_LOADING:
                status = LoadingState.Status.ABORTED_LOADING;
                break;
            case ERROR_LOADING:
                status = LoadingState.Status.ERRORED_LOADING;
                break;
            default:
                return new LoadingState("Unhandled loading bloc event.", LoadingState.Status.ERRORED_LOADING);
        }
        return new LoadingState(event.message, status);
    }
}
