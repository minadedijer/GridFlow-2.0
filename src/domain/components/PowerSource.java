package domain.components;

import domain.geometry.Point;
import visualization.componentIcons.ComponentIcon;
import visualization.componentIcons.ComponentIconCreator;
import visualization.componentIcons.SourceIcon;

import java.util.List;
import java.util.UUID;

public class PowerSource extends Source {

    private Wire outWire; // this is on the bottom of the source when oriented up
    private SourceIcon icon;

    public PowerSource(String name, Point position, boolean on) {
        super(name, position, on);
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
    public void toggle() {
        setOn(!isOn());
        createComponentIcon();
    }

    @Override
    public void delete() {
        outWire.disconnect(getId());
    }

    @Override
    public List<Component> getConnections() {
        return List.of(outWire);
    }

    private void createComponentIcon() {
        SourceIcon icon = ComponentIconCreator.getPowerSourceIcon(getPosition(), getName(), isOn());
        icon.setSourceNodeEnergyState(isOn());
        icon.setWireEnergyState(false, 0);
        icon.setComponentIconID(getId().toString());
        icon.setAngle(getAngle(), getPosition());
        setComponentIcon(icon);
    }

    @Override
    public void updateComponentIcon() {
        SourceIcon icon = (SourceIcon) getComponentIcon();
        icon.setWireEnergyState(isOutWireEnergized(), 0);
    }
}
