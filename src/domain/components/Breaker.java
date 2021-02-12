package domain.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.geometry.Point;
import visualization.componentIcons.BreakerIcon;
import visualization.componentIcons.ComponentIconCreator;
import visualization.componentIcons.DeviceIcon;

import java.util.UUID;

public class Breaker extends Closeable {

    private Voltage voltage;

    public Breaker(String name, Point position, Voltage voltage, boolean closedByDefault) {
        super(name, position, closedByDefault);
        this.voltage = voltage;
        createComponentIcon();
    }

    public Breaker(JsonNode node) {
        super(UUID.fromString(node.get("id").asText()), node.get("name").asText(),
                Point.fromString(node.get("pos").asText()), node.get("angle").asDouble(),
                node.get("closedByDefault").asBoolean(), node.get("closed").asBoolean());
        voltage = Voltage.valueOf(node.get("voltage").asText());
        createComponentIcon();
    }

    private void createComponentIcon() {
        BreakerIcon icon;
        if (voltage == Voltage.KV12) {
            icon = ComponentIconCreator.get12KVBreakerIcon(getPosition(), isClosed(), isClosedByDefault());
        } else {
            icon = ComponentIconCreator.get70KVBreakerIcon(getPosition(), isClosed(), isClosedByDefault());
        }
        icon.setComponentName(getName());
        icon.setBreakerEnergyStates(isInWireEnergized(), isOutWireEnergized(), isClosed());
        icon.setComponentIconID(getId().toString());
        icon.setAngle(getAngle(), getPosition());
        setComponentIcon(icon);
    }

    @Override
    public void updateComponentIcon() {
        BreakerIcon icon = (BreakerIcon)getComponentIcon();
        icon.setBreakerEnergyStates(isInWireEnergized(), isOutWireEnergized(), isClosed());
    }

    public Voltage getVoltage() {
        return voltage;
    }

    @Override
    public ObjectNode getObjectNode(ObjectMapper mapper) {
        ObjectNode breaker = super.getObjectNode(mapper);
        breaker.put("voltage", voltage.toString());
        return breaker;
    }

    

    @Override
    public void toggle() {
        toggleClosed();
        createComponentIcon();
    }
}
