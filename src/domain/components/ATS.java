package domain.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import construction.ComponentType;
import construction.history.ComponentMemento;
import construction.history.MementoType;
import domain.geometry.Point;
import visualization.componentIcons.ComponentIconCreator;
import visualization.componentIcons.SourceIcon;

import java.util.List;
import java.util.UUID;

public class ATS extends Source {

    private Wire outWire; // this is on the bottom of the source when oriented up


    //   To keep track of the components needed to create an ATS

    private Wire mainLineNode;
    private String atsCutOutID;


    // the states which the ATS can be in
    private int STATE = 0;
    private final int POWEREDBYGENERATOR = 2;
    private final int POWEREDBYMAIN = 1;
    private  boolean energized = false;

    public ATS(String name, Point position, boolean on) {
        super(name, position, on);
        createComponentIcon();
    }

    public ATS(JsonNode node) {
        super(UUID.fromString(node.get("id").asText()), node.get("name").asText(),
                Point.fromString(node.get("pos").asText()), node.get("angle").asDouble(),
                node.get("on").asBoolean());
        this.energized = node.get("energized").asBoolean();
        this.STATE = node.get("STATE").asInt();
        this.atsCutOutID = node.get("atsCutOutID").asText();
        createComponentIcon();
    }

    public ATS(ATSSnapshot snapshot) {
        super(UUID.fromString(snapshot.id), snapshot.name, snapshot.pos, snapshot.angle, snapshot.on);
        this.energized = snapshot.energized;
        this.mainLineNode = snapshot.mainLineNode;
        this.STATE = snapshot.STATE;
        createComponentIcon();
    }

    public void connectWire(Wire outWire) {
        this.outWire = outWire;
    }

    private boolean isOutWireEnergized() {
        if (outWire == null) return false;
        return outWire.isEnergized();
    }

    @Override
    public void toggleState() {
        return;
        //setOn(!isOn());
        //createComponentIcon();
    }

    @Override
    public void delete() {
        outWire.disconnect(getId());
    }

    @Override
    public List<Component> getConnections() {
        if (outWire == null) {
            return List.of();
        }
        return List.of(outWire);
    }

    @Override
    public void setConnections(List<Component> connections) {
        outWire = (Wire)connections.get(0);
        mainLineNode = (Wire) connections.get(1);
    }

    @Override
    public ObjectNode getObjectNode(ObjectMapper mapper) {
        ObjectNode ats = super.getObjectNode(mapper);
        ats.put("outWire", outWire.getId().toString());
        ats.put("mainLineNode", mainLineNode.getId().toString());
        ats.put("energized", energized);
        ats.put("STATE", STATE);
        ats.put("atsCutOutID", atsCutOutID);

        return ats;
    }

    public void createComponentIcon() {
        //changed name field to fix double name printing bug
        SourceIcon icon = ComponentIconCreator.getATSIcon(getPosition(),energized);
        icon.setSourceNodeEnergyState(isOn());
       // icon.setWireEnergyState(false, 0);
        icon.setWireEnergyState(true, 0);
        icon.setComponentIconID(getId().toString());
        icon.setAngle(getAngle(), getPosition());
        icon.setComponentName(getName(), isNameRight());
        setComponentIcon(icon);
    }

    @Override
    public ComponentType getComponentType() { return ComponentType.ATS; }

    @Override
    public void updateComponentIcon() {
        // Added by Ali to see if ATS can be dynamic
        if (mainLineNode == null) {
            System.out.println("GOT TO NULL\n");
        }

        if (mainLineNode != null ) {

// go by checking the state, not the isenergized.
            // if changeTheState = false,
            //  check if its energized, if it is, toggle
            //  if it isnt energized, change the state = false;
            //

            System.out.println("\nMainlineNode energized: " + mainLineNode.isEnergized());
            System.out.println(" STATE: " + STATE);
            System.out.println("Name of MainLineNode  " + mainLineNode.getId() + "\n");

            if (mainLineNode.isEnergized() && STATE != POWEREDBYMAIN) {


                energized = true;
                STATE = POWEREDBYMAIN;
            }

            if (!mainLineNode.isEnergized() && STATE != POWEREDBYGENERATOR) {

                energized = false;
                STATE = POWEREDBYGENERATOR;
            }


        }
        System.out.println("This is energized:  " + energized);
        SourceIcon icon = (SourceIcon) getComponentIcon();
        createComponentIcon();
        icon.setWireEnergyState(isOutWireEnergized(), 0);



    }


    @Override
    public void updateComponentIconName() {
        SourceIcon icon = (SourceIcon)getComponentIcon();
        icon.setComponentName(getName(), true);
    }

    @Override
    public ComponentMemento makeSnapshot() {
        return new ATSSnapshot(getId().toString(), getName(), getAngle(), getPosition(), isOn(), outWire.getId().toString(),
                STATE,  energized, mainLineNode.getId().toString(), mainLineNode, atsCutOutID);
    }

    @Override
    public void toggleLockedState() {
        return;

        //toggleLocked(); // Changes the locked state in the parent class (closeable)
        //createComponentIcon(); // Updates the component icon to show the new state
    }

    public Wire getMainLineNode() {
        return mainLineNode;
    }

    public void setMainLineNode(Wire newMainLine) {
        this.mainLineNode = newMainLine;
    }

    public String getATSCutOutID() {
        return atsCutOutID;
    }

    public void setAtsCutOutID(String cutOutID) {
        this.atsCutOutID = cutOutID;
    }



}

class ATSSnapshot implements ComponentMemento {
    String id;
    String name;
    double angle;
    Point pos;
    boolean on;
    String outWireID;
    String mainLineNodeID;
    String atsCutOutID;
    Wire mainLineNode;
    // the states which the ATS can be in
    int STATE = 0;
    boolean energized = true;






    public ATSSnapshot(String id, String name, double angle, Point pos, boolean on, String outWireID,
                       int STATE, boolean energized, String mainLineNodeID, Wire mainLineNode,
                       String atsCutOutID) {
        this.id = id;
        this.name = name;
        this.angle = angle;
        this.pos = pos.copy();
        this.on = on;
        this.outWireID = outWireID;
        this.STATE = STATE;
        this.energized = energized;
        this.mainLineNodeID = mainLineNodeID;
        this.mainLineNode = mainLineNode;
        this.atsCutOutID = atsCutOutID;

    }

    @Override
    public Component getComponent() {
        return new ATS(this);
    }

    @Override
    public List<String> getConnectionIDs() {
        return List.of(outWireID, mainLineNodeID);
    }
}
