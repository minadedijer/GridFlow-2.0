package domain.components;

import com.fasterxml.jackson.databind.JsonNode;
import construction.ComponentType;
import construction.history.ComponentMemento;
import domain.geometry.Point;
import visualization.componentIcons.ComponentIconCreator;
import visualization.componentIcons.DeviceIcon;

import java.util.List;
import java.util.UUID;

public class Pole extends Device {

    public Pole(String name, Point position) {
        super(name, position);
        createComponentIcon();
    }

    public Pole(JsonNode node) {
        super(UUID.fromString(node.get("id").asText()), node.get("name").asText(),
                Point.fromString(node.get("pos").asText()), node.get("angle").asDouble(),
                node.get("namepos").asBoolean());
        createComponentIcon();
    }

    public Pole(PoleSnapshot snapshot) {
        super(UUID.fromString(snapshot.id), snapshot.name, snapshot.pos, snapshot.angle, snapshot.namepos);
        createComponentIcon();
    }

    protected void createComponentIcon() {
        DeviceIcon icon = ComponentIconCreator.getPoleIcon(getPosition());
        icon.setDeviceEnergyStates(false, false);
        icon.setComponentIconID(getId().toString());
        icon.setAngle(-45, getPosition());
        icon.setComponentName(getName(), isNameRight());
        setComponentIcon(icon);
    }

    @Override
    public ComponentMemento makeSnapshot() {
        return new PoleSnapshot(getId().toString(), getName(), getAngle(), getPosition(), getInWireID().toString(), getOutWireID().toString(), isNameRight());
    }

    @Override
    public void updateComponentIcon() {
        DeviceIcon icon = (DeviceIcon)getComponentIcon();
        icon.setDeviceEnergyStates(isInWireEnergized(), isOutWireEnergized());
    }

    @Override
    public void updateComponentIconName() {
        DeviceIcon icon = (DeviceIcon)getComponentIcon();
        icon.setComponentName(getName(), isNameRight());
    }

    @Override
    public ComponentType getComponentType() { return ComponentType.POLE; }
}

class PoleSnapshot implements ComponentMemento {
    String id;
    String name;
    double angle;
    Point pos;
    String inNodeId;
    String outNodeId;
    boolean namepos;

    public PoleSnapshot(String id, String name, double angle, Point pos, String inNodeId, String outNodeId, boolean namepos) {
        this.id = id;
        this.name = name;
        this.angle = angle;
        this.pos = pos.copy();
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
        this.namepos = namepos;
    }

    @Override
    public Component getComponent() {
        return new Pole(this);
    }

    @Override
    public List<String> getConnectionIDs() {
        return List.of(inNodeId, outNodeId);
    }
}
