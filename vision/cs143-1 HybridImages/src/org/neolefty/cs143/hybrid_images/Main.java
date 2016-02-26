package org.neolefty.cs143.hybrid_images;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String PREFS_FILE_HISTORY = "file_history";

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("CS143 #1 Hybrid Images");

        LoadImageView left = new LoadImageView(getClass(), PREFS_FILE_HISTORY + "_left");
        LoadImageView right = new LoadImageView(getClass(), PREFS_FILE_HISTORY + "_right");

        HBox outer = new HBox(left, right);
//        box.setFillHeight(true);

//        StackPane outer = new StackPane();
//        left.setAlignment(Pos.TOP_LEFT);
//        right.setAlignment(Pos.TOP_RIGHT);
        outer.widthProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("width: " + oldValue + " -> " + newValue);
            double x = newValue.doubleValue() / 2;
//            left.setPrefWidth(x);
//            left.setMaxWidth(x);
            left.setMinWidth(x);
//            right.setPrefWidth(x);
//            right.setMaxWidth(x);
            right.setMinWidth(x);
        });
        outer.heightProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("height: " + oldValue + " -> " + newValue);
//            left.setPrefHeight(newValue.doubleValue());
//            left.setMaxHeight(newValue.doubleValue());
            left.setMinHeight(newValue.doubleValue());
//            right.setPrefHeight(newValue.doubleValue());
//            right.setMaxHeight(newValue.doubleValue());
            right.setMinHeight(newValue.doubleValue());
        });
//        outer.getChildren().add(left);
//        outer.getChildren().add(right);


//        GridPane grid = new GridPane();
//
//        RowConstraints row = new RowConstraints();
//        row.setPercentHeight(100);
//        row.setVgrow(Priority.ALWAYS);
//        row.setPrefHeight(10);
//        grid.getRowConstraints().add(row);
//
//        ColumnConstraints col = new ColumnConstraints();
//        col.setPercentWidth(50);
//        grid.getColumnConstraints().addAll(col, col);
//
////        grid.add(new LoadImageView(getClass(), PREFS_FILE_HISTORY + "_left"), 0, 0);
////        grid.add(new LoadImageView(getClass(), PREFS_FILE_HISTORY + "_right"), 1, 0);
//        grid.addRow(0, left, right);
////        grid.setFillHeight(left, true);

        primaryStage.setScene(new Scene(outer, 600, 250));
        primaryStage.show();
    }
}
