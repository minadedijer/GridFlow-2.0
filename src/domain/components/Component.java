package domain.components;

import domain.geometry.Point;
import visualization.componentIcons.ComponentIcon;

import java.util.List;
import java.util.UUID;

public class Component {

    private UUID id;
    private String name;
    private Point position;
    private double angle;

    public Component(String name, Point position) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.position = position;
        this.angle = 0;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Point getPosition() {
        return position;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        if (angle % 90 != 0) return;
        this.angle = angle;
    }

    public ComponentIcon getComponentIcon() {
        throw new UnsupportedOperationException();
    }

    public List<Component> getAccessibleConnections() {
        return List.of();
    }

    public List<Component> getConnections() {
        return List.of();
    }

    public void delete() {
    }

}
