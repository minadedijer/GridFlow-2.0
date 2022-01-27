package construction.selector;

import application.events.GridChangedEvent;
import application.events.GridFlowEventManager;
import application.events.PlacementFailedEvent;
import application.events.SaveStateEvent;
import construction.ComponentType;
import construction.buildMenu.BuildMenuData;
import construction.ToolType;
import construction.builder.GridBuilder;
import construction.builder.GridBuilderController;
import construction.canvas.GridCanvasFacade;
import construction.ghosts.GhostManager;
import construction.ghosts.GhostManagerController;
import construction.history.GridMemento;
import construction.properties.PropertiesData;
import construction.properties.objectData.ObjectData;
import construction.selector.observable.Observer;
import domain.Grid;
import domain.components.Component;
import domain.geometry.Point;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class SelectionManagerController {

    private SelectionManager model;
    private BuildMenuData buildMenuData;
    private GridFlowEventManager gridFlowEventManager;
    private boolean dragSelecting = false;
    private Grid grid;
    private PropertiesData propertiesData;
    private GridBuilder modelGrid;

    // Ali added this: Trying to give ghost capabilities and gridbuilding capabilities to selection manager.
    //      This is so that the user can select a single item, copy it, and place it somewhere else.

    private GhostManagerController ghostController;
    private GhostManager ghostModel;
    private GridBuilderController gridBuilderController;
    private String targetIDForSingleComponent;

    public SelectionManagerController(GridCanvasFacade canvasFacade, BuildMenuData buildMenuData,
                                      Grid grid, GridFlowEventManager gridFlowEventManager,
                                      PropertiesData propertiesData, GhostManagerController GMC,
                                      GridBuilderController GBC) {
        this.model = new SelectionManager(canvasFacade, grid);
        this.buildMenuData = buildMenuData;
        this.gridFlowEventManager = gridFlowEventManager;
        this.grid = grid;
        this.propertiesData = propertiesData;
        // Added by ali
        this.modelGrid = GBC.getModel();
        this.ghostController = GMC;
        this.ghostModel = ghostController.getGhostModel();
        this.gridBuilderController = GBC;
    }

    public void addSelectedIDObserver(Observer<String> observer) {
        model.addObserver(observer);
    }

    public void buildMenuDataChanged() {
        model.deSelectAll();
    }

    private final EventHandler<MouseEvent> startSelectionEventHandler = event -> {

        if (!event.isPrimaryButtonDown()) return;
        if (buildMenuData.toolType != ToolType.SELECT) return;
        System.out.println("Got to Start Selection\n\n");
        targetIDForSingleComponent = ((Node)event.getTarget()).getId();


        dragSelecting = true;
        model.beginSelection(event.getX(), event.getY());

        event.consume();
    };

    private final EventHandler<MouseEvent> expandSelectionEventHandler = event -> {
        if (!event.isPrimaryButtonDown()) return;
        if (buildMenuData.toolType != ToolType.SELECT) return;
        if (!dragSelecting) return;

        model.expandSelection(event.getX(), event.getY());
        event.consume();
    };

    private final EventHandler<MouseEvent> endSelectionEventHandler = event -> {
        if (buildMenuData.toolType != ToolType.SELECT) return;
        if (!dragSelecting) return;

        dragSelecting = false;
        model.endSelection();
        event.consume();
    };



    private final EventHandler<MouseEvent> selectSingleComponentHandler = event -> {
        if (!event.isPrimaryButtonDown()) return;
        if (buildMenuData.toolType != ToolType.SELECT) return;


        String targetID = ((Node)event.getTarget()).getId();


        if (event.isControlDown()) {
            model.continuousPointSelection(targetID);
        } else {
            model.pointSelection(targetID);
        }

        event.consume();
     };

    public void delete() {
        GridMemento preDeleteState = grid.makeSnapshot();
        int numDeleted = model.deleteSelectedItems();
        if (numDeleted != 0) {
            gridFlowEventManager.sendEvent(new SaveStateEvent(preDeleteState));
            gridFlowEventManager.sendEvent(new GridChangedEvent());
        }
    }

    // Added by Ali to copy a single component.
    // To activate this function, select a single item, and press CTRL + C
    public void copySingleComponent () {
        if (this.model.getSelectedIDs().size() == 1) {
            // In the case that an association was selected
            if (grid.getComponent(targetIDForSingleComponent) == null) {
                return;
            }
            else {
                // Find the specific component from the targetID
                Component comp = grid.getComponent(targetIDForSingleComponent);

                // Activate the ghost mode to place the new component
                buildMenuData.componentType = comp.getComponentType();
                buildMenuData.toolType = ToolType.PLACE;
                ghostController.buildMenuDataChanged();

                // Gather the component's data to be sent to the right class object
                String compName = comp.getName();
                ObjectData originalComponentData = comp.getComponentObjectData();

                // Set the details of the copied component in the modelGrid
                modelGrid.setCopiedComponentName(compName);
                modelGrid.setIsCopying(true);
                modelGrid.setOriginalComponentData(originalComponentData);

            }
        }
    }


    public void selectAll() {
        model.selectAll();
    }

    public EventHandler<MouseEvent> getStartSelectionEventHandler() {
        return startSelectionEventHandler;
    }

    public EventHandler<MouseEvent> getExpandSelectionEventHandler() {
        return expandSelectionEventHandler;
    }

    public EventHandler<MouseEvent> getEndSelectionEventHandler() {
        return endSelectionEventHandler;
    }

    public EventHandler<MouseEvent> getSelectSingleComponentHandler() {
        return selectSingleComponentHandler;
    }

    public SelectionManager getModel() {return model;}

}
