package domain.components;

import domain.geometry.*;
import visualization.GridVisualizer;
import visualization.componentIcons.ComponentIcon;
import visualization.componentIcons.ComponentIconCreator;
import visualization.componentIcons.WireIcon;

import java.util.ArrayList;
import java.util.List;

public class Wire extends Component {

    private List<Component> connections;
    private Point start;
    private Point end;
    private boolean energized;

    public Wire(String name, Point p1, Point p2) {
        super(name, Point.midpoint(p1, p2));
        this.connections = new ArrayList<>();
        start = p1;
        end = p2;
        energized = false;
    }

    public Wire(String name, Point p) {
        super(name, p);
        this.connections = new ArrayList<>();
        start = p;
        end = p;
        energized = false;
    }

    public void energize() {
        energized = true;
    }

    public void deEnergize() {
        energized = false;
    }

    public boolean isEnergized() {
        return energized;
    }

    public void connect(Component component) {
        connections.add(component);
    }

    public void setConnections(List<Component> connections) {
        this.connections = connections;
    }

    public List<Component> getConnections() {
        return connections;
    }

    public boolean isPointWire() {
        return start.equals(end);
    }

    public boolean isVerticalWire() {
        return start.getX() == end.getX() && start.getY() != end.getY();
    }

    @Override
    public List<Component> getAccessibleConnections() {
        energize();
        return connections;
    }

    @Override
    public ComponentIcon getComponentIcon() {
        WireIcon icon = ComponentIconCreator.getWireIcon(start, end);
        icon.setWireIconEnergyState(energized);
        icon.setComponentIconID(getId().toString());

        //double unitWidth = Math.max(0.5, start.differenceX(end) / GridScene.UNIT);
        //double unitHeight = Math.max(0.5, start.differenceY(end) / GridScene.UNIT);
        double unitWidth = start.differenceX(end) / GridVisualizer.UNIT;
        double unitHeight = start.differenceY(end) / GridVisualizer.UNIT;

        icon.setBoundingRect(getPosition(), unitWidth, unitHeight, 0.5, 0.5);
        return icon;
    }
}