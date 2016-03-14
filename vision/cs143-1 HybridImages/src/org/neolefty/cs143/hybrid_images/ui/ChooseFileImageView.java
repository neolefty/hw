package org.neolefty.cs143.hybrid_images.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import org.neolefty.cs143.hybrid_images.img.ImageProcessor;
import org.neolefty.cs143.hybrid_images.ui.util.FileHistoryMenu;
import org.neolefty.cs143.hybrid_images.ui.util.FilenameShortener;
import org.neolefty.cs143.hybrid_images.ui.util.ProcessedBI;
import org.neolefty.cs143.hybrid_images.util.DecayHistory;
import org.neolefty.cs143.hybrid_images.util.SetupKit;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Loads a BufferedImage interactively. */
public class ChooseFileImageView extends StackImageView {
    private Class prefsClass;
    private String prefsFileHistoryKey;
    private DecayHistory<String> fileHistory;
    private static ExecutorService diskAccessThreadPool = Executors.newSingleThreadExecutor();
    private ImageProcessor preprocessor = null;
    private ReadOnlyObjectWrapper<ProcessedBI> imageProperty = new ReadOnlyObjectWrapper<>();

    private static final String PREFS_FILE_HISTORY = "file_history";

    public ChooseFileImageView(Class prefsClass, String prefsSuffix) {
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
        FileHistoryMenu menu = new FileHistoryMenu(fileHistory);
        menu.valueProperty().addListener((observable, oldValue, newValue) -> loadImage(newValue));
        controls.getChildren().add(menu);

        if (getControlPane().getBottom() != null) throw new IllegalStateException();
        getControlPane().setBottom(controls);

        // wait until we're made visible for the first time before loading our image
        parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (newValue != null) { // if we're added to a parent
                    parentProperty().removeListener(this); // only trigger once
                    // load the previous image by default
                    File topFile = getMostRecentFile();
                    if (topFile != null && topFile.exists())
                        loadImage(topFile);
                }
            }
        });
    }

    /** Something that processes the image as soon as it's loaded. */
    public void setPreprocessor(ImageProcessor preprocessor) {
        this.preprocessor = preprocessor;
    }

    /** Either the current file or the top one in the history. */
    private File getMostRecentFile() {
        ProcessedBI pbi = imageProperty.getValue();
        if (pbi == null) {
            String topName = fileHistory.getTop();
            return topName == null ? null : new File(topName);
        }
        else
            return pbi.getFile();
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

    private void loadImage(FilenameShortener shortener) {
        if (shortener != null)
            loadImage(shortener.getVerbose());
    }

    private void loadImage(String filename) {
        loadImage(new File(filename));
    }

    /** Load and display an image. */
    private void loadImage(File file) {
        // load from file in a worker thread
        diskAccessThreadPool.execute(() -> {
            try {
                ProcessedBI image = new ProcessedBI(file);
                if (preprocessor != null)
                    image = image.process(preprocessor);
                setImage(image);
                rememberFile(file);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    fileHistory.remove(file.getCanonicalPath());
                } catch(IOException ignored) {}
            }
        });
    }
}
