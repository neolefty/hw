package org.neolefty.cs143.hybrid_images.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import org.neolefty.cs143.hybrid_images.util.DecayHistory;
import org.neolefty.cs143.hybrid_images.util.ImageIOKit;
import org.neolefty.cs143.hybrid_images.util.SetupKit;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Loads a BufferedImage interactively. */
public class LoadImageView extends StackImageView {
    private Class prefsClass;
    private String prefsFileHistoryKey;
    private DecayHistory<String> fileHistory;
    private File curFile;
    private static ExecutorService diskAccessThreadPool = Executors.newSingleThreadExecutor();

    private static final String PREFS_FILE_HISTORY = "file_history";

    public LoadImageView(Class prefsClass, String prefsSuffix) {
        this.prefsClass = prefsClass;
        this.prefsFileHistoryKey = PREFS_FILE_HISTORY + "_" + prefsSuffix;
        //noinspection unchecked
        fileHistory = (DecayHistory<String>) SetupKit.loadPref(prefsClass, prefsFileHistoryKey);
        if (fileHistory == null)
            fileHistory = new DecayHistory<>();

        FlowPane controls = new FlowPane();
        controls.setAlignment(Pos.BOTTOM_CENTER);
        Button chooseButton = new Button("Choose ...");
        chooseButton.setOnAction(value -> loadImageInteractive());
        controls.getChildren().add(chooseButton);
        DecayHistoryMenu menu = new DecayHistoryMenu(fileHistory);
        menu.valueProperty().addListener((observable, oldValue, newValue) -> loadImage(newValue));
        controls.getChildren().add(menu);

        getChildren().add(controls);

        // load the previous image by default
        File topFile = getMostRecentFile();
        if (topFile != null && topFile.exists())
            loadImage(topFile);
    }

    /** Either the current file or the top one in the history. */
    private File getMostRecentFile() {
        if (curFile != null)
            return curFile;
        else {
            String topName = fileHistory.getTop();
            return topName == null ? null : new File(topName);
        }
    }

    /** Either the current file's directory or the top one in history. */
    private File getMostRecentDir() {
        File topFile = getMostRecentFile();
        return topFile == null ? null : topFile.getParentFile();
    }

    /** Interactively choose and display a file. */
    private void loadImageInteractive() {
        File file = chooseFile();
        if (file != null) // Cancel --> null
            loadImage(file);
    }

    /** Interactively choose a file. */
    private File chooseFile() {
        FileChooser chooser = new FileChooser();
        File topDir = getMostRecentDir();
        if (topDir != null && topDir.exists())
            chooser.setInitialDirectory(topDir);
        return chooser.showOpenDialog(getScene().getWindow());
    }

    /** Store a file in the history. */
    private void rememberFile(File file) {
        if (file != null)
            try {
                fileHistory.add(file.getCanonicalPath());
                SetupKit.savePref(prefsClass, prefsFileHistoryKey, fileHistory);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void loadImage(String filename) {
        loadImage(new File(filename));
    }

    /** Load and display an image. */
    private void loadImage(File file) {
        // load from file in a worker thread
        diskAccessThreadPool.submit(() -> {
            try {
                setImage(ImageIOKit.loadImage(file));
                this.curFile = file;
                rememberFile(file);
            } catch (IOException e) {
                try {
                    fileHistory.remove(file.getCanonicalPath());
                } catch(IOException ignored) {}
                e.printStackTrace();
            }
        });
    }
}
