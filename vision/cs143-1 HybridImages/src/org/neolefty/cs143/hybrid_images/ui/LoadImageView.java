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

        getChildren().add(controls);

        // load the previous image by default
        File topFile = topFile();
        if (topFile != null && topFile.exists())
            loadImage(topFile);
    }


    private File topFile() {
        String topName = fileHistory.getTop();
        return topName == null ? null : new File(topName);
    }

    private File topDir() {
        File topFile = topFile();
        return topFile == null ? null : topFile.getParentFile();
    }

    private void loadImageInteractive() {
        File file = chooseFile();
        if (file != null)
            loadImage(file);
    }

    private File chooseFile() {
        FileChooser chooser = new FileChooser();
        File topDir = topDir();
        if (topDir != null && topDir.exists())
            chooser.setInitialDirectory(topDir);
        File file = chooser.showOpenDialog(getScene().getWindow());
        if (file != null)
            try {
                fileHistory.add(file.getCanonicalPath());
                SetupKit.savePref(prefsClass, prefsFileHistoryKey, fileHistory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return file;
    }

    private void loadImage(File file) {
        // load from file in a worker thread
        diskAccessThreadPool.submit(() -> {
            try {
                setImage(ImageIOKit.loadImage(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
