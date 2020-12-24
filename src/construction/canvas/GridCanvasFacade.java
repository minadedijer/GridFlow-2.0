package construction.canvas;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import visualization.componentIcons.ComponentIcon;
import visualization.componentIcons.WireIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GridCanvasFacade {

    // this is the master's baby. it adds nodes to and removes nodes from the canvas.
    private GridCanvas canvas;

    private final List<ComponentIcon> componentIcons = new ArrayList<>();

    // Component Event Handlers
    private EventHandler<MouseEvent> toggleComponentEventHandler;
    private EventHandler<MouseEvent> enterComponentHoverEventHandler;
    private EventHandler<MouseEvent> exitComponentHoverEventHandler;
    private EventHandler<MouseEvent> selectSingleComponentHandler;

    public GridCanvasFacade() {
        createCanvas();
    }

    private void createCanvas() {
        canvas = new GridCanvas();
        canvas.setTranslateX(-5350); // get this from application settings?
        canvas.setTranslateY(-2650);

        // canvas events
        SceneGestures sceneGestures = new SceneGestures(canvas);

        // panning and scrolling
        canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getBeginPanEventHandler());
        canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnPanEventHandler());
        canvas.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
    }

    public void addComponentIcon(ComponentIcon icon) {
        Group componentNode = icon.getComponentNode();
        Group energyOutlineNodes = icon.getEnergyOutlineNodes();
        Rectangle boundingRect = icon.getBoundingRect();
        boundingRect.addEventHandler(MouseEvent.MOUSE_PRESSED, toggleComponentEventHandler);
        boundingRect.addEventHandler(MouseEvent.MOUSE_PRESSED, selectSingleComponentHandler);
        if (!(icon instanceof WireIcon)) {
            boundingRect.setOnMouseEntered(enterComponentHoverEventHandler);
            boundingRect.setOnMouseExited(exitComponentHoverEventHandler);
        }

        canvas.componentGroup.getChildren().add(componentNode);
        canvas.energyOutlineGroup.getChildren().add(energyOutlineNodes);
        canvas.boundingRectGroup.getChildren().add(boundingRect);
        componentIcons.add(icon);
    }

    public void addOverlayNode(Node overlayNode) {
        canvas.overlayGroup.getChildren().add(overlayNode);
    }

    public void clearOverlay() {
        canvas.overlayGroup.getChildren().clear();
    }

    public void clearComponentGroups() {
        componentIcons.clear();
        canvas.componentGroup.getChildren().clear();
        canvas.energyOutlineGroup.getChildren().clear();
        canvas.boundingRectGroup.getChildren().clear();
    }

    public void setCanvasCursor(Cursor cursor) {
        canvas.setCursor(cursor);
    }

    public void addCanvasEventHandler(EventType eventType, EventHandler eventHandler) {
        canvas.addEventHandler(eventType, eventHandler);
    }

    public void addCanvasEventFilter(EventType eventType, EventHandler eventHandler) {
        canvas.addEventFilter(eventType, eventHandler);
    }

    public void selectComponent(String ID) {
        componentIcons.forEach(icon -> {
            if (icon.getID() == ID) {
                icon.select();
            }
        });
    }

    public void deSelectAll() {
        componentIcons.forEach(icon -> {
            icon.deSelect();
        });
    }

    public List<javafx.scene.shape.Rectangle> getAllBoundingRects() {
        return componentIcons.stream().map(icon -> icon.getBoundingRect()).collect(Collectors.toList());
    }

    public List<javafx.scene.shape.Rectangle> getNonWireBoundingRects() {
        return componentIcons.stream().filter(icon -> !(icon instanceof WireIcon)).map(icon -> icon.getBoundingRect()).collect(Collectors.toList());
    }

    public GridCanvas getCanvas() {
        return canvas;
    }

    public void setToggleComponentEventHandler(EventHandler<MouseEvent> eventHandler) {
        this.toggleComponentEventHandler = eventHandler;
    }

    public void setEnterComponentHoverEventHandler(EventHandler<MouseEvent> eventHandler) {
        this.enterComponentHoverEventHandler = eventHandler;
    }

    public void setExitComponentHoverEventHandler(EventHandler<MouseEvent> eventHandler) {
        this.exitComponentHoverEventHandler = eventHandler;
    }

    public void setSelectSingleComponentHandler(EventHandler<MouseEvent> selectSingleComponentHandler) {
        this.selectSingleComponentHandler = selectSingleComponentHandler;
    }
}
