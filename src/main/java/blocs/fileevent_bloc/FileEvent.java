package blocs.fileevent_bloc;

import java.nio.file.Path;

public class FileEvent {
    public final Path file;
    public final String currentText;
    public final Type eventType;

    private FileEvent(Path file, String currentText, Type eventType) {
        this.file = file;
        this.currentText = currentText;
        this.eventType = eventType;
    }

    public enum Type {
        FILE_OPEN,
        FILE_CLOSE,
        FILE_SAVE,
        FILE_SAVE_AS
    }

    public static FileEvent fileOpenEvent(Path file)
    {
        return new FileEvent(file, null, Type.FILE_OPEN);
    }
    public static FileEvent fileCloseEvent()
    {
        return new FileEvent(null, null, Type.FILE_CLOSE);
    }
    public static FileEvent fileSaveEvent(String newText)
    {
        return new FileEvent(null, newText, Type.FILE_SAVE);
    }
    public static FileEvent fileSaveAsEvent(Path file, String newText)
    {
        return new FileEvent(file, newText, Type.FILE_SAVE_AS);
    }
}