package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.neolefty.cs143.hybrid_images.util.DecayHistory;
import org.neolefty.cs143.hybrid_images.util.ImageIOKit;
import org.neolefty.cs143.hybrid_images.util.SetupKit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private BufferedImageView imageView;
    private ImagePanel imagePanel;
    private DecayHistory<String> fileHistory;

    private static final String PREFS_FILE_HISTORY = "file_history";

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("CS143 #1 Hybrid Images");
        //noinspection unchecked
        fileHistory = (DecayHistory<String>) SetupKit.loadPref(getClass(), PREFS_FILE_HISTORY);
        if (fileHistory == null)
            fileHistory = new DecayHistory<>();

        FlowPane buttons = new FlowPane();
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        Button loadButton = new Button("Load");
        loadButton.setOnAction(value -> loadImageInteractive(primaryStage));
        buttons.getChildren().add(loadButton);

        imageView = new BufferedImageView();

        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        root.getChildren().add(buttons);
//        BorderPane root = new BorderPane();
//        root.setCenter(imageView);
//        root.setBottom(buttons);

        imageView.fitWidthProperty().bind(root.widthProperty());
        imageView.fitHeightProperty().bind(root.heightProperty());
//        imageView.fitHeightProperty().bind(root.heightProperty().subtract(buttons.heightProperty()));

        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();

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

    private void loadImageInteractive(Window owner) {
        File file = chooseFile(owner);
        if (file != null)
            setImageFile(file);
    }

    private File chooseFile(Window owner) {
        FileChooser chooser = new FileChooser();
        File topDir = topDir();
        if (topDir != null && topDir.exists())
            chooser.setInitialDirectory(topDir);
        File file = chooser.showOpenDialog(owner);
        try {
            fileHistory.add(file.getCanonicalPath());
            SetupKit.savePref(getClass(), PREFS_FILE_HISTORY, fileHistory);
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
        if (imagePanel != null)
            imagePanel.setImage(image);
    }
}
