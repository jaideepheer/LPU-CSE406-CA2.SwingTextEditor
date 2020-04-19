package jd.cse.lpu.CSE406.SwingTextEditor.blocs.fileevent_bloc;

import jd.cse.lpu.CSE406.SwingTextEditor.bloc_lib.Bloc;
import jd.cse.lpu.CSE406.SwingTextEditor.bloc_lib.StateListener;
import jd.cse.lpu.CSE406.SwingTextEditor.blocs.loading_bloc.LoadingBloc;
import jd.cse.lpu.CSE406.SwingTextEditor.blocs.loading_bloc.LoadingEvent;
import jd.cse.lpu.CSE406.SwingTextEditor.blocs.loading_bloc.LoadingState;
import jd.cse.lpu.CSE406.SwingTextEditor.main;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;

import java.io.IOException;
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

    private Document generatePDFFromText(String text) throws IOException {
        Document document = new Document(40, 50, 40, 60);
        Paragraph paragraph = new Paragraph();
        paragraph.addText(text, 20, PDType1Font.HELVETICA);
        document.add(paragraph);
        return document;
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
                    switch (main.get_file_ext(toSave).orElse(".txt"))
                    {
                        case ".pdf":
                            generatePDFFromText(event.currentText)
                                    .save(toSave.toFile());
                            break;
                        case ".doc":
                        case ".txt":
                            Files.write(toSave, event.currentText.getBytes());
                    }
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
