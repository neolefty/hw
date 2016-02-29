package org.neolefty.cs143.hybrid_images.ui.util;

import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

import java.util.List;

/** A GridPane that reminds children to shrink to fit. */
public class StrictGrid extends GridPane {
    public StrictGrid() {
        super();
        widthProperty().addListener((observable, oldValue, newValue) -> {
            //noinspection Convert2streamapi
            for (Node node : getChildren()) {
                if (node instanceof Region)
                    ((Region) node).setMinWidth(getPrefWidth(node));
            }
        });
        heightProperty().addListener((observable, oldValue, newValue) -> {
            //noinspection Convert2streamapi
            for (Node node : getChildren()) {
                if (node instanceof Region)
                    ((Region) node).setMinHeight(getPrefHeight(node));
            }
        });
    }

    private double getPrefWidth(Node child) {
        int n = getColumnIndex(child);
        List<ColumnConstraints> constraintsList = getColumnConstraints();
        if (constraintsList.size() > n)
            return constraintsList.get(n).getPrefWidth();
        else
            return getWidth() * colSpanNoNull(child) / getColCount();
    }

    private double getPrefHeight(Node child) {
        int n = getColumnIndex(child);
        List<RowConstraints> constraintsList = getRowConstraints();
        if (constraintsList.size() > n)
            return constraintsList.get(n).getPrefHeight();
        else
            return getHeight() * rowSpanNoNull(child) / getRowCount();
    }

    private int getColCount() {
        int result = 0;
        for (Node child : getChildren())
            if (getColumnIndex(child) != null)
                result = Math.max(getColumnIndex(child), result);
        return result + 1;
    }

    private int getRowCount() {
        int result = 0;
        for (Node child : getChildren())
            if (getRowIndex(child) != null)
                result = Math.max(getRowIndex(child), result);
        return result + 1;
    }

    private int colSpanNoNull(Node child) {
        Integer result = getColumnSpan(child);
        return result == null ? 1 : result;
    }

    private int rowSpanNoNull(Node child) {
        Integer result = getRowSpan(child);
        return result == null ? 1 : result;
    }
}
