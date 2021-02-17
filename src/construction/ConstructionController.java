package construction;

import application.events.GridFlowEventManager;
import construction.builder.GridBuilderController;
import construction.canvas.GridCanvasFacade;
import construction.ghosts.GhostManagerController;
import construction.history.GridHistorianController;
import construction.selector.SelectionManagerController;
import domain.Grid;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ConstructionController {

    private GridCanvasFacade canvasFacade = null;
    private GridFlowEventManager gridFlowEventManager;
    private Stage stage;

    // Sub Controllers
    private GridBuilderController gridBuilderController;
    private GhostManagerController ghostManagerController;
    private SelectionManagerController selectionManagerController;
    private GridHistorianController gridHistorianController;

    // UI Data
    private BuildMenuData buildMenuData;
    private PropertiesData propertiesData;

    // Wire Placing and Association Placing
    // Used to share if a double click is in progress, and where the first click was if so
    private DoubleClickPlacementContext doubleClickContext = new DoubleClickPlacementContext();

    public ConstructionController(Grid grid, GridFlowEventManager gridFlowEventManager, Stage stage) {
        // shared objects
        this.gridFlowEventManager = gridFlowEventManager;
        this.canvasFacade = new GridCanvasFacade();
        this.buildMenuData = new BuildMenuData();
        this.propertiesData = new PropertiesData();
        this.stage = stage;

        // controllers
        gridBuilderController = new GridBuilderController(grid, gridFlowEventManager, doubleClickContext, buildMenuData,
                propertiesData, canvasFacade);
        ghostManagerController = new GhostManagerController(canvasFacade, doubleClickContext, buildMenuData, propertiesData);
        selectionManagerController = new SelectionManagerController(canvasFacade, buildMenuData, grid, gridFlowEventManager);
        gridHistorianController = new GridHistorianController(grid, gridFlowEventManager);
        gridFlowEventManager.addListener(gridHistorianController);
        gridFlowEventManager.addListener(ghostManagerController);

        setPropertiesData(0, true);
        setBuildMenuData(ToolType.INTERACT, null);

        installEventHandlers();
    }

    public GridCanvasFacade getCanvasFacade() {
        return canvasFacade;
    }

    public void setBuildMenuData(ToolType toolType, ComponentType componentType) {
        if (toolType != null) buildMenuData.toolType = toolType;
        if (componentType != null) buildMenuData.componentType = componentType;

        // default state becomes closed when tool switches
        setPropertiesData(propertiesData.getRotation(), true);

        // these run if the controllers need to react to build data changing
        ghostManagerController.buildMenuDataChanged();
        selectionManagerController.buildMenuDataChanged();
        gridBuilderController.buildDataChanged();
    }

    public void setPropertiesData(double rotation, boolean defaultState) {
        boolean rotationChanged = rotation != propertiesData.getRotation();
        boolean defaultStateChanged = defaultState != propertiesData.getDefaultState();

        propertiesData.setRotation(rotation);
        propertiesData.setDefaultState(defaultState);

        gridBuilderController.propertiesDataChanged();
        ghostManagerController.propertiesDataChanged(rotationChanged, defaultStateChanged);
    }

    private final EventHandler<KeyEvent> handleRKeyRotation = event -> {
        if (event.getCode() != KeyCode.R) return;
        rotate(event.isControlDown());
        event.consume();
    };

    private final EventHandler<MouseEvent> handleMiddleMouseRotation = event -> {
        if (!event.isMiddleButtonDown()) return;
        rotate(false);
        event.consume();
    };

    private final EventHandler<KeyEvent> handleToggleDefaultState = event -> {
        if (event.getCode() != KeyCode.E) return;
        if (buildMenuData.toolType != ToolType.PLACE) return;
        setPropertiesData(propertiesData.getRotation(), !propertiesData.getDefaultState());
        event.consume();
    };

    private final EventHandler<KeyEvent> handleEscapeTool = event -> {
        if (event.getCode() != KeyCode.ESCAPE) return;
        if (buildMenuData.toolType == ToolType.INTERACT) return;
        setBuildMenuData(ToolType.INTERACT, buildMenuData.componentType);
        event.consume();
    };

    // association event handlers
    // eats clicks on the association, to prevent associations from being placed inside the association
    private final EventHandler<MouseEvent> consumeAssociationClicksHandler = event -> {
        if (buildMenuData.toolType != ToolType.ASSOCIATION) return;
        event.consume();
    };

    private void rotate(boolean ccw) {
        double rotation;

        if (ccw) {
            rotation = (propertiesData.getRotation() == 0) ? 270 : propertiesData.getRotation() - 90;
        } else {
            rotation = (propertiesData.getRotation() == 270) ? 0 : propertiesData.getRotation() + 90;
        }

        setPropertiesData(rotation, propertiesData.getDefaultState());
    }

    private void installEventHandlers() {
        // gets event handlers from the sub controllers and installs them into the canvasFacade
        // event handlers are what respond to user inputs

        // construction controller events
        stage.addEventFilter(KeyEvent.KEY_PRESSED, handleRKeyRotation);
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, handleMiddleMouseRotation);
        stage.addEventFilter(KeyEvent.KEY_PRESSED, handleToggleDefaultState);
        stage.addEventFilter(KeyEvent.KEY_PRESSED, handleEscapeTool);

        // undo/redo events
        stage.addEventFilter(KeyEvent.KEY_PRESSED, gridHistorianController.getUndoRedoEventHandler());

        // builder events
        canvasFacade.setToggleComponentEventHandler(gridBuilderController.getToggleComponentEventHandler());
        canvasFacade.addCanvasEventHandler(MouseEvent.MOUSE_PRESSED, gridBuilderController.getPlaceComponentEventHandler());
        canvasFacade.addCanvasEventFilter(MouseEvent.MOUSE_PRESSED, gridBuilderController.getPlaceWireEventHandler());
        canvasFacade.addCanvasEventHandler(MouseEvent.MOUSE_PRESSED, gridBuilderController.getPlaceAssociationEventHandler());

        // ghost manager events
        canvasFacade.addCanvasEventFilter(MouseEvent.MOUSE_MOVED, ghostManagerController.getGhostMoveEventHandler());

        // selection events
        canvasFacade.addCanvasEventHandler(MouseEvent.MOUSE_PRESSED, selectionManagerController.getStartSelectionEventHandler());
        canvasFacade.addCanvasEventHandler(MouseEvent.MOUSE_DRAGGED, selectionManagerController.getExpandSelectionEventHandler());
        canvasFacade.addCanvasEventHandler(MouseEvent.MOUSE_RELEASED, selectionManagerController.getEndSelectionEventHandler());
        canvasFacade.setSelectSingleComponentHandler(selectionManagerController.getSelectSingleComponentHandler());
        stage.addEventFilter(KeyEvent.KEY_PRESSED, selectionManagerController.getDeleteHandler());

        // association events
        canvasFacade.setConsumeAssociationClicksHandler(consumeAssociationClicksHandler);

        // disables ghost when hovering over association
        canvasFacade.setBeginHoverAssociationHandler(ghostManagerController.getBeginHoverAssociationHandler());
        canvasFacade.setEndHoverAssociationHandler(ghostManagerController.getEndHoverAssociationHandler());

        // move text when dragging the text and show new cursor
        canvasFacade.setBeginAssociationTextDragHandler(gridBuilderController.getBeginAssociationTextDragEventHandler());
        canvasFacade.setDragAssociationTextHandler(gridBuilderController.getDragAssociationTextEventHandler());
        canvasFacade.setShowMoveCursorOnTextHoverHandler(gridBuilderController.getShowMoveCursorOnTextHoverHandler());
        canvasFacade.setShowDefaultCursorOnLeaveTextHoverHandler(gridBuilderController.getShowDefaultCursorOnLeaveTextHoverHandler());

        // resize association when dragging handles
        canvasFacade.setBeginResizeAssociationHandler(gridBuilderController.getBeginResizeAssociationEventHandler());
        canvasFacade.setResizeAssociationNWHandler(gridBuilderController.getResizeAssociationNWHandler());
        canvasFacade.setResizeAssociationSEHandler(gridBuilderController.getResizeAssociationSEHandler());
        canvasFacade.setResizeAssociationNEHandler(gridBuilderController.getResizeAssociationNEHandler());
        canvasFacade.setResizeAssociationSWHandler(gridBuilderController.getResizeAssociationSWHandler());
    }
}
