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
import domain.components.Wire;
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

    // To implement copying and pasting, the selection manager needs to be able to copy data and place a new component.
    //      To do that, SMC needs the functonality of the below controllers.
    //      Ghost controls visual placement, gridBuilder control placement of the component logically.

    private GhostManagerController ghostController;
    private GhostManager ghostModel;
    private GridBuilderController gridBuilderController;

    // This variable is to store the targetID for a single component, to be used in the copy-paste functions.
    private String targetIDForSingleComponent;


    // This boolean keeps track of the drag state.
    private boolean dragMoving = false;

    //this keeps the grid state before dragging
    //private SaveStateEvent preDragState = null;


    public SelectionManagerController(GridCanvasFacade canvasFacade, BuildMenuData buildMenuData,
                                      Grid grid, GridFlowEventManager gridFlowEventManager,
                                      PropertiesData propertiesData, GhostManagerController GMC,
                                      GridBuilderController GBC) {
        this.model = new SelectionManager(canvasFacade, grid);
        this.buildMenuData = buildMenuData;
        this.gridFlowEventManager = gridFlowEventManager;
        this.grid = grid;
        this.propertiesData = propertiesData;
        // Added to accomodate the new controllers
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
        if(targetIDForSingleComponent == ((Node)event.getTarget()).getId() && (targetIDForSingleComponent != null)) return;

        System.out.println("Function: StartSelectionEventHandler, in src/construction/selector/selectionManagerController\n");
        targetIDForSingleComponent = ((Node)event.getTarget()).getId();
        dragSelecting = true;
        dragMoving = false;
        model.beginSelection(event.getX(), event.getY());

        event.consume();
    };

    private final EventHandler<MouseEvent> expandSelectionEventHandler = event -> {
        if (!event.isPrimaryButtonDown()) return;
        if (buildMenuData.toolType != ToolType.SELECT) return;
        if(dragMoving)
        {
            dragSingleComponent();
            dragMoving = false;
        }
        if (!dragSelecting)
        {
            return;
        }
        model.expandSelection(event.getX(), event.getY());
        event.consume();
    };

    private final EventHandler<MouseEvent> endSelectionEventHandler = event -> {
        if (buildMenuData.toolType != ToolType.SELECT) return;
        if (!dragSelecting) return;

            dragSelecting = false;
        model.endSelection();
        if (this.model.getSelectedIDs().size() != 0) {
            targetIDForSingleComponent = this.model.getSelectedIDs().get(0);
            System.out.println(targetIDForSingleComponent);

        }
        event.consume();
    };



    private final EventHandler<MouseEvent> selectSingleComponentHandler = event -> {
        if (!event.isPrimaryButtonDown()) return;
        if (buildMenuData.toolType != ToolType.SELECT) return;


        System.out.println("Function: selectSingleComponentHandler, in src/construction/selector/selectionManagerController\n");


        String targetID = ((Node)event.getTarget()).getId();

        //Drag if clicking the same thing twice
        if(targetIDForSingleComponent != null) {
            String newId = ((Node) event.getTarget()).getId();
            //select same component again
            if(newId != null) {
                if (newId.equals(targetIDForSingleComponent)) {
                    dragSelecting = false;
                    dragMoving = true;
                    //dragSingleComponent();
                    return;
                }
            }
        }

        targetIDForSingleComponent = ((Node)event.getTarget()).getId();


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

    // Drags a single component. To use, click-and-hold on a single component and move mouse.
    public void dragSingleComponent()
    {
        System.out.println("Function: DragSingleComponent, in src/construction/selector/selectionManagerController\n");
        if (this.model.getSelectedIDs().size() == 1) {
            // In the case that an association was selected
            if (grid.getComponent(targetIDForSingleComponent) == null) {
                return;
            }
            else {
                // Find the specific component from the targetID
                Component comp = grid.getComponent(targetIDForSingleComponent);
                //Can't Drag Wires yet
                if(comp.getComponentType()==ComponentType.WIRE){
                    modelGrid.setDragWireBeginPoint(((Wire)comp).getStart());
                    buildMenuData.toolType = ToolType.WIRE;
                    return;
                }

//                SaveStateEvent e = new SaveStateEvent(grid.makeSnapshot());
//                modelGrid.setPreDragSaveState(e);
//                gridFlowEventManager.sendEvent(e);



                // Activate the ghost mode to place the new component
                buildMenuData.componentType = comp.getComponentType();

                buildMenuData.toolType = ToolType.PLACE;
                System.out.println("DRAG GHOST");
                ghostController.dragGhost();

                // Gather the component's data to be sent to the right class object
                String compName = comp.getName();
                ObjectData originalComponentData = comp.getComponentObjectData();

                // Set the details of the copied component in the modelGrid
                modelGrid.setCopiedComponentName(compName);
                modelGrid.setOriginalComponentData(originalComponentData);

                modelGrid.setIsDragging(true);

                SaveStateEvent e = new SaveStateEvent(grid.makeSnapshot());
                modelGrid.setPreDragSaveState(e);
                gridFlowEventManager.sendEvent(e);
                model.deleteSelectedItems();


            }
        }
    }

    // To activate this function, select a single item, and press CTRL + C
    public void copySingleComponent () {

        if (this.model.getSelectedIDs().size() == 1) {
            // In the case that an association was selected
            System.out.println("This is targetID :  \n");
            System.out.println(targetIDForSingleComponent);

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
