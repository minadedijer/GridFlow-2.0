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

public class Generator extends Source {

    private Wire outWire; // this is on the bottom of the source when oriented up

    public Generator(String name, Point position, boolean on) {
        super(name, position, on);
        createComponentIcon();
    }

    public Generator(JsonNode node) {
        super(UUID.fromString(node.get("id").asText()), node.get("name").asText(),
                Point.fromString(node.get("pos").asText()), node.get("angle").asDouble(),
                node.get("on").asBoolean());
        createComponentIcon();
    }

    public Generator(GeneratorSnapshot snapshot) {
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
        ObjectNode Generator = super.getObjectNode(mapper);
        Generator.put("outWire", outWire.getId().toString());
        return Generator;
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
    public ComponentType getComponentType() { return ComponentType.POWER_SOURCE; }

    @Override
    public void updateComponentIcon() {
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
        return new GeneratorSnapshot(getId().toString(), getName(), getAngle(), getPosition(), isOn(), outWire.getId().toString());
    }

    @Override
    public void toggleLockedState() {
        toggleLocked(); // Changes the locked state in the parent class (closeable)
        createComponentIcon(); // Updates the component icon to show the new state
    }
}

class GeneratorSnapshot implements ComponentMemento {
    String id;
    String name;
    double angle;
    Point pos;
    boolean on;
    String outWireID;

    public GeneratorSnapshot(String id, String name, double angle, Point pos, boolean on, String outWireID) {
        this.id = id;
        this.name = name;
        this.angle = angle;
        this.pos = pos.copy();
        this.on = on;
        this.outWireID = outWireID;
    }

    @Override
    public Component getComponent() {
        return new Generator(this);
    }

    @Override
    public List<String> getConnectionIDs() {
        return List.of(outWireID);
    }
}
