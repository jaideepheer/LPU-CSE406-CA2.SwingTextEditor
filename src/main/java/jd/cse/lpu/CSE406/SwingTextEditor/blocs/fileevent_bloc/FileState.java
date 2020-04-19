package jd.cse.lpu.CSE406.SwingTextEditor.blocs.fileevent_bloc;

import java.nio.file.Path;

public class FileState {
    public final String currentText;
    public final Path currentFile;

    public FileState(String currentText, Path currentFile) {
        this.currentText = currentText;
        this.currentFile = currentFile;
    }
}
