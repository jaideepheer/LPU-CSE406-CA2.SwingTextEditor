package jd.cse.lpu.CSE406.SwingTextEditor;

import jd.cse.lpu.CSE406.SwingTextEditor.blocs.fileevent_bloc.FileEventBloc;

import java.nio.file.Path;
import java.util.Optional;

public class main {
    public static void main(String[] args) {
        make_new_editor();
    }

    public static void make_new_editor() {
        FileEventBloc fileEventBloc = new FileEventBloc();
        TextEditor editor = new TextEditor(fileEventBloc);
    }

    /**
     * Returns extension in lower case with dot(.).
     * @param file to get extension
     * @return extension in lower case like ".txt"
     */
    public static Optional<String> get_file_ext(Path file)
    {
        String fname = file.getFileName().toString();
        int idx = fname.lastIndexOf('.');
        if(idx == -1)
            return Optional.empty();
        return Optional.of(fname.substring(idx).toLowerCase());
    }
}
