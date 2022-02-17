package domain.components;

import com.fasterxml.jackson.databind.JsonNode;
import construction.ComponentType;
import construction.history.ComponentMemento;
import domain.geometry.Point;
import visualization.componentIcons.ComponentIconCreator;
import visualization.componentIcons.DeviceIcon;

import java.util.List;
import java.util.UUID;

public class ATS extends Closeable {


    public ATS(String name, Point position, boolean closedByDefault) {
        super(name, position, closedByDefault);
        createComponentIcon();
    }

    public ATS(JsonNode node) {
        super(UUID.fromString(node.get("id").asText()), node.get("name").asText(),
                Point.fromString(node.get("pos").asText()), node.get("angle").asDouble(),
                node.get("closedByDefault").asBoolean(), node.get("closed").asBoolean(),
                node.get("locked").asBoolean(), node.get("namepos").asBoolean());
        createComponentIcon();
    }

    public ATS(ATSSnapshot snapshot) {
        super(UUID.fromString(snapshot.id), snapshot.name, snapshot.pos, snapshot.angle, snapshot.closedByDefault, snapshot.closed, snapshot.locked, snapshot.namepos);
        createComponentIcon();
    }

    protected void createComponentIcon() {
        DeviceIcon icon = ComponentIconCreator.getSwitchIcon(getPosition(), isClosed(), isClosedByDefault(), isLocked());
        icon.setDeviceEnergyStates(false, false);
        icon.setComponentIconID(getId().toString());
        icon.setAngle(getAngle(), getPosition());
        icon.setComponentName(getName(), isNameRight());
        setComponentIcon(icon);
    }

    @Override
    public void updateComponentIcon() {
        DeviceIcon icon = (DeviceIcon) getComponentIcon();
        icon.setDeviceEnergyStates(isInWireEnergized(), isOutWireEnergized());
    }

    @Override
    public void updateComponentIconName() {
        DeviceIcon icon = (DeviceIcon)getComponentIcon();
        icon.setComponentName(getName(), isNameRight());
    }

    @Override
    public ComponentType getComponentType() { return ComponentType.ATS; }

    @Override
    public void toggleState() {
        toggleClosed();
        createComponentIcon();
    }

    @Override
    public void toggleLockedState() {
        toggleLocked();
        createComponentIcon();
    }

    @Override
    public ComponentMemento makeSnapshot() {
        return new ATSSnapshot(getId().toString(), getName(), getAngle(), getPosition(), isClosed(), isClosedByDefault(), isLocked(), getInWireID().toString(), getOutWireID().toString(), isNameRight());
    }
}

class ATSSnapshot implements ComponentMemento {
    String id;
    String name;
    double angle;
    Point pos;
    boolean closed;
    boolean closedByDefault;
    boolean locked;
    String inNodeId;
    String outNodeId;
    boolean namepos;

    public ATSSnapshot(String id, String name, double angle, Point pos, boolean closed, boolean closedByDefault, boolean locked, String inNodeId, String outNodeId, boolean namepos) {
        this.id = id;
        this.name = name;
        this.angle = angle;
        this.pos = pos.copy();
        this.closed = closed;
        this.closedByDefault = closedByDefault;
        this.locked = locked;
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
        this.namepos = namepos;
    }

    @Override
    public Component getComponent() {
        return new ATS(this);
    }

    @Override
    public List<String> getConnectionIDs() {
        return List.of(inNodeId, outNodeId);
    }
}
