package domain.components;

import com.fasterxml.jackson.databind.JsonNode;
import construction.history.ComponentMemento;
import domain.geometry.Point;
import visualization.componentIcons.ComponentIconCreator;
import visualization.componentIcons.DeviceIcon;

import java.util.List;
import java.util.UUID;

public class Cutout extends Closeable{

    public Cutout(String name, Point position, boolean closedByDefault) {
        super(name, position, closedByDefault);
        createComponentIcon();
    }

    public Cutout(CutoutSnapshot snapshot) {
        super(UUID.fromString(snapshot.id), snapshot.name, snapshot.pos, snapshot.angle, snapshot.closedByDefault, snapshot.closed);
        createComponentIcon();
    }

    public Cutout(JsonNode node) {
        super(UUID.fromString(node.get("id").asText()), node.get("name").asText(),
                Point.fromString(node.get("pos").asText()), node.get("angle").asDouble(),
                node.get("closedByDefault").asBoolean(), node.get("closed").asBoolean());
        createComponentIcon();
    }

    private void createComponentIcon() {
        DeviceIcon icon = ComponentIconCreator.getCutoutIcon(getPosition(), isClosed());
        icon.setDeviceEnergyStates(false, false);
        icon.setComponentIconID(getId().toString());
        icon.setComponentName(getName());
        icon.setAngle(getAngle(), getPosition());
        setComponentIcon(icon);
    }

    @Override
    public void updateComponentIcon() {
        DeviceIcon icon = (DeviceIcon) getComponentIcon();
        icon.setDeviceEnergyStates(isInWireEnergized(), isOutWireEnergized());
    }

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
        return new CutoutSnapshot(getId().toString(), getName(), getAngle(), getPosition(), isClosed(), isClosedByDefault(),
                getInWireID().toString(), getOutWireID().toString());
    }
}

class CutoutSnapshot implements ComponentMemento {
    String id;
    String name;
    double angle;
    Point pos;
    boolean closed;
    boolean closedByDefault;
    String outNodeId;
    String inNodeId;

    public CutoutSnapshot(String id, String name, double angle, Point pos, boolean closed, boolean closedByDefault, String inNodeId, String outNodeId) {
        this.id = id;
        this.name = name;
        this.angle = angle;
        this.pos = pos.copy();
        this.closed = closed;
        this.closedByDefault = closedByDefault;
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
    }

    public Cutout getComponent() {
        return new Cutout(this);
    }

    @Override
    public List<String> getConnectionIDs() {
        return List.of(inNodeId, outNodeId);
    }
}
