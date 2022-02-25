package domain.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import construction.ComponentType;
import construction.history.ComponentMemento;
import domain.geometry.Point;
import visualization.componentIcons.ComponentIconCreator;
import visualization.componentIcons.SourceIcon;

import java.util.List;
import java.util.UUID;

public class ATS extends Source {

    private Wire outWire; // this is on the bottom of the source when oriented up


   //   Added by Ali
    private Generator generator;
    private Wire mainLineNode;
    private Wire generatorLineNode;
    private Wire outputLineNode;
    private boolean connectedToPower;
    private boolean changeTheState;
    // the states which the ATS can be in
    private int STATE = 0;
    private final int POWEREDBYGENERATOR = 2;
    private final int POWEREDBYMAIN = 1;

    public ATS(String name, Point position, boolean on) {
        super(name, position, on);
        createComponentIcon();
    }

    public ATS(JsonNode node) {
        super(UUID.fromString(node.get("id").asText()), node.get("name").asText(),
                Point.fromString(node.get("pos").asText()), node.get("angle").asDouble(),
                node.get("on").asBoolean());
        createComponentIcon();
    }

    public ATS(ATSSnapshot snapshot) {
        super(UUID.fromString(snapshot.id), snapshot.name, snapshot.pos, snapshot.angle, snapshot.on);
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
        setOn(!isOn());
        createComponentIcon();
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
    }

    @Override
    public ObjectNode getObjectNode(ObjectMapper mapper) {
        ObjectNode ats = super.getObjectNode(mapper);
        ats.put("outWire", outWire.getId().toString());
        return ats;
    }

    protected void createComponentIcon() {
        //changed name field to fix double name printing bug
        SourceIcon icon = ComponentIconCreator.getPowerSourceIcon(getPosition(), " ", isOn(), isLocked());
        icon.setSourceNodeEnergyState(isOn());
        icon.setWireEnergyState(false, 0);
        icon.setComponentIconID(getId().toString());
        icon.setAngle(getAngle(), getPosition());
        icon.setComponentName(getName(), true);
        setComponentIcon(icon);
    }

    @Override
    public ComponentType getComponentType() { return ComponentType.ATS; }

    @Override
    public void updateComponentIcon() {
        // Added by Ali to see if ATS can be dynamic

        if (mainLineNode != null ) {
            System.out.println(mainLineNode.isEnergized());


// go by checking the state, not the isenergized.
            // if changeTheState = false,
            //  check if its energized, if it is, toggle
            //  if it isnt energized, change the state = false;
            //
            if (mainLineNode.isEnergized() && STATE != POWEREDBYMAIN) {
                toggleState();
                STATE = POWEREDBYMAIN;
            }

            if (!mainLineNode.isEnergized() && STATE != POWEREDBYGENERATOR) {
                toggleState();
                STATE = POWEREDBYGENERATOR;
            }


        }

        SourceIcon icon = (SourceIcon) getComponentIcon();
        icon.setWireEnergyState(isOutWireEnergized(), 0);



    }

    @Override
    public void updateComponentIconName() {
        SourceIcon icon = (SourceIcon)getComponentIcon();
        icon.setComponentName(getName(), true);
    }

    @Override
    public ComponentMemento makeSnapshot() {
        return new ATSSnapshot(getId().toString(), getName(), getAngle(), getPosition(), isOn(), outWire.getId().toString());
    }

    @Override
    public void toggleLockedState() {
        toggleLocked(); // Changes the locked state in the parent class (closeable)
        createComponentIcon(); // Updates the component icon to show the new state
    }
    // Created by Ali, to allow the ATS to track its specific generator
    public void setGenerator( Generator generator) {
        this.generator = generator;
    }

    public Generator getGenerator () {
        return generator;
    }

    public Wire getMainLineNode() {
        return mainLineNode;
    }
    public Wire getOutputLineNode () {
        return outputLineNode;
    }
    public Wire getGeneratorLineNode () {
        return generatorLineNode;
    }

    public void setMainLineNode(Wire newMainLine) {
        this.mainLineNode = newMainLine;
    }

    public void setGeneratorLineNode(Wire newGeneratorLine) {
        this.generatorLineNode = newGeneratorLine;
    }
    public void setOutputLineNode(Wire newOutputLine) {
        this.outputLineNode = newOutputLine;
    }



}

class ATSSnapshot implements ComponentMemento {
    String id;
    String name;
    double angle;
    Point pos;
    boolean on;
    String outWireID;

    public ATSSnapshot(String id, String name, double angle, Point pos, boolean on, String outWireID) {
        this.id = id;
        this.name = name;
        this.angle = angle;
        this.pos = pos.copy();
        this.on = on;
        this.outWireID = outWireID;
    }

    @Override
    public Component getComponent() {
        return new ATS(this);
    }

    @Override
    public List<String> getConnectionIDs() {
        return List.of(outWireID);
    }
}
