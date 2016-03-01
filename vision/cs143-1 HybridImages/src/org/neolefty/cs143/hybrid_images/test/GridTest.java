package org.neolefty.cs143.hybrid_images.test;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.neolefty.cs143.hybrid_images.test.ui.CirclePane;
import org.neolefty.cs143.hybrid_images.ui.util.PersistentScene;
import org.neolefty.cs143.hybrid_images.test.ui.StretchOval;
import org.neolefty.cs143.hybrid_images.ui.util.StrictGrid;

public class GridTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        int x = 0x123456;
        int y = ((x & 0xffff) << 8) + ((x & 0xff0000) >> 16);
        System.out.println("x = " + Integer.toHexString(x));
        System.out.println("y = " + Integer.toHexString(y));

        primaryStage.setTitle("Test Grid");
        GridPane outer = new StrictGrid();
        outer.add(new StretchOval(), 0, 0);
        outer.add(new Button("Upper Right"), 1, 0);
        outer.add(new Label("Lower Left"), 0, 1);
        outer.add(new CirclePane(200, 200, Color.WHEAT), 1, 1);
        primaryStage.setScene(new PersistentScene(getClass(), outer, 600, 600));
        primaryStage.show();
    }
}
