package org.neolefty.cs143.hybrid_images;

import javafx.application.Platform;
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
    private String prefsKey;
    private BufferedImageView imageView;
    private DecayHistory<String> fileHistory;

    public LoadImageView(Class prefsClass, String prefsKey) {
        this.prefsClass = prefsClass;
        this.prefsKey = prefsKey;
        //noinspection unchecked
        fileHistory = (DecayHistory<String>) SetupKit.loadPref(prefsClass, prefsKey);
        if (fileHistory == null)
            fileHistory = new DecayHistory<>();

        FlowPane controls = new FlowPane();
        controls.setAlignment(Pos.BOTTOM_CENTER);
        Button chooseButton = new Button("Choose ...");
        chooseButton.setOnAction(value -> loadImageInteractive());
        controls.getChildren().add(chooseButton);

        imageView = new BufferedImageView();
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
        try {
            fileHistory.add(file.getCanonicalPath());
            SetupKit.savePref(prefsClass, prefsKey, fileHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void setImageFile(File file) {
        try {
            BufferedImage image = ImageIOKit.loadImage(file);
            setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setImage(BufferedImage image) {
        if (imageView != null)
            imageView.setImage(image);
    }
}
