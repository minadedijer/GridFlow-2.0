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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ATS extends Source {

    private Wire outWire; // this is on the bottom of the source when oriented up


    //   To keep track of the components needed to create an ATS

    private Wire mainLineNode;
    private String atsCutOutID;
    private String connectedLoadID;

    // This is a precursor implementation to keep track of the various ID's associated
    //      with the component. If this implementation works, this list should be abstracted
    //      to all components, as all devices are connected to multiple components (wires, periphery, etc).

    private List<String> attachedComponentIDs = new ArrayList<String>();


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
        this.connectedLoadID = node.get("connectedLoadID").asText();
        //this.attachedComponentIDs = node.get("attachedComponentIDs").;

        createComponentIcon();
    }

    public ATS(ATSSnapshot snapshot) {
        super(UUID.fromString(snapshot.id), snapshot.name, snapshot.pos, snapshot.angle, snapshot.on);
        this.energized = snapshot.energized;
        this.mainLineNode = snapshot.mainLineNode;
        this.STATE = snapshot.STATE;
        this.connectedLoadID = snapshot.connectedLoadID;
        this.attachedComponentIDs = snapshot.attachedComponentIDs;
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
        ats.put("connectedLoadID", connectedLoadID);

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

        if (mainLineNode != null ) {
            if (mainLineNode.isEnergized() && STATE != POWEREDBYMAIN) {
                energized = true;
                STATE = POWEREDBYMAIN;
            }

            if (!mainLineNode.isEnergized() && STATE != POWEREDBYGENERATOR) {
                energized = false;
                STATE = POWEREDBYGENERATOR;
            }


        }
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
        return new ATSSnapshot(getId().toString(), getName(), getAngle(), getPosition(),
                isOn(), outWire.getId().toString(),
                STATE,  energized, mainLineNode.getId().toString(),
                mainLineNode, atsCutOutID, connectedLoadID, attachedComponentIDs);
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
    public void setConnectedLoadID (String connectedLoadID) {
        this.connectedLoadID = connectedLoadID;
    }

    public String getConnectedLoadID () {
        return this.connectedLoadID;
    }

    public void setAtsCutOutID(String cutOutID) {
        this.atsCutOutID = cutOutID;
    }

    public void addAttachedIDs(String attachedID) {
        this.attachedComponentIDs.add(attachedID);
    }

    public void clearAttachedIDs() {
        this.attachedComponentIDs.clear();
    }

    public List<String> getAttachedComponentIDs (){
        return attachedComponentIDs;
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
    String connectedLoadID;
    Wire mainLineNode;
    // the states which the ATS can be in
    int STATE = 0;
    boolean energized = true;
    List<String> attachedComponentIDs;





    public ATSSnapshot(String id, String name, double angle, Point pos, boolean on, String outWireID,
                       int STATE, boolean energized, String mainLineNodeID, Wire mainLineNode,
                       String atsCutOutID, String connectedLoadID, List<String> attachedComponentIDs) {
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
        this.connectedLoadID = connectedLoadID;
        this.attachedComponentIDs = attachedComponentIDs;

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
