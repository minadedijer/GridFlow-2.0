package construction.canvas;

import application.Globals;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import visualization.componentIcons.ComponentIcon;

public class GridCanvas extends Pane {

    private final DoubleProperty scale = new SimpleDoubleProperty(1.0);
    private final EventHandler<MouseEvent> toggleComponentEventHandler;

    private final double unitWidth = 1000;
    private final double unitHeight = 500;

    private final Group components = new Group();
    private final Group energyOutlines = new Group();

    public GridCanvas(EventHandler<MouseEvent> toggleComponentEventHandler) {
        // event handlers
        this.toggleComponentEventHandler = toggleComponentEventHandler;

        getChildren().addAll(energyOutlines, components);
        addBackgroundGrid();

        setPrefSize(unitWidth * Globals.UNIT, unitHeight * Globals.UNIT);
        setStyle("-fx-background-color: white; -fx-border-color: blue;");

        scaleXProperty().bind(scale);
        scaleYProperty().bind(scale);
    }

    public double getScale() {
        return scale.get();
    }

    public void setScale(double scale) {
        this.scale.set(scale);
    }

    public void setPivot(double x, double y) {
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
    }

    public void addComponentIcon(ComponentIcon icon) {
        Group componentNode = icon.getComponentNode();
        Group energyOutlineNodes = icon.getEnergyOutlineNodes();
        componentNode.addEventHandler(MouseEvent.MOUSE_PRESSED, toggleComponentEventHandler);

        components.getChildren().add(componentNode);
        energyOutlines.getChildren().add(energyOutlineNodes);
    }

    public void clearComponents() {
        components.getChildren().clear();
        energyOutlines.getChildren().clear();
    }

    private void addBackgroundGrid() {
        Group backgroundGrid = new Group();
        double width = unitWidth * Globals.UNIT;
        double height = unitHeight * Globals.UNIT;
        // vertical lines
        for (int i = 0; i < width; i += Globals.UNIT) {
            Line gridLine = new Line(i, 0, i, height);
            gridLine.setStroke(Color.LIGHTGRAY);
            gridLine.setOpacity(0.5);
            backgroundGrid.getChildren().add(gridLine);
        }

        // horizontal lines
        for (int i = 0; i < height; i += Globals.UNIT) {
            Line gridLine = new Line(0, i, width, i);
            gridLine.setStroke(Color.LIGHTGRAY);
            gridLine.setOpacity(0.5);
            backgroundGrid.getChildren().add(gridLine);
        }
        getChildren().add(backgroundGrid);
        backgroundGrid.toBack();

    }
}