package visualization.componentIcons;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
import model.components.Breaker;
import model.components.PowerSource;
import model.components.Switch;
import model.components.Voltage;
import model.geometry.Point;
import visualization.GridScene;

public class ComponentIconCreator {

    public static DeviceIcon getSwitchIcon(Switch switchComponent) {
        DeviceIcon switchIcon = new DeviceIcon();
        Point p = switchComponent.getPosition();

        // base shape
        Line inLine = createLine(p, p.translate(0, 1.25 * GridScene.UNIT));
        Line inBar = createLine(p.translate(-0.5 * GridScene.UNIT, 1.25 * GridScene.UNIT),
                p.translate(0.5 * GridScene.UNIT, 1.25 * GridScene.UNIT));
        switchIcon.addInNodeShapes(inLine, inBar);

        Line outLine = createLine(p.translate(0, 1.75 * GridScene.UNIT),
                p.translate(0, 3 * GridScene.UNIT));
        Line outBar = createLine(p.translate(-0.5 * GridScene.UNIT, 1.75 * GridScene.UNIT),
                p.translate(0.5 * GridScene.UNIT, 1.75 * GridScene.UNIT));
        switchIcon.addOutNodeShapes(outLine, outBar);

        // State indicators
        if (switchComponent.isClosedByDefault()) {
            if (switchComponent.isClosed()){
                Line closedBar = createLine(p.translate(0.5 * GridScene.UNIT, GridScene.UNIT),
                        p.translate(-0.5 * GridScene.UNIT, 2 * GridScene.UNIT));
                switchIcon.addMidNodeShapes(closedBar);
            }
            else {
                Circle openCircle = createCircle(p.translate(0, 1.5 * GridScene.UNIT), 0.25 * GridScene.UNIT,
                        Color.TRANSPARENT, Color.LIMEGREEN);
                switchIcon.addStaticNodes(openCircle);
            }
        }
        else {
            if (switchComponent.isClosed()) {
                Line closedBar1 = createLine(p.translate(0.5 * GridScene.UNIT, GridScene.UNIT),
                        p.translate(-0.5 * GridScene.UNIT, 2 * GridScene.UNIT));
                closedBar1.setStroke(Color.RED);
                Line closedBar2 = createLine(p.translate(-0.5 * GridScene.UNIT, GridScene.UNIT),
                        p.translate(0.5 * GridScene.UNIT, 2 * GridScene.UNIT));
                closedBar2.setStroke(Color.RED);
                switchIcon.addStaticNodes(closedBar1, closedBar2);
            }
        }

        return switchIcon;
    }

    public static DeviceIcon getBreakerIcon(Breaker breaker) {
        if (breaker.getVoltage() == Voltage.KV12) {
            return get12KVBreakerIcon(breaker);
        } else {
            return get70KVBreakerIcon(breaker);
        }
    }

    private static DeviceIcon get70KVBreakerIcon(Breaker breaker) {
        DeviceIcon breakerIcon = new DeviceIcon();
        Point p = breaker.getPosition();
        Line inLine = createLine(p, p.translate(0, 1 * GridScene.UNIT));
        breakerIcon.addInNodeShapes(inLine);

        Line outLine = createLine(p.translate(0, 2 * GridScene.UNIT), p.translate(0, 3 * GridScene.UNIT));
        breakerIcon.addOutNodeShapes(outLine);

        Rectangle box = createRectangle(p.translate(-0.5 * GridScene.UNIT, 1 * GridScene.UNIT),
                p.translate(0.5 * GridScene.UNIT, 2 * GridScene.UNIT), Color.RED, Color.BLACK);
        breakerIcon.addMidNodeShapes(box);

        if (breaker.isClosedByDefault()) {
            if (!breaker.isClosed()) {
                box.setFill(Color.LIME);
                Node textBox = createTextBox("N/C", p.translate(-3.5 * GridScene.UNIT, GridScene.UNIT),
                        p.translate(-2.5 * GridScene.UNIT, 2 * GridScene.UNIT));
                breakerIcon.addStaticNodes(textBox);
            }
        }
        else {
            if (breaker.isClosed()) {
                // Draw N/O text
            }
            else {
                box.setFill(Color.LIME);
            }
        }

        breakerIcon.setBoundingRect(p, 2, 3);
        return breakerIcon;
    }

    private static DeviceIcon get12KVBreakerIcon(Breaker breaker) {
        DeviceIcon breakerIcon = new DeviceIcon();
        Point p = breaker.getPosition();

        Line inLine1 = createLine(p, p.translate(0, 0.75 * GridScene.UNIT));
        Line inLine2 = createRoundedLine(p.translate(0, GridScene.UNIT), p.translate(0, 1.5 * GridScene.UNIT));
        Line inChevron1L = createRoundedLine(p.translate(-0.5 * GridScene.UNIT, GridScene.UNIT), p.translate(0, 0.75 * GridScene.UNIT));
        Line inChevron1R = createRoundedLine(p.translate(0.5 * GridScene.UNIT, GridScene.UNIT), p.translate(0, 0.75 * GridScene.UNIT));
        Line inChevron2L = createRoundedLine(p.translate(-0.5 * GridScene.UNIT, 1.25 * GridScene.UNIT), p.translate(0, GridScene.UNIT));
        Line inChevron2R = createRoundedLine(p.translate(0.5 * GridScene.UNIT, 1.25 * GridScene.UNIT), p.translate(0, GridScene.UNIT));
        breakerIcon.addInNodeShapes(inLine1, inLine2, inChevron1L, inChevron1R, inChevron2L, inChevron2R);

        Line outLine1 = createRoundedLine(p.translate(0, 2.5 * GridScene.UNIT), p.translate(0, 3 * GridScene.UNIT));
        Line outLine2 = createLine(p.translate(0, 3.25 * GridScene.UNIT), p.translate(0, 4 * GridScene.UNIT));
        Line outChevron1L = createRoundedLine(p.translate(-0.5 * GridScene.UNIT, 2.75 * GridScene.UNIT),
                p.translate(0, 3 * GridScene.UNIT));
        Line outChevron1R = createRoundedLine(p.translate(0.5 * GridScene.UNIT, 2.75 * GridScene.UNIT),
                p.translate(0, 3 * GridScene.UNIT));
        Line outChevron2L = createRoundedLine(p.translate(-0.5 * GridScene.UNIT, 3 * GridScene.UNIT),
                p.translate(0, 3.25 * GridScene.UNIT));
        Line outChevron2R = createRoundedLine(p.translate(0.5 * GridScene.UNIT, 3 * GridScene.UNIT),
                p.translate(0, 3.25 * GridScene.UNIT));
        breakerIcon.addOutNodeShapes(outLine1, outLine2, outChevron1L, outChevron1R, outChevron2L, outChevron2R);

        Rectangle box = createRectangle(p.translate(-0.5 * GridScene.UNIT, 1.5 * GridScene.UNIT),
                p.translate(0.5 * GridScene.UNIT, 2.5 * GridScene.UNIT), Color.RED, Color.BLACK);
        breakerIcon.addMidNodeShapes(box);

        if (!breaker.isClosed()) box.setFill(Color.LIME);

        breakerIcon.setBoundingRect(p, 2, 4);
        return breakerIcon;
    }

    public static DeviceIcon getTransformerIcon(Point p) {

        // change to new icon that can't be split energy maybe
        DeviceIcon transformerIcon = new DeviceIcon();

        Line inLine = createLine(p, p.translate(0, 1.1 * GridScene.UNIT));
        Line inEdgeL = createLine(p.translate(-1 * GridScene.UNIT, 0.9 * GridScene.UNIT),
                p.translate(-1 * GridScene.UNIT, 1.1 * GridScene.UNIT));
        Line inEdgeR = createLine(p.translate(1 * GridScene.UNIT, 0.9 * GridScene.UNIT),
                p.translate(1 * GridScene.UNIT, 1.1 * GridScene.UNIT));
        Arc arcIn1 = createHalfArc(p.translate(-0.75 * GridScene.UNIT, 1.1 * GridScene.UNIT), 0.25 * GridScene.UNIT, Orientation.DOWN);
        Arc arcIn2 = createHalfArc(p.translate(-0.25 * GridScene.UNIT, 1.1 * GridScene.UNIT), 0.25 * GridScene.UNIT, Orientation.DOWN);
        Arc arcIn3 = createHalfArc(p.translate(0.25 * GridScene.UNIT, 1.1 * GridScene.UNIT), 0.25 * GridScene.UNIT, Orientation.DOWN);
        Arc arcIn4 = createHalfArc(p.translate(0.75 * GridScene.UNIT, 1.1 * GridScene.UNIT), 0.25 * GridScene.UNIT, Orientation.DOWN);
        transformerIcon.addInNodeShapes(inLine, inEdgeL, inEdgeR, arcIn1, arcIn2, arcIn3, arcIn4);

        Line outLine = createLine(p.translate(0, 1.9 * GridScene.UNIT), p.translate(0, 3 * GridScene.UNIT));
        Line outEdgeL = createLine(p.translate(-1 * GridScene.UNIT, 1.9 * GridScene.UNIT),
                p.translate(-1 * GridScene.UNIT, 2.1 * GridScene.UNIT));
        Line outEdgeR = createLine(p.translate(1 * GridScene.UNIT, 1.9 * GridScene.UNIT),
                p.translate(1 * GridScene.UNIT, 2.1 * GridScene.UNIT));
        Arc arcOut1 = createHalfArc(p.translate(-0.75 * GridScene.UNIT, 1.9 * GridScene.UNIT), 0.25 * GridScene.UNIT, Orientation.UP);
        Arc arcOut2 = createHalfArc(p.translate(-0.25 * GridScene.UNIT, 1.9 * GridScene.UNIT), 0.25 * GridScene.UNIT, Orientation.UP);
        Arc arcOut3 = createHalfArc(p.translate(0.25 * GridScene.UNIT, 1.9 * GridScene.UNIT), 0.25 * GridScene.UNIT, Orientation.UP);
        Arc arcOut4 = createHalfArc(p.translate(0.75 * GridScene.UNIT, 1.9 * GridScene.UNIT), 0.25 * GridScene.UNIT, Orientation.UP);
        transformerIcon.addOutNodeShapes(outLine, outEdgeL, outEdgeR, arcOut1, arcOut2, arcOut3, arcOut4);

        return transformerIcon;
    }

    public static DeviceIcon getJumperIcon(Point p, boolean closed) {
        DeviceIcon jumperIcon = new DeviceIcon();

        Line inLine = createLine(p, p.translate(0, GridScene.UNIT));
        jumperIcon.addInNodeShapes(inLine);

        Line outLine = createLine(p.translate(0, 2 * GridScene.UNIT), p.translate(0, 3 * GridScene.UNIT));
        Arc jumper = createHalfArc(p.translate(0, 1.5 * GridScene.UNIT), 0.5 * GridScene.UNIT, Orientation.RIGHT);
        // transforms must be applied prior to adding the node
        if (!closed) rotateNode(jumper, p.translate(0, 2 * GridScene.UNIT), 45);

        jumperIcon.addOutNodeShapes(outLine, jumper);

        return jumperIcon;
    }

    public static DeviceIcon getCutoutIcon(Point p, boolean closed) {
        DeviceIcon cutoutIcon = new DeviceIcon();

        Line inLine = createLine(p, p.translate(0, .95 * GridScene.UNIT));
        cutoutIcon.addInNodeShapes(inLine);

        Line outLine = createLine(p.translate(0, 2 * GridScene.UNIT), p.translate(0, 3 * GridScene.UNIT));
        // these shapes get rotated together
        Arc cutoutArc = createHalfArc(p.translate(0, 1.125 * GridScene.UNIT), 0.15 * GridScene.UNIT, Orientation.UP);
        Circle cutoutDot = createCircle(p.translate(0, 1.125 * GridScene.UNIT), 0.5, Color.TRANSPARENT, Color.BLACK);
        Line cutoutLineL = createRoundedLine(p.translate(0, 2 * GridScene.UNIT), p.translate(-0.15 * GridScene.UNIT, 1.125 * GridScene.UNIT));
        Line cutoutLineR = createRoundedLine(p.translate(0, 2 * GridScene.UNIT), p.translate(0.15 * GridScene.UNIT, 1.125 * GridScene.UNIT));

        // rotate shapes
        if (!closed) {
            Point pivot = p.translate(0, 2 * GridScene.UNIT);
            double angle = 135;

            rotateNode(cutoutArc, pivot, angle);
            rotateNode(cutoutLineL, pivot, angle);
            rotateNode(cutoutLineR, pivot, angle);
            rotateNode(cutoutDot, pivot, angle);
        }
        cutoutIcon.addOutNodeShapes(outLine, cutoutArc, cutoutDot, cutoutLineL, cutoutLineR);

        return cutoutIcon;
    }

    public static SourceIcon getPowerSourceIcon(PowerSource source) {
        SourceIcon powerSourceIcon = new SourceIcon();
        Point p = source.getPosition();

        Rectangle sourceBox = createRectangle(p.translate(-GridScene.UNIT, 0),
                p.translate(GridScene.UNIT, 2 * GridScene.UNIT), Color.RED, Color.BLACK);
        powerSourceIcon.addSourceNodeShapes(sourceBox);

        Line outLine = createLine(p.translate(0, 2 * GridScene.UNIT), p.translate(0, 3 * GridScene.UNIT));
        powerSourceIcon.addOutputLine(outLine);

        if (!source.isOn()) sourceBox.setFill(Color.LIME);

        return powerSourceIcon;
    }

    public static SourceIcon getTurbineIcon(Point p) {
        SourceIcon turbineIcon = new SourceIcon();

        Circle turbineCircle = createCircle(p.translate(0, 2 * GridScene.UNIT), GridScene.UNIT, Color.RED, Color.BLACK);
        turbineIcon.addSourceNodeShapes(turbineCircle);

        Line outLine1 = createLine(p, p.translate(0, GridScene.UNIT));
        turbineIcon.addOutputLine(outLine1);

        Line outLine2 = createLine(p.translate(0, 3 * GridScene.UNIT), p.translate(0, 4 * GridScene.UNIT));
        turbineIcon.addOutputLine(outLine2);

        return turbineIcon;
    }

    public static WireIcon getWireIcon(Point p1, Point p2) {
        WireIcon wireIcon = new WireIcon();

        if (p1.equals(p2)) {
            Circle wireDot = createCircle(p1, 1, Color.BLACK, Color.BLACK);
            wireIcon.addWireShape(wireDot);
        } else {
            Line wireLine = createLine(p1, p2);
            wireIcon.addWireShape(wireLine);
        }

        return wireIcon;
    }

    private static Line createLine(Point p1, Point p2) {
        Line line = new Line();
        line.setStrokeWidth(GridScene.STROKE_WIDTH);

        line.setStartX(p1.getX());
        line.setStartY(p1.getY());

        line.setEndX(p2.getX());
        line.setEndY(p2.getY());

        return line;
    }

    private static Line createRoundedLine(Point p1, Point p2) {
        Line line = createLine(p1, p2);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        return line;
    }

    private static Rectangle createRectangle(Point p1, Point p2, Color fill, Color stroke) {
        Rectangle rectangle = new Rectangle();
        rectangle.setStrokeWidth(GridScene.STROKE_WIDTH);
        rectangle.setStrokeType(StrokeType.CENTERED);
        rectangle.setStroke(stroke);
        rectangle.setFill(fill);

        rectangle.setX(p1.getX());
        rectangle.setY(p1.getY());

        rectangle.setWidth(p1.differenceX(p2));
        rectangle.setHeight(p1.differenceY(p2));

        return rectangle;
    }

    private static Arc createHalfArc(Point center, double radius, Orientation orientation) {
        Arc arc = new Arc();
        arc.setStrokeWidth(GridScene.STROKE_WIDTH);
        arc.setStroke(Color.BLACK);
        arc.setFill(Color.TRANSPARENT);
        arc.setType(ArcType.OPEN);
        arc.setStrokeType(StrokeType.CENTERED);

        arc.setCenterX(center.getX());
        arc.setCenterY(center.getY());
        arc.setRadiusX(radius);
        arc.setRadiusY(radius);
        arc.setLength(180);
        arc.setStartAngle(getArcStartAngle(orientation));

        return arc;
    }

    private static double getArcStartAngle(Orientation orientation) {
        switch (orientation) {
            case UP:
                return 0;
            case DOWN:
                return 180;
            case LEFT:
                return 90;
            case RIGHT:
                return 270;
        }
        return 0;
    }

    private static Circle createCircle(Point center, double radius, Color fill, Color stroke) {
        Circle circle = new Circle();
        circle.setStroke(stroke);
        circle.setStrokeWidth(GridScene.STROKE_WIDTH);
        circle.setFill(fill);

        circle.setCenterX(center.getX());
        circle.setCenterY(center.getY());
        circle.setRadius(radius);

        return circle;
    }

    private static void rotateNode(Node node, Point pivot, double angle) {
        Rotate rotateTransform = new Rotate();
        rotateTransform.setPivotX(pivot.getX());
        rotateTransform.setPivotY(pivot.getY());
        rotateTransform.setAngle(angle);

        node.getTransforms().add(rotateTransform);
    }

    public static Node createTextBox(String string, Point corner1, Point corner2) {
        Rectangle rect = createRectangle(corner1, corner2, Color.TRANSPARENT, Color.BLUE);
        Text text = new Text(string);
        text.setFill(Color.RED);
        text.setBoundsType(TextBoundsType.VISUAL);

        StackPane layout = new StackPane(rect, text);
        layout.setPadding(new Insets(20));
        return layout;
    }
}

enum Orientation {
    UP, DOWN, LEFT, RIGHT
}