package construction.builder;

import application.events.SaveStateEvent;
import construction.AssociationMoveContext;
import construction.properties.PropertiesData;
import construction.ComponentType;
import construction.properties.objectData.ObjectData;
import domain.Association;
import domain.Grid;
import domain.components.*;
import domain.geometry.Point;
import javafx.scene.shape.Rectangle;
import visualization.componentIcons.ComponentIcon;

import javax.print.attribute.standard.OrientationRequested;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GridBuilder {

    private Grid grid;
    private PropertiesData properties;

    // If user wants to trace back function information, set DEBUG to true
    private boolean DEBUG = false;

    // Additional variables so that the copy and drag functions can be implemented after components
    //      have been clicked on.
    private String copiedComponentID;
    private boolean isCopying;
    private ObjectData originalComponentData;

    // Variable that keeps track of whether the component is being dragged
    private boolean isDragging = false;
    private SaveStateEvent preDragState = null;

    // Variables to enable group components to keep track of their individual components
    private String peripherySourceComponent;
    private String peripheryDeviceComponent;
    // Possible to have more components, ideally these variables will be wiped after the complete
    //      placement of the group component.
    //      Ie: An XYZ is made up of a generator and a switch. When creating a new XYZ, store the
    //      generator as the sourceComponent,amd the switch as a deviceComponent. Then,


    // When copying components like the ATS, you want to make sure that the copy and drag functions
    //      are only targeting the data of the main component, the ATS. Not the periphery components,
    //      like the cutout and the connected load. Since COPY/DRAG can only work for single components,
    //      we choose which component's data we want to copy.
    private boolean isThisMainComponent = true;
    private List<String> attachedComponentIDs = new ArrayList<String>();


    public GridBuilder(Grid grid, PropertiesData properties) {
        this.grid = grid;
        this.properties = properties;
    }

    // This is what runs when a component is placed on the canvas standalone
    public boolean placeComponent(Point position, ComponentType componentType) {
        if (DEBUG) {
            System.out.println("Function: placeComponent, in src/construction/builder/GridBuilder\n");
        }
        if (isGroup(componentType)) {
            return placeGroup(position, componentType);
        }
        if (isDevice(componentType)) {
            return placeDevice(position, componentType);
        }
        else if (isSource(componentType)) {
            return placeSource(position, componentType);
        }
        return false;
    }

    public boolean placeGroup(Point position, ComponentType componentType) {

        if (componentType == componentType.ATS)

            return placeSource(position, componentType);
        else
            return false;
/*
            double rotation = 0;
            boolean cutOutPlacement = true;
            Point CutoutPoint = position.translate(-40, -80);

            if (this.properties.getRotation() != 0) {
                rotation = 90;
            }
            else
                rotation = 0;
            this.properties.setRotation(rotation);
            cutOutPlacement = placeDevice(CutoutPoint, componentType.CUTOUT);

            if (!cutOutPlacement) {
                return false;
            }


            boolean generatorPlacement = true;
            Point generatorPoint = position.translate(40, -20);
            if (rotation == 90) {
                rotation = 0;
                generatorPoint = position.translate(40, -20);
            }
            else
                rotation = 90;
            this.properties.setRotation(rotation);
            generatorPlacement = placeSource(generatorPoint, componentType.GENERATOR);

            this.properties.setRotation(0);
            if (!generatorPlacement) {
                return false;
            }

        }
        return true;
*/
    }



        // TODO: abstract conflictcomponent logic to it's own method to avoid duplicate code
    //  this is done in multiple places in this file.

    // PlaceDevice checks if the component is being copied, and if so, copies the
    //      data to the new placed device. Otherwise, it verifies placement and places
    //      components.

    public boolean placeDevice(Point position, ComponentType componentType) {

        Device device = createDevice(position, componentType);
        // If this is a periphery device, place the UUID into the variable, so that the
        //      main component can catch it in it's respective function.
        peripheryDeviceComponent = device.getId().toString();
        if (device == null) return false;
        device.setAngle(properties.getRotation());
        if (DEBUG) {
            System.out.println("placeDevice in GridBuilder: \n ");
            System.out.println("\n Copying:  " + isCopying);
        }
        checkIfComponentIsACopy(device);

        if(!verifyPlacement(device)) return false;
        // Return the data to the copied component

        Wire inWire = new Wire(position);
        Component conflictComponent = verifySingleWirePosition(inWire);

        if(conflictComponent == null) { // use new wire
            device.connectInWire(inWire);
            inWire.connect(device);
            grid.addComponent(inWire);
        }
        else if (conflictComponent instanceof Wire){
            inWire = (Wire) conflictComponent;
            device.connectInWire(inWire);
            inWire.connect(device);
        }
        else{
            conflictComponent.getComponentIcon().showError();
            return false;
        }


        Point outPoint;
        if (componentType == ComponentType.POLE) {
            outPoint = position.translate(0, 0);
        }
        else
        {
            outPoint = position.translate(0, device.getComponentIcon().getHeight());
        }

        Wire outWire = new Wire(outPoint.rotate(properties.getRotation(), position));

        conflictComponent = verifySingleWirePosition(outWire);
        if(conflictComponent == null) { // use new wire
            device.connectOutWire(outWire);
            outWire.connect(device);
            grid.addComponent(outWire);
        }
        else if (conflictComponent instanceof Wire){ // there is a wire conflict, connect this wire
            outWire = (Wire) conflictComponent;
            device.connectOutWire(outWire);
            outWire.connect(device);
        }
        else{
            conflictComponent.getComponentIcon().showError();
            return false;
        }

        grid.addComponent(device);

        return true;
    }

    public Device createDevice(Point point, ComponentType componentType) {
        return switch (componentType) {
            case TRANSFORMER -> new Transformer("", point);
            case POLE -> new Pole("", point);
            case BREAKER_12KV -> new Breaker("", point, Voltage.KV12, properties.getDefaultState(), null);
            case BREAKER_70KV -> new Breaker("", point, Voltage.KV70, properties.getDefaultState(), null);
            case JUMPER -> new Jumper("", point, properties.getDefaultState());
            case CUTOUT -> new Cutout("", point, properties.getDefaultState());
            case SWITCH -> new Switch("", point, properties.getDefaultState());
            default -> null;
        };
    }

    public boolean placeSource(Point position, ComponentType componentType) {

        switch (componentType) {
            case POWER_SOURCE -> {
                PowerSource powerSource = new PowerSource("", position, true);
                powerSource.setAngle(properties.getRotation());
                checkIfComponentIsACopy(powerSource);
                if(!verifyPlacement(powerSource)) return false;

                Wire outWire = new Wire(position);
                Component conflictComponent = verifySingleWirePosition(outWire);
                if(conflictComponent == null) { // use new wire
                    powerSource.connectWire(outWire);
                    outWire.connect(powerSource);
                    grid.addComponent(outWire);
                }
                else if (conflictComponent instanceof Wire){ // there is a wire conflict, connect this wire
                    outWire = (Wire) conflictComponent;
                    powerSource.connectWire(outWire);
                    outWire.connect(powerSource);
                }
                else{
                    conflictComponent.getComponentIcon().showError();
                    return false;
                }


                grid.addComponents(powerSource);
            }
            case TURBINE -> {
                Turbine turbine = new Turbine("", position, true);
                turbine.setAngle(properties.getRotation());
                checkIfComponentIsACopy(turbine);
                if(!verifyPlacement(turbine)) return false;

                Wire topWire = new Wire(position);
                Component conflictComponent = verifySingleWirePosition(topWire);
                if(conflictComponent == null) { // use new wire
                    turbine.connectTopOutput(topWire);
                    topWire.connect(turbine);
                    grid.addComponent(topWire);
                }
                else if (conflictComponent instanceof Wire){
                    topWire = (Wire) conflictComponent;
                    turbine.connectTopOutput(topWire);
                    topWire.connect(turbine);
                }
                else{
                    conflictComponent.getComponentIcon().showError();
                    return false;
                }

                Point bottomPoint = position.translate(0, turbine.getComponentIcon().getHeight())
                        .rotate(turbine.getAngle(), position);
                Wire bottomWire = new Wire(bottomPoint);
                conflictComponent = verifySingleWirePosition(bottomWire);
                if(conflictComponent == null) { // use new wire
                    turbine.connectBottomOutput(bottomWire);
                    bottomWire.connect(turbine);
                    grid.addComponent(bottomWire);
                }
                else if (conflictComponent instanceof Wire){ // there is a wire conflict, connect this wire
                    bottomWire = (Wire) conflictComponent;
                    turbine.connectBottomOutput(bottomWire);
                    bottomWire.connect(turbine);
                }
                else{
                    conflictComponent.getComponentIcon().showError();
                    return false;
                }

                grid.addComponents(turbine);
            }
            // Added by Ali to create an ATS system, which uses a generator


            // Added by Ali to create a single piece ATS, which acts as a powersource.
            //      The ATS will shift from main power to generator power when the sensorWire becomes
            //      de-energized. Otherwise, the sensorWire has no effect on the power output of the ATS.
            //      This means that the ATS will always be supplying power, with no option to turn off said power.
            case ATS -> {

                // declare the CUTOUT as a periphery component
                isThisMainComponent = false;
                placeDevice(position.translate(0,-60), componentType.CUTOUT);

                ATS ats = new ATS("", position, true);

                // Set the ATS's CutOutID to the newly created CutOut.
                ats.setAtsCutOutID(peripheryDeviceComponent);
                peripheryDeviceComponent = null;

                if (isDragging) {
                    if (!attachedComponentIDs.isEmpty()) {
                        for (int i = 0; i < attachedComponentIDs.size(); i++) {
                            grid.deleteSelectedItem(attachedComponentIDs.get(i));
                        }
                        attachedComponentIDs.clear();
                    }

                }

                ats.setAngle(properties.getRotation());

                // declare the ATS as the main component
                checkIfComponentIsACopy(ats);

                /*
                grid.deleteSelectedItem(ats.getATSCutOutID());
                ats.setAtsCutOutID(ats.getTempID());

                */

                if(!verifyPlacement(ats)) return false;
                if (DEBUG) {
                    System.out.println("Got past verify placement");
                }
                Wire outWire = new Wire(position.translate(0, 60));
                Component conflictComponent = verifySingleWirePosition(outWire);
                if(conflictComponent == null) { // use new wire
                    ats.connectWire(outWire);
                    outWire.connect(ats);
                    grid.addComponent(outWire);
                }
                else if (conflictComponent instanceof Wire){ // there is a wire conflict, connect this wire
                    outWire = (Wire) conflictComponent;
                    ats.connectWire(outWire);
                    outWire.connect(ats);
                }

                else{
                    conflictComponent.getComponentIcon().showError();
                    return false;
                }
/*
                Point sensorPoint = position.translate(0, 0);
                Wire sensorWire = new Wire(sensorPoint.rotate(properties.getRotation(), position));
                Wire tempWire = new Wire(sensorPoint.rotate(properties.getRotation(), position));

                conflictComponent = verifySingleWirePosition(sensorWire);

                if(conflictComponent == null) { // use new wire

                    grid.addComponent(sensorWire);
                }
                else if (conflictComponent instanceof Wire){
                    tempWire = (Wire) conflictComponent;
                    sensorWire.connect(tempWire);
                }
                else{
                    conflictComponent.getComponentIcon().showError();
                    return false;
                }
*/

                Wire inWire = new Wire(position);
                conflictComponent = verifySingleWirePosition(inWire);

                if(conflictComponent == null) { // use new wire
                    ats.setMainLineNode(inWire);
                   // inWire.connect(ats);
                    grid.addComponent(inWire);
                }
                else if (conflictComponent instanceof Wire){
                    inWire = (Wire) conflictComponent;
                    ats.setMainLineNode(inWire);
                   // inWire.connect(ats);
                }
                else{
                    conflictComponent.getComponentIcon().showError();
                    return false;
                }

               // ats.setMainLineNode(sensorWire);
                grid.addComponents(ats);
            }




        }
        return true;
    }

    private void checkIfComponentIsACopy (Component component) {

        if ((isCopying || isDragging) && isThisMainComponent) {
            component.applyComponentData(originalComponentData);
            if (component.getComponentType() == ComponentType.ATS) {
            }
        }

        isThisMainComponent = true;
    }


    public boolean placeWire(Point startPosition, Point endPosition, boolean shouldConnect) {
        Wire tempWire = new Wire(startPosition, endPosition);
        Wire wire = new Wire(getTrueStart(tempWire), getTrueEnd(tempWire));

        List<Component> wireConflicts = verifyWirePlacement(wire);

        if (wireConflicts == null) {
            return false; // there was a non wire conflict
        } else if (wireConflicts.size() == 0) {
            grid.addComponent(wire); // nothing conflicted
        } else {
            // TODO: connect if ctrl is pressed
            if(shouldConnect) {
                // wires overlapped, connect to them
                for(Component conflictingComponent : wireConflicts) {
                    if(conflictingComponent instanceof Wire)
                        ((Wire) conflictingComponent).connect(wire);
                    wire.connect(conflictingComponent);
                }
            }
            else {
                // Add wire with bridges
                ArrayList<Point> addBridgePoints = new ArrayList<>();
                ArrayList<Wire> connectComponents = new ArrayList<>();
                for(Component conflictingComponent : wireConflicts) {
                    if(conflictingComponent instanceof Wire) {
                        Point conflictPoint = getConflictPoint(wire, (Wire) conflictingComponent);
                        if(conflictPoint == null) {
                            return false;
                        }
                        else if(conflictPoint.equals(wire.getStart()) || conflictPoint.equals(wire.getEnd())) {
                            connectComponents.add((Wire) conflictingComponent);
                        }
                        else {
                            addBridgePoints.add(conflictPoint);
                        }
                    }
                }
                for(Point bridgePoint : addBridgePoints) {
                    wire.addBridgePoint(bridgePoint);
                }
                for(Wire connectComponent : connectComponents) {
                    wire.connect(connectComponent);
                    connectComponent.connect(wire);
                }
            }
            grid.addComponent(wire);

        }
        return true;
    }


    public static Point getConflictPoint(Wire wire1, Wire wire2) {
        
        boolean isWire1Vertical = wire1.isVerticalWire();
        boolean isWire1Point = wire1.isPointWire();

        boolean isWire2Vertical = wire2.isVerticalWire();
        boolean isWire2Point = wire2.isPointWire();

        if (isWire1Point && isWire2Point) {
            return null;
        }
        else if (isWire1Point) {
            return assertOverlappingConflicts(wire1, wire2);
        }
        else if (isWire2Point) {
            return new Point(wire2.getStart().getX(), wire2.getStart().getY());
        }
        else if (!isWire1Vertical && !isWire2Vertical) { // horizontal on horizontal
            return assertOverlappingConflicts(wire1, wire2);
        }
        else if (!isWire1Vertical && isWire2Vertical) { // horizontal on vertical
            return new Point(wire2.getStart().getX(), wire1.getStart().getY());
        }
        else if (isWire1Vertical && !isWire2Vertical) { // vertical on horizontal
            return new Point(wire1.getStart().getX(), wire2.getStart().getY());
        }
        else if (isWire1Vertical && isWire2Vertical) { // vertical on vertical
            return assertOverlappingConflicts(wire1, wire2);
        }
        else {
            return null;
        }
    }

    public static Point assertOverlappingConflicts(Wire wire1, Wire wire2) {
        if(wire1.getStart().equals(wire2.getEnd())) {
            return new Point(wire1.getStart().getX(), wire1.getStart().getY());
        }
        else if(wire1.getEnd().equals(wire2.getStart())) {
            return new Point(wire1.getEnd().getX(), wire1.getEnd().getY());
        }
        else {
            return null;
        }
    }

    public Point getTrueStart(Wire wire) {
        if(wire.getStart().getY() < wire.getEnd().getY() || wire.getStart().getX() < wire.getEnd().getX()) {
            return wire.getStart();
        }
        return wire.getEnd();
    }

    public Point getTrueEnd(Wire wire) {
        if(wire.getStart().getY() < wire.getEnd().getY() || wire.getStart().getX() < wire.getEnd().getX()) {
            return wire.getEnd();
        }
        return wire.getStart();
    }

    public List<Component> verifyWirePlacement(Component component) {
        // returns list of wires that conflict or null if a non-wire conflict occured.
        ArrayList<Component> wireConflicts = new ArrayList<>();
        int nonWireConflicts = 0;

        Rectangle currentComponentRect = component.getComponentIcon().getFittingRect();

        List<ComponentIcon> existingComponents = grid.getComponents().stream()
                .map(comp -> comp.getComponentIcon()).collect(Collectors.toList());

        for(ComponentIcon comp : existingComponents) {
            if (currentComponentRect.getBoundsInParent().intersects(comp.getFittingRect().getBoundsInParent())) {
                Component conflictingComponent = grid.getComponent(comp.getID());
                if(conflictingComponent instanceof Wire) {
                    wireConflicts.add(conflictingComponent);
                }
                else {
                    comp.showError();
                    nonWireConflicts = nonWireConflicts + 1;
                }
            }
        }
        if (nonWireConflicts > 0) return null;
        return wireConflicts;
    }

    public boolean verifyPlacement(Component component) {
        // returns true if placement is valid, false if placement is invalid
        int conflicts = 0;

        Rectangle currentComponentRect = component.getComponentIcon().getFittingRect();

        List<ComponentIcon> existingComponents = grid.getComponents().stream()
                .map(comp -> comp.getComponentIcon()).collect(Collectors.toList());

        for(ComponentIcon comp : existingComponents) {
            if (currentComponentRect.getBoundsInParent().intersects(comp.getFittingRect().getBoundsInParent())) {
                comp.showError();
                conflicts = conflicts + 1;
            }
        }
        if (DEBUG) {
            System.out.println("Conflicts found in src/construction/builder/GridBuilder/verifyPlacement: " + conflicts);
        }
        return conflicts == 0;
    }


    public Component verifySingleWirePosition(Component component) {
        Rectangle currentComponentRect = component.getComponentIcon().getFittingRect();

        List<ComponentIcon> existingComponents = grid.getComponents().stream()
                .map(comp -> comp.getComponentIcon()).collect(Collectors.toList());

        for(ComponentIcon compIcon : existingComponents) {
            if (currentComponentRect.getBoundsInParent().intersects(compIcon.getFittingRect().getBoundsInParent())) {
                Component conflictingComponent = grid.getComponent(compIcon.getID());
                return conflictingComponent;
            }
        }
        return null;
    }

    public void placeAssociation(Point start, Point end) {
        // determine topLeft point
        double x = Math.min(start.getX(), end.getX());
        double y = Math.min(start.getY(), end.getY());
        Point topLeft = new Point(x, y);

        // rectangle dimensions
        double width = start.differenceX(end);
        double height = start.differenceY(end);

        // create the association and add it to the grid
        Association association = new Association(topLeft, width, height, grid.countAssociations());
        grid.addAssociation(association);
    }

    public void resizeAssociation(AssociationMoveContext context, Point position) {
        Rectangle rect = context.target.getAssociationIcon().getRect();

    }

    public void toggleComponent(String componentId) {
        Component component = grid.getComponent(componentId);

        //only toggle if component is not locked
        boolean locked = component instanceof Closeable && ((Closeable) component).isLocked();
        locked = (component instanceof Source)? ((Source) component).isLocked() : locked;
        if(locked)
            return;

        if (component instanceof IToggleable) {
            if(component instanceof Breaker){
                Breaker breaker = (Breaker) component;

                // if the breaker has a tandem and it is going to be closed, lock it's tandem component.
                if(breaker.hasTandem() && !breaker.isClosed()) {
                    Component tandemComponent = grid.getComponent(breaker.getTandemID());
                    if(tandemComponent instanceof Breaker){
                        Breaker tandemBreaker = (Breaker) tandemComponent;
                        if(!tandemBreaker.isLocked()) {
                            lockComponent(breaker.getTandemID());
                        }
                    }
                }

                // if the breaker has a tandem and it is going to be opened, unlock it's tandem component.
                else if(breaker.hasTandem() && breaker.isClosed()) {
                    Component tandemComponent = grid.getComponent(breaker.getTandemID());
                    if(tandemComponent instanceof Breaker){
                        Breaker tandemBreaker = (Breaker) tandemComponent;
                        if(tandemBreaker.isLocked()) {
                            lockComponent(breaker.getTandemID());
                        }
                    }
                }
            }
            ((IToggleable) component).toggleState();
        }
    }

    public void lockComponent(String componentId) {
        Component component = grid.getComponent(componentId);

        if (component instanceof ILockable) {
            if(component instanceof Closeable) {
                if(((Closeable) component).isClosed()) {
                    toggleComponent(componentId);
                }
            }
            else if(component instanceof Source) {
                if(((Source) component).isOn()) {
                    toggleComponent(componentId);
                }
            }
            ((ILockable) component).toggleLockedState();
        } else {
            System.err.println("Component Not Lockable");
        }
    }


    // created by Ali to determine if a group placement is occuring

    private boolean isGroup(ComponentType componentType) {
        return switch (componentType) {
            case ATS -> true;
            default -> false;
        };
    }


    private boolean isDevice(ComponentType componentType) {
        return switch (componentType) {
            case BREAKER_12KV, BREAKER_70KV, CUTOUT, JUMPER, SWITCH, TRANSFORMER,POLE -> true;
            default -> false;
        };
    }

    private boolean isSource(ComponentType componentType) {
        return switch (componentType) {
            case POWER_SOURCE, TURBINE, ATS -> true;
            default -> false;
        };
    }

    public String getCopiedComponentID () {
        return copiedComponentID;
    }
    public boolean getIsCopying () {
        return isCopying;
    }
    public ObjectData getOriginalComponentData () {
         return originalComponentData;
    }
    public void setCopiedComponentID(String nameOfOriginalComponent) {
        copiedComponentID= nameOfOriginalComponent;
    }
    public void setIsCopying(boolean isComponentBeingCopied) {
        isCopying = isComponentBeingCopied;
    }

    public void setOriginalComponentData(ObjectData dataToBeCopied) {
        originalComponentData = dataToBeCopied;
    }

    public void addAttachedComponentIDs(String attachedComponentID) {
        attachedComponentIDs.add(attachedComponentID);
    }


    //Drag functions
    public void setIsDragging(boolean drag)
    {
        isDragging=drag;
    }
    public boolean getIsDragging()
    {
        return isDragging;
    }

    public void setPreDragSaveState(SaveStateEvent e)
    {
        preDragState = e;
    }
    public SaveStateEvent getPreDragSaveState()
    {
        return preDragState;
    }
}
