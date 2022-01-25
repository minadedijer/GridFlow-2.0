package visualization.componentIcons;
import application.Globals;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import domain.geometry.Point;

import java.util.List;

public class PoleIcon extends ComponentIcon {
    private Group energyOutlines = new Group();

    public PoleIcon() {
        addEnergyOutlineNode(energyOutlines);
    }

    public void addPoleShape(Shape poleShape) {
        Shape energyOutline = ShapeCopier.copyShape(poleShape);
        energyOutline.setStrokeWidth(Globals.ENERGY_STROKE_WIDTH);
        energyOutline.setStroke(Color.YELLOW);
        energyOutline.setFill(Color.TRANSPARENT);

        addNodesToIconNode(poleShape);
        addShapesToEnergyOutlineNode(energyOutlines, poleShape);
    }

    public void setPoleIconEnergyState(boolean energized) {
        energyOutlines.getChildren().forEach(child -> child.setOpacity(energized ? 1: 0));
    }

    @Override
    public void setBoundingRect(Dimensions dimensions, Point position) {
        dimensionsToPoleRectangle(getBoundingRect(), dimensions, position);
    }

    @Override
    public void setFittingRect(Dimensions dimensions, Point position) {
        dimensionsToPoleRectangle(getFittingRect(), dimensions, position);
    }

    private void dimensionsToPoleRectangle(Rectangle rect, Dimensions dimensions, Point position) {
        Point topLeft = position.translate(-dimensions.getAdjustedWidth()/2, -dimensions.getAdjustedHeight()/2);
        rect.setX(topLeft.getX());
        rect.setY(topLeft.getY());
        rect.setWidth(dimensions.getAdjustedWidth());
        rect.setHeight(dimensions.getAdjustedHeight());
    }
}
