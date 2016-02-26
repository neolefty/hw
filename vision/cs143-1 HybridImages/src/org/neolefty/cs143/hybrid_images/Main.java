package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.neolefty.cs143.hybrid_images.util.ImageIOKit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private BufferedImageView imageView;
    private ImagePanel imagePanel;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("CS 143 - 1 - Hybrid Images");

//        SwingSetup.init(primaryStage);
//        frame.setVisible(true);
//        JPanel content = new JPanel(new BorderLayout());
//        setContentPane(content);

        BorderPane root = new BorderPane();

        FlowPane buttons = new FlowPane();
        buttons.setAlignment(Pos.CENTER);
        Button loadButton = new Button("Load");
        loadButton.setOnAction(value -> loadImage(primaryStage));
        buttons.getChildren().add(loadButton);
        root.setBottom(buttons);

//        imageView = new BufferedImageView();
//        root.setCenter(imageView);

        imagePanel = new ImagePanel();
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(imagePanel);
        root.setCenter(swingNode);

        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();

//        imagePanel = new ImagePanel();
//        content.add(imagePanel, BorderLayout.CENTER);

//        JPanel buttons = new JPanel();
//        content.add(buttons, BorderLayout.SOUTH);
//        JButton load = new JButton("Load Image");
//        buttons.add(load);
//        load.addActionListener(e -> loadImage());
    }

    private void loadImage(Window owner) {
        // JFileChooser
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(owner);
        if (file != null)
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
