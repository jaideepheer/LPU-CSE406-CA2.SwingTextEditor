import blocs.fileevent_bloc.FileEventBloc;

public class main {
    public static void main(String[] args) {
        make_new_editor();
    }

    public static void make_new_editor() {
        FileEventBloc fileEventBloc = new FileEventBloc();
        TextEditor editor = new TextEditor(fileEventBloc);
    }
}
