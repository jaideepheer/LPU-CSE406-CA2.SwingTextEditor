package jd.cse.lpu.CSE406.SwingTextEditor;

import jd.cse.lpu.CSE406.SwingTextEditor.blocs.fileevent_bloc.FileEvent;
import jd.cse.lpu.CSE406.SwingTextEditor.blocs.fileevent_bloc.FileEventBloc;
import jd.cse.lpu.CSE406.SwingTextEditor.blocs.fileevent_bloc.FileState;
import jd.cse.lpu.CSE406.SwingTextEditor.blocs.loading_bloc.LoadingState;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class TextEditor extends JFrame {
    // components
    private JTextArea mainTextArea;
    private JScrollPane mainTextScrollPane;
    private JLabel cursorPositionLabel;
    private JPanel mainPanel;

    // menu items
    private JMenuItem exitMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem newMenuItem;
    private JMenuItem saveAsMenuItem;

    private JMenuItem aboutMenuItem;

    private JDialog loadingDialog;

    public void onFileStateUpdate(FileState oldState, FileState newState) {
        SwingUtilities.invokeLater(()->{
            mainTextArea.setText(newState.currentText);
            mainTextArea.addKeyListener(new KeyAdapter(){
                @Override
                public void keyTyped(KeyEvent e) {
                    setTitle(getTitle()+"*");
                    mainTextArea.removeKeyListener(this);
                }
            });
            setTitle(
                    newState.currentFile == null ?
                            "untitled" :
                    newState.currentFile.getFileName().toString()
            );
        });
    }

    public void onLoadingStateUpdate(LoadingState oldState, LoadingState newState)
    {
        SwingUtilities.invokeLater(()->{
            switch (newState.status)
            {
                case IS_LOADING:
                    setLoadingDialog(newState.message);
                    break;
                case DONE_LOADING:
                    if(loadingDialog!=null) loadingDialog.dispose();
                    break;
                // TODO: show errors
                case ERRORED_LOADING:
                    break;
                case ABORTED_LOADING:
                    break;
            }
        });
    }

    // Constructor to register event handlers
    public TextEditor(FileEventBloc fileEventBloc) {

        // register self with FileEventBloc and its loading bloc
        fileEventBloc.register(this::onFileStateUpdate);
        fileEventBloc.registerForLoadingState(this::onLoadingStateUpdate);

        // Add caret position updater
        mainTextArea.addCaretListener(event -> {
            try {
                JTextArea source = (JTextArea)(event.getSource());
                int pos = source.getCaretPosition();
                int line = source.getLineOfOffset(pos);
                int col = pos - source.getLineStartOffset(line);
                ++line;++col;
                cursorPositionLabel.setText(String.format("Ln %d, Col %d", line, col));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });

        ActionListener saveAsFile = event -> displayFileChooser(true)
                .ifPresent(file -> fileEventBloc.add(FileEvent.fileSaveAsEvent(file, mainTextArea.getText())));
        ActionListener saveFile = event -> {
            // save if file exists on disk
            if(fileEventBloc.getCurrentState().currentFile != null)
            {
                fileEventBloc.add(FileEvent.fileSaveEvent(mainTextArea.getText()));
            }
            // else goto save-as
            else saveAsFile.actionPerformed(event);
        };

        newMenuItem.addActionListener(event -> main.make_new_editor());
        openMenuItem.addActionListener(event ->
            displayFileChooser(false)
                    .ifPresent( openFile -> fileEventBloc.add(FileEvent.fileOpenEvent(openFile)))
        );
        saveMenuItem.addActionListener(saveFile);
        saveAsMenuItem.addActionListener(saveAsFile);
        exitMenuItem.addActionListener(event -> {
            unsavedExit dialog = new unsavedExit();
            dialog.pack();
            dialog.setVisible(true);
            switch (dialog.result)
            {
                case SAVE:
                    saveFile.actionPerformed(event);
                case DISCARD:
                    dispose();
                    break;
                case CANCELL:
                    break;
            }
        });

        add($$$getRootComponent$$$());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(720,480);
        setLocationRelativeTo(null);
        setVisible(true);

        // set initial state
        fileEventBloc.add(FileEvent.fileCloseEvent());
    }

    private void setLoadingDialog(String message)
    {
        loadingDialog = new JDialog(this, "Loading", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(message), BorderLayout.CENTER);
        loadingDialog.add(panel);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setVisible(true);
    }

    private Optional<Path> displayFileChooser(boolean toSave)
    {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter txt = new FileNameExtensionFilter("Text Files (*.txt)","txt");
        FileNameExtensionFilter doc = new FileNameExtensionFilter("DOC Files (*.doc)","doc");
        FileNameExtensionFilter pdf = new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
        fc.addChoosableFileFilter(doc);
        fc.addChoosableFileFilter(pdf);
        fc.setFileFilter(txt);
        fc.setAcceptAllFileFilterUsed(!toSave);

        // display dialog
        int chosen;
        if(toSave)
            chosen = fc.showSaveDialog(mainPanel);
        else
            chosen = fc.showOpenDialog(mainPanel);

        if(chosen == JFileChooser.APPROVE_OPTION)
        {
            if(!toSave)
                return Optional.of(fc.getSelectedFile().toPath());

            Path fpath = fc.getSelectedFile().toPath();
            // Fix file extension
            String required_ext;
            if (fc.getFileFilter() == txt) required_ext = ".txt";
            else if (fc.getFileFilter() == pdf) required_ext = ".pdf";
            else if (fc.getFileFilter() == doc) required_ext = ".doc";
            else required_ext = ".txt";

            // If user typed the correct file ext. in name, then return
            Optional<String> user_ext = main.get_file_ext(fpath);
            if(user_ext.isPresent() && user_ext.get().equals(required_ext))
                return Optional.of(fpath);

            // return correct path with required_ext appended
            return Optional.of(Paths.get(fpath.toString() + required_ext));
        }
        else return Optional.empty();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        final JMenuBar menuBar1 = new JMenuBar();
        menuBar1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
        mainPanel.add(menuBar1, BorderLayout.NORTH);
        final JMenu menu1 = new JMenu();
        menu1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        Font menu1Font = this.$$$getFont$$$(null, -1, -1, menu1.getFont());
        if (menu1Font != null) menu1.setFont(menu1Font);
        menu1.setText("File");
        menuBar1.add(menu1);
        newMenuItem = new JMenuItem();
        newMenuItem.setText("New");
        menu1.add(newMenuItem);
        openMenuItem = new JMenuItem();
        openMenuItem.setText("Open");
        menu1.add(openMenuItem);
        final JSeparator separator1 = new JSeparator();
        menu1.add(separator1);
        saveMenuItem = new JMenuItem();
        saveMenuItem.setText("Save");
        menu1.add(saveMenuItem);
        saveAsMenuItem = new JMenuItem();
        saveAsMenuItem.setText("Save As");
        menu1.add(saveAsMenuItem);
        final JSeparator separator2 = new JSeparator();
        menu1.add(separator2);
        exitMenuItem = new JMenuItem();
        exitMenuItem.setText("Exit");
        menu1.add(exitMenuItem);
        final JMenu menu2 = new JMenu();
        menu2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        menu2.setEnabled(true);
        menu2.setText("Edit");
        menuBar1.add(menu2);
        final JMenuItem menuItem1 = new JMenuItem();
        menuItem1.setText("Clear");
        menu2.add(menuItem1);
        final JMenu menu3 = new JMenu();
        menu3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        menu3.setText("Help");
        menuBar1.add(menu3);
        aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("About");
        menu3.add(aboutMenuItem);
        mainTextScrollPane = new JScrollPane();
        mainPanel.add(mainTextScrollPane, BorderLayout.CENTER);
        mainTextArea = new JTextArea();
        mainTextScrollPane.setViewportView(mainTextArea);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        mainPanel.add(panel1, BorderLayout.SOUTH);
        cursorPositionLabel = new JLabel();
        cursorPositionLabel.setText("Ln 1, Col 1");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.ipadx = 10;
        panel1.add(cursorPositionLabel, gbc);
        final JSeparator separator3 = new JSeparator();
        separator3.setOrientation(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel1.add(separator3, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     *
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
