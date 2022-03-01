package domain.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import construction.ComponentType;
import construction.history.ComponentMemento;
import construction.properties.objectData.ComponentData;
import construction.properties.objectData.ObjectData;
import domain.geometry.Point;
import visualization.componentIcons.CLTIcon;
import visualization.componentIcons.ComponentIconCreator;
import visualization.componentIcons.DeviceIcon;

import java.util.List;
import java.util.UUID;

public class ConnectedLoadText extends Device {

    private String buildings = "Bldg(s)";
    private String transformerSize = "Transformer Size";
    private String warnings = "No Warnings";

    public ConnectedLoadText(String name, Point position) {
        super(name, position);
        createComponentIcon();
    }

    public ConnectedLoadText(JsonNode node) {
        super(UUID.fromString(node.get("id").asText()), node.get("name").asText(),
                Point.fromString(node.get("pos").asText()), node.get("angle").asDouble(),
                node.get("namepos").asBoolean());
        createComponentIcon();
    }

    public ConnectedLoadText(ConnectedLoadTextSnapshot snapshot) {
        super(UUID.fromString(snapshot.id), snapshot.name, snapshot.pos, snapshot.angle, snapshot.namepos);
        createComponentIcon();
    }

    protected void createComponentIcon() {
        //DeviceIcon icon = ComponentIconCreator.getConnectedLoadTextIcon(getPosition());
        CLTIcon icon = ComponentIconCreator.getConnectedLoadTextIcon(getPosition());

        icon.setDeviceEnergyStates(false, false);
        icon.setComponentIconID(getId().toString());
        icon.setAngle(0, getPosition());
        icon.setComponentName(getName(), isNameRight());
        icon.setBuildingText(getBuildings(), false);
        icon.setTransformerText(getTransformerSize(), false);
        icon.setWarningText(getWarnings(), false);

        setComponentIcon(icon);
    }

    @Override
    public ObjectData getComponentObjectData() {
        return new ComponentData(getName(), isNameRight(), getAngle(), getTransformerSize(), getBuildings(), getWarnings());
    }

//    @Override
//    public ObjectData getComponentObjectData() {
//        return new ComponentData(getName(), isNameRight(), getAngle(), transformerSize, buildings, warnings);
//    }


    @Override
    public ComponentMemento makeSnapshot() {
        return new ConnectedLoadTextSnapshot(getId().toString(), getName(), getAngle(), getPosition(), getInWireID().toString(), getOutWireID().toString(), isNameRight());
    }

    @Override
    public void updateComponentIcon() {
        CLTIcon icon = (CLTIcon)getComponentIcon();
        icon.setDeviceEnergyStates(isInWireEnergized(), isOutWireEnergized());
    }

    @Override
    public void updateComponentIconName() {
        CLTIcon icon = (CLTIcon)getComponentIcon();
        icon.setComponentName(getName(), isNameRight());
        icon.setTransformerText(getTransformerSize(), false);
        icon.setBuildingText(getBuildings(), false);
        icon.setWarningText(getWarnings(), false);

    }

    @Override
    public void applyComponentData(ObjectData objectData) {
        ComponentData data = (ComponentData) objectData;
        if (true) {
            System.out.println("Change the data in the CLT! "+data.getTransformerSize());
            setName(data.getName());
            setNameRight(data.isNamePos());
            setBuildings(data.getBuildings());
            setTransformerSize(data.getTransformerSize());
            setWarnings(data.getWarnings());
            updateComponentIconName();
        }
    }

    @Override
    public ComponentType getComponentType() { return ComponentType.CONNECTED_LOAD_TEXT; }

    public String getBuildings() {
        return buildings;
    }

    public void setBuildings(String buildings) {
        this.buildings = buildings;
    }

    public String getTransformerSize() {
        return transformerSize;
    }

    public void setTransformerSize(String transformerSize) {
        this.transformerSize = transformerSize;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }
}

class ConnectedLoadTextSnapshot implements ComponentMemento {
    String id;
    String name;
    double angle;
    Point pos;
    String inNodeId;
    String outNodeId;
    boolean namepos;

    public ConnectedLoadTextSnapshot(String id, String name, double angle, Point pos, String inNodeId, String outNodeId, boolean namepos) {
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
        return new ConnectedLoadText(this);
    }

    @Override
    public List<String> getConnectionIDs() {
        return List.of(inNodeId, outNodeId);
    }
}
