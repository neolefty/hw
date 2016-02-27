package org.neolefty.cs143.hybrid_images.ui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.neolefty.cs143.hybrid_images.util.DecayHistory;
import org.neolefty.cs143.hybrid_images.util.ImageIOKit;
import org.neolefty.cs143.hybrid_images.util.SetupKit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** Loads a BufferedImage interactively. */
public class LoadImageView extends StackPane {
    private Class prefsClass;
    private String prefsFileHistoryKey;
    private BufferedImageView imageView;
    private DecayHistory<String> fileHistory;

    private static final String PREFS_FILE_HISTORY = "file_history";

    public LoadImageView(Class prefsClass, String prefsSuffix) {
//        setPrefHeight(100);
//        setPrefWidth(100);

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

        imageView = new BufferedImageView();
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());

        getChildren().add(imageView);
        getChildren().add(controls);

        // load the previous image by default
        Platform.runLater(() -> {
            File topFile = topFile();
            if (topFile != null && topFile.exists())
                setImageFile(topFile);
        });
    }

    public ObjectProperty<BufferedImage> bufferedImageProperty() {
        return imageView.bufferedImageProperty();
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
            setImageFile(file);
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

    private void setImageFile(File file) {
        try {
            BufferedImage image = ImageIOKit.loadImage(file);
            bufferedImageProperty().setValue(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
