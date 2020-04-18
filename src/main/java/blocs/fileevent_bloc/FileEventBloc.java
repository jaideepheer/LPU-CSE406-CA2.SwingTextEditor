package blocs.fileevent_bloc;

import bloc_lib.Bloc;
import bloc_lib.StateListener;
import blocs.loading_bloc.LoadingBloc;
import blocs.loading_bloc.LoadingEvent;
import blocs.loading_bloc.LoadingState;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileEventBloc extends Bloc<FileEvent, FileState> {
    private final LoadingBloc loadingBloc;

    public FileEventBloc()
    {
        this(new LoadingBloc());
    }
    public FileEventBloc(LoadingBloc loadingBloc)
    {
        this.loadingBloc = loadingBloc;
    }

    public void registerForLoadingState(StateListener<LoadingState> listener)
    {
        loadingBloc.register(listener);
    }

    @Override
    public FileState getInitialState() {
        return new FileState(null, null);
    }

    @Override
    public FileState mapEventToState(FileState currentState, FileEvent event) {
        Path toSave = currentState.currentFile;
        FileState retVal;
        switch (event.eventType)
        {
            case FILE_OPEN:
                try {
                    loadingBloc.add(LoadingEvent.beginLoading(String.format("Opening file '%s'.", event.file.getFileName().toString())));
                    retVal = new FileState(new String(Files.readAllBytes(event.file)), event.file);
                    loadingBloc.add(LoadingEvent.endLoading("File opened."));
                } catch (Exception e) {
                    e.printStackTrace();
                    String error = "Error: File read IOException.\n"+e.toString();
                    retVal = new FileState(error, null);
                    loadingBloc.add(LoadingEvent.errorLoading(error));
                }
                break;

            case FILE_SAVE_AS:
                toSave = event.file;
            case FILE_SAVE:
                try {
                    loadingBloc.add(LoadingEvent.beginLoading(String.format("Saving file as '%s'.", toSave.getFileName().toString())));
                    Files.write(toSave, event.currentText.getBytes());
                    loadingBloc.add(LoadingEvent.endLoading("File saved."));
                } catch (Exception e) {
                    e.printStackTrace();
                    String error = "Error: File write IOException.\n"+e.toString();
                    loadingBloc.add(LoadingEvent.errorLoading(error));
                }
                retVal = new FileState(event.currentText, toSave);
                break;

            case FILE_CLOSE:
            default:
                retVal = getInitialState();
        }
        return retVal;
    }
}
